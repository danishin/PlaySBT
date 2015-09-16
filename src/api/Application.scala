package api

import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

object Application extends Controller {
  def index = Action { Ok(Json.arr(1,2,3)) }
}