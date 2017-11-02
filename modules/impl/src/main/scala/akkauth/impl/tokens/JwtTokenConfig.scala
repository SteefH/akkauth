package akkauth.impl.tokens

import com.typesafe.config.{Config, ConfigException}
import pdi.jwt.JwtAlgorithm
import pdi.jwt.algorithms.JwtHmacAlgorithm

import scala.concurrent.duration._

case class JwtTokenConfig[T](
    tokenLifetime: Duration,
    key: String,
    algorithm: JwtHmacAlgorithm
)

object JwtTokenConfig {

  def createAccessTokenConfig(config: Config): JwtTokenConfig[AccessToken] =
    create(config.getConfig("akkauth.tokens.accessToken"))

  def createRefreshTokenConfig(config: Config): JwtTokenConfig[RefreshToken] =
    create(config.getConfig("akkauth.tokens.refreshToken"))

  def create[T](config: Config): JwtTokenConfig[T] = {

    val tokenLifeTime = config.getDuration("tokenLifetime", java.util.concurrent.TimeUnit.SECONDS).seconds
    val key           = config.getString("key")
    val algorithm = JwtAlgorithm.fromString(config.getString("algorithm")) match {
      case a: JwtHmacAlgorithm =>
        a
      case _ =>
        throw new ConfigException.BadValue(
          config.origin(),
          "algorithm",
          "Algorithm should be one of HMD5, HS224, HS256, HS384, HS512"
        )
    }
    JwtTokenConfig[T](tokenLifeTime, key, algorithm)
  }
}
