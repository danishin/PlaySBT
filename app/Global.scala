import play.api.{Application, GlobalSettings, Logger}

//object AccessLoggingFilter extends Filter {
//  val accessLogger = Logger("access")
//
//  def apply(next: (RequestHeader) => Future[Result])(request: RequestHeader) = {
//    accessLogger.info(s"API request")
//
//    val resultFuture = next(request)
//
//    resultFuture.foreach(result => {
//      accessLogger.info(s"API Response")
//    })
//
//    resultFuture
//  }
//}

object Global extends GlobalSettings {
  override def onStart(app: Application): Unit = {
    Logger.info("Application has started")
  }

  override def onStop(app: Application): Unit = {
    Logger.info("Application has stopped")
  }
}



/*
* Filters wrap the action after the action has been looked up by the router.
* This means you cannot use a filter to transform a path, method, or query parameter to impact the router.
* However, you can direct the request to a different action by invoking that action directly from the filter.
* Though be aware that this will bypass the rest of the filter chain.
*
* If you do need to modify the request before the router is invoked, a better way to do this would be to place your logic in Global.onRouteRequest instead
* */

