package api

import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

case class Location(lat: Double, long: Double)
object Location {
  implicit val locationWrites = Json.writes[Location]
//  implicit val locationWrites = new Writes[Location] {
//    def writes(location: Location) = Json.obj(
//      "lat" -> location.lat,
//      "long" -> location.long
//    )
//  }
}


case class User(hobbies: List[String], location: Location)
object User {
  implicit val userWrites = Json.writes[User]

//  implicit val userWrites = new Writes[User] {
//    def writes(user: User) = Json.obj(
//      "hobbies" -> user.hobbies,
//      "location" -> user.location
//    )
//  }
}

class JSONController extends Controller {
  def get = Action {

    val user = User(
      List("programming", "workout"),
      Location(1, 2)
    )

    val json = Json.toJson(Location(1,2))

    val lat = (json \ "location" \ "lat").get
    val hobby = (json \ "hobbies")(1).get

    json.validate[String]


    Ok(Json.toJson(user))
  }
}