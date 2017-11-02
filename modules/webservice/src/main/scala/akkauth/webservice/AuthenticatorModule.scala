package akkauth.webservice

import java.util.UUID

import scala.concurrent.{ExecutionContext, Future}
import com.softwaremill.macwire._
import akkauth.{AccountId, Authenticator}
import akkauth.core.email.{EmailAccountRepository, EmailAuthenticator}
import akkauth.core.facebook.FacebookAuthenticator
import akkauth.core.facebook.algebras.{FacebookAssociationRepository, FacebookAuthClient}
import akkauth.impl.email.AkkaEmailAccountRepository
import akkauth.impl.facebook.{AkkaFacebookAssociationRepository, AkkaFacebookAuthClient}
import akkauth.impl.tokens.{AccessToken, RefreshToken}
import cats.instances.future._

trait AuthenticatorModule extends JwtTokenProviderModule {
  me: ActorSystemModule with ConfigModule with TimeProviderModule =>

  private implicit val executionContext: ExecutionContext = actorSystem.dispatcher

  lazy val accountIdGenerator: AccountId.Generator[Future] =
    () => Future.successful(AccountId(UUID.randomUUID()))

  private lazy val emailAssociations: EmailAccountRepository[Future] = wire[AkkaEmailAccountRepository]
  private lazy val emailAuthenticator: EmailAuthenticator[Future]    = wire[EmailAuthenticator[Future]]

  private lazy val facebookAssociations: FacebookAssociationRepository[Future] = wire[AkkaFacebookAssociationRepository]
  private lazy val facebookAuthClient: FacebookAuthClient[Future]              = wire[AkkaFacebookAuthClient]
  private lazy val facebookAuthenticator: FacebookAuthenticator[Future]        = wire[FacebookAuthenticator[Future]]

  lazy val authenticator: Authenticator[AccessToken, RefreshToken, Future] =
    wire[Authenticator[AccessToken, RefreshToken, Future]]
}
