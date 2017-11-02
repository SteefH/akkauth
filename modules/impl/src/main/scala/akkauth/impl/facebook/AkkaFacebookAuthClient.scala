package akkauth.impl.facebook

import akkauth.core.facebook.algebras.FacebookAuthClient
import akkauth.core.facebook.{FacebookLoginCode, FacebookUserId}

import scala.concurrent.Future

class AkkaFacebookAuthClient extends FacebookAuthClient[Future] {
  override def authenticate(loginCode: FacebookLoginCode): Future[FacebookUserId] = ???
}
