package akkauth.core.facebook

import cats._
import cats.implicits._
import akkauth.AccountId
import akkauth.core.facebook.algebras.{FacebookAssociationRepository, FacebookAuthClient}

/**
  * Business logic for authenticating a facebook user
  *
  * @param facebookAssociations
  * @param facebookAuthClient
  * @param generateAccountId
  * @tparam F
  */
class FacebookAuthenticator[F[_]: Monad](
    facebookAssociations: FacebookAssociationRepository[F],
    facebookAuthClient: FacebookAuthClient[F],
    generateAccountId: AccountId.Generator[F]
) {

  /**
    * Authenticate using a Facebook login code.
    *
    * If the Facebook user referred to by the login code has previously logged in, return their account id.
    * If this is the first time logging in for the Facebook user, register and return a newly generated account id.
    *
    *
    * @param facebookLoginCode
    * @return
    */
  final def authenticate(facebookLoginCode: FacebookLoginCode): F[AccountId] = {
    def associate(facebookUserId: FacebookUserId): F[AccountId] =
      for {
        accountId <- generateAccountId()
        _         <- facebookAssociations.associate(facebookUserId, accountId)
      } yield accountId

    for {
      facebookUserId    <- facebookAuthClient.authenticate(facebookLoginCode)
      possibleAccountId <- facebookAssociations.lookupAccountId(facebookUserId)
      accountId         <- possibleAccountId.map(Monad[F].pure) getOrElse associate(facebookUserId)
    } yield accountId
  }
  final def dissociate(facebookUserId: FacebookUserId): F[Unit] = {
    facebookAssociations.dissociate(facebookUserId)
  }
}
