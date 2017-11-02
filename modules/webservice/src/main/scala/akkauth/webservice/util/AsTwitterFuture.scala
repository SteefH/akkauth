package akkauth.webservice.util

object AsTwitterFuture {
  import scala.concurrent.ExecutionContext
  import scala.util.{Success, Failure}

  implicit class AsTwitterFutureSyntax[A](val future: scala.concurrent.Future[A]) extends AnyVal {
    def asTwitter(implicit e: ExecutionContext): com.twitter.util.Future[A] = {
      val p = new com.twitter.util.Promise[A]
      future.onComplete {
        case Success(value)     => p.setValue(value)
        case Failure(exception) => p.setException(exception)
      }

      p
    }
  }

}
