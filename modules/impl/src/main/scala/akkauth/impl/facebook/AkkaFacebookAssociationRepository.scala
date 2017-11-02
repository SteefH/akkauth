package akkauth.impl.facebook

import akka.actor.{ActorSystem, Props}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import akka.util.Timeout
import akka.pattern.ask
import akkauth.AccountId
import akkauth.core.facebook.FacebookUserId
import akkauth.core.facebook.algebras.FacebookAssociationRepository
import akkauth.impl.config.AkkaRepositoryConfig

import scala.concurrent.{ExecutionContext, Future}

class AkkaFacebookAssociationRepository(
    actorSystem: ActorSystem
) extends FacebookAssociationRepository[Future] {

  import AkkaFacebookAssociation.Command

  private val config = AkkaRepositoryConfig.create(
    actorSystem.settings.config.getConfig("akkauth.repositories.facebookAssociations")
  )

  implicit private val executionContext: ExecutionContext = actorSystem.dispatcher

  implicit private val askTimeout: Timeout = Timeout(config.askTimeout)

  override def associate(
      facebookUserId: FacebookUserId,
      id: AccountId
  ): Future[Unit] =
    (shardingRegion ? Command.Associate(facebookUserId, id)).map(_ => ())

  override def lookupAccountId(
      facebookUserId: FacebookUserId
  ): Future[Option[AccountId]] =
    (shardingRegion ? Command.LookupAccountId(facebookUserId))
      .mapTo[Option[AccountId]]

  override def dissociate(
      facebookUserId: FacebookUserId
  ): Future[Unit] =
    (shardingRegion ? Command.Dissociate(facebookUserId)).map(_ => ())

  private val shardingRegion = {
    val numberOfShards = config.numberOfShards

    ClusterSharding(actorSystem).start(
      typeName = "facebook-association",
      entityProps = Props[AkkaFacebookAssociation],
      settings = ClusterShardingSettings(actorSystem),
      extractEntityId = {
        case c: AkkaFacebookAssociation.Command =>
          (c.facebookUserId.value, c)
      },
      extractShardId = {
        case c: AkkaFacebookAssociation.Command =>
          (c.facebookUserId.value.hashCode % numberOfShards).toString
      }
    )
  }
}

object AkkaFacebookAssociationRepository {}
