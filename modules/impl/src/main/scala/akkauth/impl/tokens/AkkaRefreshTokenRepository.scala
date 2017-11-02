package akkauth.impl.tokens

import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import akka.util.Timeout
import akkauth.AccountId
import akkauth.core.tokens.algebras.TokenRepository
import akkauth.impl.config.AkkaRepositoryConfig

import scala.concurrent.{ExecutionContext, Future}

class AkkaRefreshTokenRepository(
    actorSystem: ActorSystem
) extends TokenRepository[RefreshToken, Future] {
  import AkkaRefreshTokensForAccountId._

  private val config = AkkaRepositoryConfig.create(
    actorSystem.settings.config.getConfig("akkauth.repositories.facebookAssociations")
  )

  implicit private val executionContext: ExecutionContext = actorSystem.dispatcher
  implicit private val askTimeout: Timeout                = config.askTimeout

  override def storeRefreshToken(accountId: AccountId, refreshToken: RefreshToken): Future[Unit] =
    (shardingRegion ? Command.Store(accountId, refreshToken)).map(_ => ())

  override def invalidateRefreshToken(accountId: AccountId, refreshToken: RefreshToken): Future[Boolean] =
    (shardingRegion ? Command.Invalidate(accountId, refreshToken)).mapTo[Boolean]

  private val shardingRegion = {

    ClusterSharding(actorSystem).start(
      typeName = "refreshtokens-for-account",
      entityProps = Props[AkkaRefreshTokensForAccountId],
      settings = ClusterShardingSettings(actorSystem),
      extractEntityId = {
        case c: AkkaRefreshTokensForAccountId.Command =>
          (c.accountId.value.toString, c)
      },
      extractShardId = {
        case c: AkkaRefreshTokensForAccountId.Command =>
          (c.accountId.value.hashCode % config.numberOfShards).toString
      }
    )
  }
}
