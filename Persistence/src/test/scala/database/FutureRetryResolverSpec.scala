package database

import org.scalatest.RecoverMethods.*
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpec

import scala.concurrent.Future
import scala.util.{Failure, Success}

class FutureRetryResolverSpec extends AsyncWordSpec with Matchers {
  // needed to prevent endless loop when blocking for Future Result (ScalaTest is using a different Execution Context)
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  "A FutureRetryResolver" should {
    val futureRetryResolver = new FutureRetryResolver

    "resolve blocking by use of a Future" in {
      val future = Future(1)
      val result = futureRetryResolver.resolveBlockingOnFuture(future)
      
      result should be (1)
    }

    "resolve non blocking by use of a Future" in {
      val future = Future(1)
      val resultFuture = futureRetryResolver.resolveNonBlockingOnFuture(future)
      
      resultFuture map {
        result => result should be (1)
      }
    }

    "resolve non blocking by use of a Future with Failures an the use of the retrys" in {
      val errorMessage = "Test Error"
      val failingFuture = Future.failed(new Throwable(errorMessage))
      val futureException = recoverToExceptionIf[RetryExceptionList] {
        futureRetryResolver.resolveNonBlockingOnFuture(failingFuture)
      }
      
      futureException map {
        exceptionList =>
          exceptionList.list.length should be (3)
          exceptionList.list.head._2.getMessage should be (errorMessage)
      }
    }
  }
}
