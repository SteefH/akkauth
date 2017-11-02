package akkauth

import akkauth.core.email.{EmailAddress, EmailAuthenticator, Password}
import akkauth.core.facebook.{FacebookAuthenticator, FacebookLoginCode}
import akkauth.core.tokens.{Token, TokenProvider}
import cats.Monad
import cats.implicits._

class Authenticator[AccessToken: Token, RefreshToken: Token, M[_]: Monad](
    facebookAuthenticator: FacebookAuthenticator[M],
    emailAuthenticator: EmailAuthenticator[M],
    tokenProvider: TokenProvider[AccessToken, RefreshToken, M]
) {

  type Tokens = (AccessToken, RefreshToken)

  private def tokensForAccountId(accountId: AccountId): M[Tokens] =
    tokenProvider.generateTokens(accountId)

  final def facebookAuthenticate(facebookLoginCode: FacebookLoginCode): M[Tokens] =
    facebookAuthenticator.authenticate(facebookLoginCode).flatMap(tokensForAccountId)

  final def emailAuthenticate(emailAddress: EmailAddress, password: Password): M[Tokens] =
    emailAuthenticator.authenticate(emailAddress, password).flatMap(tokensForAccountId)

  final def registerEmailAccount(emailAddress: EmailAddress, password: Password): M[Unit] =
    emailAuthenticator.register(emailAddress, password).map(_ => ())

  final def refresh(refreshToken: RefreshToken): M[Option[Tokens]] =
    tokenProvider.refresh(refreshToken)
}
