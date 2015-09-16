import play.api.GlobalSettings
import play.api.mvc.{Handler, RequestHeader}
import api._

object Global extends GlobalSettings {
  override def onRouteRequest(req: RequestHeader): Option[Handler] = {
    (req.method, req.path) match {
      case ("GET", "/") => Some(Application.index)
      case _ => None
    }
  }
}
