package akkauth.webservice

import akkauth.core.tokens.algebras.TokenRepository
import akkauth.core.tokens.{TokenFactory, TokenProvider}
import akkauth.impl.tokens._
import com.softwaremill.macwire._

import scala.concurrent.{ExecutionContext, Future}
import cats.instances.future._

trait JwtTokenProviderModule extends TokenInstances {
  me: ActorSystemModule with ConfigModule with TimeProviderModule =>

  private implicit val executionContext: ExecutionContext = actorSystem.dispatcher

  lazy implicit val accessTokenConfig: JwtTokenConfig[AccessToken]   = JwtTokenConfig.createAccessTokenConfig(config)
  lazy implicit val refreshTokenConfig: JwtTokenConfig[RefreshToken] = JwtTokenConfig.createRefreshTokenConfig(config)

  lazy val accessTokenFactory: TokenFactory[AccessToken, Future]   = wire[JwtTokenFactory[AccessToken, Future]]
  lazy val refreshTokenFactory: TokenFactory[RefreshToken, Future] = wire[JwtTokenFactory[RefreshToken, Future]]
  lazy val tokenRepository: TokenRepository[RefreshToken, Future]  = wire[AkkaRefreshTokenRepository]

  lazy val tokenProvider: TokenProvider[AccessToken, RefreshToken, Future] =
    wire[TokenProvider[AccessToken, RefreshToken, Future]]
}
