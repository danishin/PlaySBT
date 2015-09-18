package api

import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.Results._
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future

class App extends Controller {
  def index(name: String, num: Int) = Action { request =>
    Ok(Json.arr(name, num, 2,3)).as(JSON)
  }

  def post() = Logging {
    Action(parse.json(maxLength = 100)) { request =>
      Ok(request.body)
    }
  }

  def compose(name: String) = ComposedAction {
    Ok(name)
  }
}

case class Logging[A](action: Action[A]) extends Action[A] {
  def apply(request: Request[A]): Future[Result] = {
    Logger.info("calling action")
    action(request)
  }

  lazy val parser = action.parser
}

/* Action Composition using Action Builder */
object Middleware {
  def logging[A](action: Action[A]) = Action.async(action.parser) { request =>
    Logger.info("Calling Action")
    action(request)
  }

  def authenticate[A](action: Action[A]) = Action.async(action.parser) { request =>
    request.getQueryString("name")
      .collect({ case "daniel" => action(request) })
      .getOrElse(Future.successful(Forbidden("Not Authenticated")))
  }
}

object ComposedAction extends ActionBuilder[Request] {
  def invokeBlock[A](request: Request[A], block: (Request[A] => Future[Result])) = {
    block(request)
      .map(_.withHeaders("X-UA-Compatible" -> "Chrome=1"))
  }

  override def composeAction[A](action: Action[A]) =
    Middleware.logging(Middleware.authenticate(action))
}

/*
*
* While action composition allows you to perform additional processing at the HTTP request and response level,
* often you want to build pipelines of data transformations asfdthat add context to or perform validation on the request itself.
*
* ActionFunction can be thought of as a function on the request, paramterized over both the input request type and the output type passed on to the next layer
* Each action function may represent modular processing such as authentication, database lookups for objects, permission checks, or other operations that you wish to compose and reuse across actions
* */

/* Action Transformer */
// Unconditional
class UserRequest[A](val name: Option[String], request: Request[A]) extends WrappedRequest[A](request)

object UserAction extends ActionBuilder[UserRequest] with ActionTransformer[Request, UserRequest] {
  def transform[A](request: Request[A]) = Future.successful {
    new UserRequest(request.getQueryString("name"), request)
  }
}

/*
* ActionTransformer - change the request, for example, by adding additional information
* ActionFilter - selectively intercept requests, for example to produce errors, without changing the request value
* ActionRefiner - the general case of both of the above
* ActionBUilder - the speical case of functions that take Request as input and thus can build actions
*
* You can also define your own arbitrary ActionFunction by implementing the invokeBlock method. Often it is convenient to make the input and output types instances of Request(using WrappedRequest) but this is not strictly necessary
* */

/* Adding Information to requests */
// Conditional
class Item
class ItemRequest[A](val item: Item, request: UserRequest[A]) extends WrappedRequest[A](request) {
  def name = request.name
}

object Refiner {
  def ItemAction(itemId: Int) = new ActionRefiner[UserRequest, ItemRequest] {
    def refine[A](input: UserRequest[A]) = Future.successful {
      Either.cond(itemId == 1, new ItemRequest(new Item, input), NotFound)
    }
  }
}

/* Validating Requests */
object PermissionCheckAction extends ActionFilter[ItemRequest] {
  def filter[A](input: ItemRequest[A]) = Future.successful {
    if (false) {
      Some(Forbidden)
    } else {
      None
    }
  }
}

/* Putting it all together */
object All {
  def tagItem(itemId: Int, tag: String) =
    (UserAction andThen Refiner.ItemAction(itemId) andThen PermissionCheckAction) { request =>
      Ok("Success")
    }
}







