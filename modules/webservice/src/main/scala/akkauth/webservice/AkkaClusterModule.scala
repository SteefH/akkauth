package akkauth.webservice

import akka.cluster.Cluster

trait AkkaClusterModule {
  me: ActorSystemModule =>

  val cluster = Cluster(actorSystem)
//  if (actorSystem.settings.setup)
}
