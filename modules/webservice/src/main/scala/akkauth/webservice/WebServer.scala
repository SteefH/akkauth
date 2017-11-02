package akkauth.webservice

import akkauth.core.facebook.FacebookLoginCode
import io.finch._
import io.finch.circe._
import io.circe.generic.auto._
import akkauth.webservice.util.AsTwitterFuture._
import com.typesafe.config.Config

import scala.concurrent.ExecutionContextExecutor
//import shapeless._
import akkauth.core.tokens.Token.ops._

object WebServer
    extends App
    with AuthenticatorModule
    with ActorSystemModule
    with AkkaClusterModule
    with ConfigModule
    with TimeProviderModule {

  override def config: Config = actorSystem.settings.config

  implicit private val executionContext: ExecutionContextExecutor = actorSystem.dispatcher

  case class FacebookCredentials(code: String)
  case class EmailCredentials(emailAddress: String, password: String)

  case class Tokens(accessToken: String, refreshToken: String, expiresIn: Long)

  val facebookAuthentication: Endpoint[Tokens] =
    post("facebook" :: jsonBody[FacebookCredentials]) mapAsync { c =>
      authenticator
        .facebookAuthenticate(FacebookLoginCode(c.code))
        .map {
          case (accessToken, refreshToken) =>
            Tokens(accessToken.tokenString,
                   refreshToken.tokenString,
                   accessToken.expiresAt.map(_ - timeProvider.now().getEpochSecond).get)
        }
        .asTwitter
    }
}
