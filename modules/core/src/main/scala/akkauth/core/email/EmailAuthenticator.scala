package akkauth.core.email

import akkauth.AccountId
import cats.Monad
import cats.implicits._

class EmailAuthenticator[M[_]: Monad](
    emailAssociations: EmailAccountRepository[M],
    generateAccountId: AccountId.Generator[M]
) {
  final def authenticate(email: EmailAddress, password: Password): M[AccountId] =
    emailAssociations.lookupByEmailAndPassword(email, password)

  final def register(email: EmailAddress, password: Password): M[AccountId] =
    for {
      accountId <- generateAccountId()
      _         <- emailAssociations.registerEmailAccount(accountId, email, password)
    } yield accountId

}
