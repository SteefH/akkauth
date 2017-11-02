package akkauth

import java.util.UUID

final case class AccountId(value: UUID) extends AnyVal
object AccountId {
  type Generator[F[_]] = () => F[AccountId]
}
