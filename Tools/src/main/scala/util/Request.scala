package util

/**
 * Request.scala
 * Implements the Chain of Responsibility Pattern to analyze user input
 */

/**
 * Request
 *
 * A sealed abstract class that serves as the base class for different types of requests.
 * Implementations should extend this class to define specific request types.
 */
sealed abstract class Request

/**
 * A case class representing a single-character request.
 *
 * @param userinput The user input associated with the request.
 */
case class SingleCharRequest(userinput: String) extends Request

/**
 * A case class representing a multi-character request.
 *
 * @param userinput The user input associated with the request.
 */
case class  MultiCharRequest(userinput: String) extends Request

/**
 * A case class representing a response to a request.
 *
 * @param req     The original request.
 * @param handled A boolean flag indicating whether the request has been handled.
 */
case class Response(req: Request, handled: Boolean)