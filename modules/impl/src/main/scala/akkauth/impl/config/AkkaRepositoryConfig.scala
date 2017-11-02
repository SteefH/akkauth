package akkauth.impl.config

import java.util.concurrent.TimeUnit

import com.typesafe.config.Config

import scala.concurrent.duration._

case class AkkaRepositoryConfig(askTimeout: FiniteDuration, numberOfShards: Long)
object AkkaRepositoryConfig {
  def create(config: Config): AkkaRepositoryConfig = {
    AkkaRepositoryConfig(
      config.getDuration("askTimeout0", TimeUnit.SECONDS).seconds,
      config.getLong("persistentActorShardCount")
    )
  }
}
