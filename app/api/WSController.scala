package api

import java.io.FileOutputStream
import javax.inject.Inject

import play.api.libs.iteratee.Iteratee
import play.api.libs.json.Json
import play.api.libs.ws.{WSResponse, WSClient}
import play.api.mvc.{Action, Controller}

import scala.concurrent.Future

case class Person(name: String, age: Int)
object Person { implicit val personReads = Json.reads[Person] }

class WSController @Inject() (ws: WSClient) extends Controller {
  implicit val context = play.api.libs.concurrent.Execution.Implicits.defaultContext

  val get = ws.url("https://google.com")
    .withHeaders("Accept" -> "application/json")
    .withRequestTimeout(10000)
    .withQueryString("search" -> "play")
    .withFollowRedirects(true)
    .get()

  val result1 = ws.url("example.com")
    .post(Json.arr(1,2))
    .map(response => (response.json \ "person").validate[Person])

  val futureResponse = ws.url("")
    .getStream()
    .flatMap {
    case (headers, body) =>
      body |>>> Iteratee.fold(0L) { (total, bytes) => total + bytes.length }
  }

  val downloadedFile = ws.url("").getStream()
    .flatMap {
    case (headers, body) =>
      val file = ""
      val outputStream = new FileOutputStream(file)
      // The iteratee that writes to the ouput stream
      val iteratee = Iteratee.foreach[Array[Byte]] { bytes => outputStream.write(bytes) }

      // Feed the body into the iteratee
      (body |>>> iteratee).andThen {
        case result =>
          // Close the output stream whether there was an error or not
          outputStream.close()
          // Get the result or rethrow the error
          result.get
      }.map(_ => file)
  }

  def downloadFile = Action.async {
    ws.url("").getStream().map {
      case (response, body) =>
        if (response.status == 200) {
          val contentType = response.headers.get("Content-Type")
            .flatMap(_.headOption)
            .getOrElse("application/octet-stream")

          response.headers.get("Content-Length") match {
            case Some(Seq(length)) =>
              Ok.feed(body).as(contentType).withHeaders("Content-Length" -> length)
            case _ =>
              Ok.chunked(body).as(contentType)
          }
        } else {
          BadGateway
        }
    }
  }

  val res: Future[WSResponse] = for {
    response1 <- ws.url("").get()
    response2 <- ws.url(response1.body).get()
    response3 <- ws.url(response2.body).get()
  } yield response3

  res.recover { case e: Exception => e.printStackTrace() }
















}
