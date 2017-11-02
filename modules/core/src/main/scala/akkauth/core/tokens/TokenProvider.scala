package akkauth.core.tokens

import akkauth.AccountId
import akkauth.core.tokens.algebras.TokenRepository
import cats.Monad
import cats.data.OptionT
import cats.implicits._

class TokenProvider[AccessToken: Token, RefreshToken: Token, M[_]: Monad](
    accessTokenFactory: TokenFactory[AccessToken, M],
    refreshTokenFactory: TokenFactory[RefreshToken, M],
    tokenRepository: TokenRepository[RefreshToken, M]
) {

  type Tokens = (AccessToken, RefreshToken)

  final def generateTokens(accountId: AccountId): M[Tokens] =
    for {
      accessToken  <- accessTokenFactory.createToken(accountId)
      refreshToken <- refreshTokenFactory.createToken(accountId)
      _            <- tokenRepository.storeRefreshToken(accountId, refreshToken)
    } yield (accessToken, refreshToken)

  final def refresh(refreshToken: RefreshToken): M[Option[Tokens]] = {
    import Token.ops._
    if (refreshToken.isValid) {
      val asOptionT = for {
        accountId             <- OptionT.fromOption[M](refreshToken.accountId)
        invalidationSucceeded <- OptionT.liftF(tokenRepository.invalidateRefreshToken(accountId, refreshToken))
        newTokens             <- if (invalidationSucceeded) OptionT.none[M, Tokens] else OptionT.liftF(generateTokens(accountId))
      } yield newTokens
      asOptionT.value
    } else {
      Monad[M].pure(None)
    }
  }

}
