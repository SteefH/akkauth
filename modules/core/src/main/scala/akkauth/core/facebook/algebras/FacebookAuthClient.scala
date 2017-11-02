package akkauth.core.facebook.algebras

import akkauth.core.facebook.{FacebookLoginCode, FacebookUserId}

/**
  * Tagless final DSL for calling the Facebook authentication API.
  *
  * [[https://www.beyondthelines.net/programming/introduction-to-tagless-final/ Tagless final explained]]
  *
  * @tparam M Wrapper type for operation results.
  *           For instance [[scala.concurrent.Future]] when used in a real-world application,
  *           [[cats.Id]] when used in unit testing.
  */
trait FacebookAuthClient[M[_]] {

  /**
    * Exchange a Facebook login code for a Facebook user id
    * @param loginCode The facebook login code
    * @return
    */
  def authenticate(loginCode: FacebookLoginCode): M[FacebookUserId]
}
