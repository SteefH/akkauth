package akkauth.webservice

import akka.actor.ActorSystem
import akka.cluster.Cluster

trait ActorSystemModule {
  lazy val actorSystem: ActorSystem = {
    val actorSystem = ActorSystem("akkauth")

    val cluster = Cluster(actorSystem)
    if (cluster.settings.SeedNodes.isEmpty) {
      cluster.join(cluster.selfAddress)
    }
    actorSystem
  }

}
