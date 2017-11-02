package akkauth.impl.tokens

import java.util.UUID

import akkauth.AccountId
import akkauth.core.tokens.Token
import pdi.jwt.JwtCirce

trait TokenInstances {
  private def tokenInstance[T](
      tokenToString: T => String,
      stringToToken: String => T
  )(
      implicit
      jwtTokenConfig: JwtTokenConfig[T],
      timeProvider: TimeProvider
  ): Token[T] =
    new Token[T] {

      /**
        * Extract the account ID from a token.
        *
        * @param token The token to get the account id from
        * @return `Some(accountId)` if the token is valid and contains a certain account ID, `None` otherwise
        */
      override def accountId(token: T): Option[AccountId] = {

        for {
          claim     <- JwtCirce.decode(asString(token), jwtTokenConfig.key, Seq(jwtTokenConfig.algorithm)).toOption
          json      <- io.circe.jawn.parse(claim.content).toOption
          accountId <- json.hcursor.get[UUID]("accountId").toOption
        } yield AccountId(accountId)
      }

      override def fromString(tokenString: String): T = stringToToken(tokenString)

      override def asString(token: T) = tokenToString(token)

      override def isValid(token: T): Boolean = {
        def isInTimeRange(start: Option[Long], end: Option[Long]): Boolean = {
          val now = timeProvider.now().getEpochSecond
          start.forall(_ >= now) && end.forall(_ < now)
        }
        val result = for {
          claim <- JwtCirce.decode(asString(token), jwtTokenConfig.key, Seq(jwtTokenConfig.algorithm)).toOption
          if isInTimeRange(claim.notBefore, claim.expiration)
        } yield true
        result getOrElse false
      }

      override def expiresAt(token: T): Option[Long] = ???

    }

  implicit def accessTokenInstance(
      implicit
      jwtTokenConfig: JwtTokenConfig[AccessToken],
      timeProvider: TimeProvider
  ): Token[AccessToken] =
    tokenInstance[AccessToken](_.tokenString, AccessToken)

  implicit def refreshTokenInstance(
      implicit
      jwtTokenConfig: JwtTokenConfig[RefreshToken],
      timeProvider: TimeProvider
  ): Token[RefreshToken] =
    tokenInstance[RefreshToken](_.tokenString, RefreshToken)

}

object instances extends TokenInstances
