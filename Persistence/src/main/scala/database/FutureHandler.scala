package database

import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import concurrent.duration.DurationInt

/**
 * The FutureHandler class provides utility methods for handling and resolving Futures with retry functionality.
 *
 * @constructor Create a new FutureHandler instance.
 */
class FutureHandler {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
  val logger: Logger = LoggerFactory.getLogger(this.getClass)

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
  def resolveNonBlockingOnFuture[T](futureToResolve: Future[T], numOfRetries: Int = 3): Future[T] = {
    retry(numOfRetries, RetryExceptionList(Vector.empty)) {
      () => futureToResolve
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
  //Retry using foldLeft
  private def retry[T](numOfRetries: Int, exceptionList: RetryExceptionList)(operation: () => Future[T]): Future[T] = {
    if (numOfRetries == 0)
      println(s"Retry failed finaly with exception: [${exceptionList.list}]")
      Future.failed(exceptionList)
    else {
      val retryAttempts = Iterator.fill(numOfRetries)(operation)
      val failed = Future.failed(exceptionList).asInstanceOf[Future[T]]
      retryAttempts.foldLeft(failed) { (resultFuture, operation) =>
        resultFuture.recoverWith { case _ =>
          val delay = 10.milliseconds
          Thread.sleep(delay.toMillis)
          logger.info(s"Error occurred on request, with try number[$numOfRetries], Retry after [$delay] [ms]")
          retry(numOfRetries - 1, exceptionList.copy(exceptionList.list :+ (numOfRetries, new Exception))) {
            operation
          }
        }
      }
    }
  }

  /**
   * RetryExceptionList is an exception class used to encapsulate a list of retry attempts along with their corresponding exceptions.
   *
   * @param list A Vector of tuples representing the retry attempt number and the associated exception.
   */
  private case class RetryExceptionList(list: Vector[(Int, Throwable)]) extends Exception
}