package api

import play.api.http.HttpErrorHandler
import play.api.mvc.{Result, RequestHeader}
import play.api.mvc.Results._

import scala.concurrent.Future

class ErrorHandler extends HttpErrorHandler {
  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    // Log these errors here?
    Future.successful(Status(statusCode))
  }

  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    // Log these errors here?
    Future.successful(InternalServerError)
  }
}
