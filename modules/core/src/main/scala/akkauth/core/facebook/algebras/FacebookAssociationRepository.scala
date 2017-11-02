package akkauth.core.facebook.algebras

import akkauth.AccountId
import akkauth.core.facebook.FacebookUserId

/**
  * Tagless final algebra for Facebook account associations.
  *
  * [[https://www.beyondthelines.net/programming/introduction-to-tagless-final/ Tagless final explained]]
  *
  * @tparam M Wrapper type for operation results.
  *           For instance [[scala.concurrent.Future]] when used in a real-world application,
  *           [[cats.Id]] when used in unit testing.
  */
trait FacebookAssociationRepository[M[_]] {

  /**
    * Associate a Facebook user id with an application account id.
    *
    * @param facebookUserId   The Facebook user id to associate
    * @param accountId        The account id to associate with
    * @return                 Returns `M[Unit]`
    */
  def associate(facebookUserId: FacebookUserId, accountId: AccountId): M[Unit]

  /**
    * Dissociate (ie forget) a Facebook user id.
    *
    * @param facebookUserId   The Facebook user id to dissociate
    * @return                 Returns `M[Unit]`
    */
  def dissociate(facebookUserId: FacebookUserId): M[Unit]

  /**
    * Lookup an [[akkauth.AccountId]] associated with the given `facebookUserId`.
    *
    * @param facebookUserId   The Facebook user id to lookup the associated account id.
    *                         for.
    * @return                 Returns a value of type `M[Some[AccountId]]` when an associated account id was found,
    *                         `M[None]` otherwise.
    */
  def lookupAccountId(facebookUserId: FacebookUserId): M[Option[AccountId]]

}
