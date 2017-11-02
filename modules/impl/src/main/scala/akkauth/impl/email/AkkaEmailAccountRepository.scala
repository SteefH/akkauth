package akkauth.impl.email

import akkauth.AccountId
import akkauth.core.email.{EmailAddress, EmailAccountRepository, Password}

import scala.concurrent.Future

class AkkaEmailAccountRepository() extends EmailAccountRepository[Future] {
  override def lookupByEmailAndPassword(email: EmailAddress, password: Password): Future[AccountId] = ???

  override def registerEmailAccount(accountId: AccountId, email: EmailAddress, password: Password): Future[Unit] =
    ???

}
