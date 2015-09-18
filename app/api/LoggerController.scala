package api

import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.Future

trait APILoggable extends Controller {
  val logger = Logger(this.getClass)

  private val apiLogger = Logger("API")
  object APILoggingAction extends ActionBuilder[Request] {
    override def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]): Future[Result] = {
      apiLogger.info(s"method=${request.method} uri=${request.uri} remote-address=${request.remoteAddress}")
      block(request)
    }
  }
}

class LoggerController extends Controller with APILoggable {
  def index = APILoggingAction {
    logger.info("HI")
    Ok(Json.arr(1,2))
  }
}
