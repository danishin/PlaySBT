import javax.inject.Inject

import play.api.Logger
import play.api.http.HttpFilters
import play.api.mvc.{Filter, RequestHeader, Result}
import play.api.routing.Router.Tags
import play.filters.gzip.GzipFilter

import scala.concurrent.Future

class LogTimeFilter extends Filter {
  def apply(next: RequestHeader => Future[Result])(requestHeader: RequestHeader) = {
    val startTime = System.currentTimeMillis()

    next(requestHeader).map { result =>
      val action = requestHeader.tags(Tags.RouteController) + "." + requestHeader.tags(Tags.RouteActionMethod)
      val requestTime = System.currentTimeMillis() - startTime

      Logger.info(s"$action took $requestTime ms and returned ${result.header.status}")

      result.withHeaders("Request-Time" -> requestTime.toString)
    }
  }
}

class Filters @Inject() (gzip: GzipFilter, logTime: LogTimeFilter) extends HttpFilters {
  val filters = Seq(gzip, logTime)
}
