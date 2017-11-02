package akkauth.impl.facebook

import akka.Done
import akka.persistence.PersistentActor
import akkauth.AccountId
import akkauth.core.facebook.FacebookUserId

class AkkaFacebookAssociation extends PersistentActor {
  import AkkaFacebookAssociation._
  import Command._
  import Event._

  val persistenceId = s"facebookaccount-${self.path.name}"

  override def receiveCommand: Receive = notAssociated

  // States
  private def notAssociated: Receive = {
    case Associate(facebookUserId: FacebookUserId, accountId: AccountId) =>
      persist(Associated(facebookUserId, accountId)) { _ =>
        context become associated(accountId)
        sender() ! Done
      }
    case LookupAccountId(_) =>
      sender() ! None
    case Dissociate(_) =>
      sender() ! Done
  }

  private def associated(accountId: AccountId): Receive = {
    case Dissociate(facebookUserId) =>
      persist(Dissociated(facebookUserId)) { _ =>
        context become notAssociated
        sender() ! Done
      }
    case LookupAccountId(_) =>
      sender() ! Option(accountId)
  }

  override def receiveRecover: Receive = {
    case Associated(_, accountId) =>
      context become associated(accountId)
    case Dissociated(_) =>
      context become notAssociated
  }

}

object AkkaFacebookAssociation {
  sealed trait Command {
    def facebookUserId: FacebookUserId
  }

  object Command {
    case class Associate(facebookUserId: FacebookUserId, accountId: AccountId) extends Command
    case class LookupAccountId(facebookUserId: FacebookUserId)                 extends Command
    case class Dissociate(facebookUserId: FacebookUserId)                      extends Command
  }

  sealed trait Event
  object Event {
    case class Associated(facebookUserId: FacebookUserId, accountId: AccountId) extends Event
    case class Dissociated(facebookUserId: FacebookUserId)                      extends Event
  }
}
