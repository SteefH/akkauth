package akkauth.webservice
import java.time.Instant

import akkauth.impl.tokens.TimeProvider

trait TimeProviderModule {
  implicit lazy val timeProvider: TimeProvider = new TimeProvider {
    override def now(): Instant = Instant.now()
  }
}
