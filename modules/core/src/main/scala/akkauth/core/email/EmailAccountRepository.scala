package akkauth.core.email

import akkauth.AccountId

trait EmailAccountRepository[M[_]] {
  def lookupByEmailAndPassword(email: EmailAddress, password: Password): M[AccountId]
  def registerEmailAccount(accountId: AccountId, email: EmailAddress, password: Password): M[Unit]
}
