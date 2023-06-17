package database

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import concurrent.duration.DurationInt
import scala.util.{Failure, Success}

/**
 * The FutureRetryResolver class provides utility methods for handling and resolving Futures with retry functionality.
 *
 * @constructor Create a new FutureRetryResolver instance.
 */
class FutureRetryResolver {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global

  /**
   * Resolves a Future in a blocking manner, waiting for its completion.
   *
   * @param futureToResolve The Future to be resolved.
   * @param duration        The maximum duration to wait for the Future to complete. Defaults to Duration.Inf (infinite duration).
   * @tparam T The type of the Future's result.
   * @return The result of the resolved Future.
   */
  def resolveBlockingOnFuture[T](futureToResolve: Future[T], duration: Duration = Duration.Inf): T =
    Await.result(futureToResolve, duration)

  /**
   * Resolves a Future in a non-blocking manner with retry functionality.
   *
   * @param futureToResolve The Future to be resolved.
   * @param numOfRetries    The maximum number of retries in case of failure. Defaults to 3.
   * @tparam T The type of the Future's result.
   * @return A Future that resolves to the result of the resolved Future, with retry functionality.
   */
  def resolveNonBlockingOnFutureFL[T](futureToResolve: Future[T], numOfRetries: Int = 3): Future[T] = {
    retryFoldLeft(numOfRetries, RetryExceptionList(Vector.empty)) {
      () => futureToResolve
    }
  }

  def resolveNonBlockingOnFuture[T](futureToResolve: Future[T], numOfRetries: Int = 3): Future[T] = {
    retry(numOfRetries, RetryExceptionList(Vector.empty)) {
      futureToResolve
    }
  }

  /**
   * Private helper method (with using foldLeft) for retrying a Future operation in case of failure.
   *
   * @param numOfRetries  The remaining number of retries.
   * @param exceptionList The list of exceptions encountered during retries.
   * @param operation     The operation to be retried as a by-name parameter.
   * @tparam T The type of the Future's result.
   * @return A Future that resolves to the result of the retried operation.
   */
  //Retry using foldLeft (has a problem and doesn't work properly)
  private def retryFoldLeft[T](numOfRetries: Int, exceptionList: RetryExceptionList)(operation: () => Future[T]): Future[T] = {
    if (numOfRetries == 0)
      println(s"Retry failed finaly with exception: [${exceptionList.list.toString()}]")
      Future.failed(exceptionList)
    else {
      val retryAttempts = Iterator.fill(numOfRetries)(operation)
      val failed = Future.failed(exceptionList).asInstanceOf[Future[T]]
      retryAttempts.foldLeft(failed) { (resultFuture, operation) =>
        resultFuture.recoverWith { case _ =>
          val delay = 10.milliseconds
          Thread.sleep(delay.toMillis)
          println(s"Error occurred on request, with try number[$numOfRetries], Retry after [$delay] [ms]")

          retryFoldLeft(numOfRetries - 1, exceptionList.copy(exceptionList.list :+ (numOfRetries, new Exception))) {
            operation
          }
        }
      }
    }
  }

  /**
   * Private helper method for retrying a Future operation in case of failure.
   *
   * @param numOfRetries  The remaining number of retries.
   * @param exceptionList The list of exceptions encountered during retries.
   * @param operation     The operation to be retried as a by-name parameter.
   * @tparam T The type of the Future's result.
   * @return A Future that resolves to the result of the retried operation.
   */
  private def retry[T](numOfRetries: Int, exceptionList: RetryExceptionList)(operation: => Future[T]): Future[T] = {
    if (numOfRetries == 0)
      println(s"Retry failed finally with exception: [${exceptionList.list.toString()}]")
      Future.failed(exceptionList)
    else
      operation transformWith {
        case Success(value) => Future(value)
        case Failure(exception) =>
          val delay = 10.milliseconds
          Thread.sleep(delay.toMillis)
          println(s"Error occurred on request, with try number[$numOfRetries], Retry after [$delay] [ms]")
          retry(numOfRetries - 1, exceptionList.copy(exceptionList.list :+ (numOfRetries, exception))) {
            operation
          }
      }
  }

}

/**
 * RetryExceptionList is an exception class used to encapsulate a list of retry attempts along with their corresponding exceptions.
 *
 * @param list A Vector of tuples representing the retry attempt number and the associated exception.
 */
case class RetryExceptionList(list: Vector[(Int, Throwable)]) extends Exception