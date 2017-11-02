package akkauth.impl.tokens

import akkauth.AccountId
import akkauth.core.tokens.{Token, TokenFactory}
import cats.Monad
import pdi.jwt.{JwtCirce, JwtClaim}

class JwtTokenFactory[T: Token, M[_]: Monad](
    config: JwtTokenConfig[T],
    timeProvider: TimeProvider
) extends TokenFactory[T, M] {

  override def createToken(accountId: AccountId): M[T] = {
    val claim = JwtClaim(
      expiration = Some(timeProvider.now().plusSeconds(config.tokenLifetime.toSeconds).getEpochSecond),
      issuedAt = Some(timeProvider.now().getEpochSecond)
    ) + ("accountId", accountId)

    Monad[M].pure(Token[T].fromString(JwtCirce.encode(claim)))
  }
}
