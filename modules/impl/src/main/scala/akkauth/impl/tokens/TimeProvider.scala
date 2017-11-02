package akkauth.impl.tokens

import java.time.Instant

trait TimeProvider {
  def now(): Instant
}
