package akkauth.impl.tokens

import akka.Done
import akka.persistence.PersistentActor
import akkauth.AccountId
import akkauth.impl.tokens.AkkaRefreshTokensForAccountId.Command.{Invalidate, Store}
import akkauth.impl.tokens.AkkaRefreshTokensForAccountId.Event.{RefreshTokenInvalidated, RefreshTokenStored}

class AkkaRefreshTokensForAccountId extends PersistentActor {
  import AkkaRefreshTokensForAccountId._
  override def persistenceId = s"refreshtokens-for-account-${self.path.name}"

  override def receiveCommand: Receive = withTokens(Set.empty)

  private def withTokens(tokens: Set[RefreshToken]): Receive = {
    case c: Command =>
      c match {
        case Store(accountId, refreshToken) =>
          persist(Event.RefreshTokenStored(accountId, refreshToken)) { _ =>
            context become withTokens(tokens + refreshToken)
            sender() ! Done
          }
        case Invalidate(accountId, refreshToken) =>
          if (tokens contains refreshToken) {
            persist(Event.RefreshTokenInvalidated(accountId, refreshToken)) { _ =>
              context become withTokens(tokens - refreshToken)
              sender() ! true
            }
          } else {
            sender() ! false
          }
      }
  }

  override def receiveRecover: PartialFunction[Any, Unit] = {
    val tokenSet = scala.collection.mutable.Set.empty[RefreshToken]

    {
      case e: Event =>
        e match {
          case RefreshTokenStored(_, refreshToken) =>
            tokenSet += refreshToken
            context become withTokens(tokenSet.toSet)
          case RefreshTokenInvalidated(_, refreshToken) =>
            tokenSet -= refreshToken
            context become withTokens(tokenSet.toSet)
        }
    }
  }

}
object AkkaRefreshTokensForAccountId {
  sealed trait Command {
    def accountId: AccountId
  }
  object Command {
    final case class Store(accountId: AccountId, refreshToken: RefreshToken)      extends Command
    final case class Invalidate(accountId: AccountId, refreshToken: RefreshToken) extends Command
  }
  sealed trait Event
  object Event {
    final case class RefreshTokenStored(accountId: AccountId, refreshToken: RefreshToken)      extends Event
    final case class RefreshTokenInvalidated(accountId: AccountId, refreshToken: RefreshToken) extends Event
  }
}
