package api

import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.Results._
import play.api.mvc._

import scala.concurrent.Future
import scalaz.\/
import scalaz.Scalaz._

class Auth extends Controller {
  def index = Action.async { request =>
//    val d: List[Future] = List(Future(1), Future(2))

    List(Future.successful(1), Future.successful(2), Future.successful(3))

    Ok(Json.arr(1,2))

    Future.successful(Forbidden)
  }
}

class AuthenticatedRequest[A](val user_id: Int, request: Request[A]) extends WrappedRequest[A](request)

object Authenticated extends ActionBuilder[AuthenticatedRequest] {
  type Box[+T] = Result \/ T

  val m1 = Map(
    1 -> List("a", "b"),
    2 -> List("aa", "bb")
  )

  val m2 = Map(
    1 -> List("z"),
    3 -> List("yyy", "zzz")
  )

  m1 |+| m2

  override def invokeBlock[A](request: Request[A], block: (AuthenticatedRequest[A]) => Future[Result]): Future[Result] = {
    log(request)

    checkHTTPS(request)
      .flatMap(authenticate)
      .map(block)
      .fold(Future.successful, { a => a })
  }

  private def log[A](request: Request[A]): Unit = {
    Logger.info("Calling Action")
  }

  private def checkHTTPS[A](request: Request[A]): Box[Request[A]] = {
    request.headers.get("X-Forwarded-Proto")
      .collect { case "https" => request }
      .toRightDisjunction(Forbidden)
  }

  private def authenticate[A](request: Request[A]): Box[AuthenticatedRequest[A]] = {
    request.getQueryString("name")
      .collect { case "daniel" => new AuthenticatedRequest(1, request) }
      .toRightDisjunction(Forbidden)
  }
}
