package api

import org.joda.time.DateTime
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.mvc._

object Persistence {
  def save(userData: UserData) = 1
}

case class UserData(name: String, email: Option[String], age: Int, date: DateTime, hobbies: List[String]) {
  def id = 1
  def dateString = date.toString
}

class UserFormController extends Controller {
  val userForm = Form(
    mapping(
    "name" -> nonEmptyText,
    "email" -> optional(email.verifying(nonEmpty)),
    "age" -> default(number(min = 0, max = 100), 10),
    "date" -> jodaDate.verifying(_.isBeforeNow),
    "hobbies" -> list(text)
  )(UserData.apply)(UserData.unapply)
    .verifying(ud => { ud.name match {
      case "daniel" if ud.age == 21 => true
      case "admin" if ud.email.isDefined => true
      case _ => false
    }}))

  val tupleForm = Form(tuple("name" -> text, "age" -> number))
  val singleForm = Form(single("email" -> email))

  singleForm.fill("daniel.shin@gmail.com")

//  def action1[A](action: Action[A]) = new Action {
//    override def parser: BodyParser[A] = parse.json
//    override def apply(request: Request[A]): Future[Result] = {
//
//    }
//  }
//
//  def action2[A](form: Form[A]): Action[A] = new Action {
//    override def parser: BodyParser[A] = {
//      parse.form(form)
//    }
//
//    override def apply(request: Request[A]): Future[Result] = {
//      new Action {
//        override def parser: BodyParser[A] = parse.json
//        override def apply(request: Request[A]): Future[Result] = {
//
//        }
//      }
//    }
//  }

//  def formAction(form: Form) = Action { request =>
//
//
//
//  }

  def post = Action/*(parse.form(userForm, onErrors = (userForm: Form[UserData]) => BadRequest(userForm)))*/ { implicit request =>
//    val userData = userForm.bindFromRequest.fold({ BadRequest(_) }, { Ok(_) })

    val d = userForm.bind(request.body.asJson.get)

//    val userData = request.body
//    Persistence.save(userData)

    Ok()
  }


}

/* Forms
*
* The function of a Form is to transform form data into a bound instance of a case class
*
*
* You are not limited to using case classes in your form mapping. As long as the apply and unapply methods are properly mapped,
* you can pass anything you like.
*
* However, there are serveral advantages to defining a case class specifically for a form
* - Form specific case classes are convenient
* - Form specific case classes are powerful: custom apply / unapply methods
* - FOrm specific case classes are targeted specifically to the Form
* */

