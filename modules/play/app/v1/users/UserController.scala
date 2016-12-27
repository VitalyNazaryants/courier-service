package v1.users

import java.util.UUID
import javax.inject.Inject

import com.example.user.{User, UserDAO, UserDAOExecutionContext}
import org.joda.time.DateTime
import play.api.data.Form
import play.api.libs.json.{JsValue, Json, Writes}
import play.api.mvc.{Controller, _}

import scala.concurrent.Future

case class UserInput(userType: String, name: String, phone: String, address: Option[String], passport: Option[String], email: String)

class UserController @Inject()(action: UserAction, handler: UserDAO, userDAOExecutionContext: UserDAOExecutionContext)
  extends Controller {

  implicit val ec = userDAOExecutionContext

  implicit val implicitWrites = new Writes[User] {
    override def writes(user: User): JsValue = {
      Json.obj(
        "id" -> user.id,
        "type" -> user.userType,
        "name" -> user.name,
        "phone" -> user.phone,
        "address" -> user.address,
        "passport" -> user.passport,
        "email" -> user.email,
        "created" -> user.createdAt,
        "updated" -> user.updatedAt
      )
    }
  }

  private val form: Form[UserInput] = {
    import play.api.data.Forms._

    Form(
      mapping(
        "type" -> nonEmptyText,
        "name" -> nonEmptyText,
        "phone" -> nonEmptyText,
        "address" -> optional(text),
        "passport" -> optional(text),
        "email" -> nonEmptyText
      )(UserInput.apply)(UserInput.unapply)
    )
  }

  def list: Action[AnyContent] = {
    action.async { implicit request =>
      handler.all.map { users =>
        Ok(Json.toJson(users))
      }
    }
  }

  def process: Action[AnyContent] = {
    action.async { implicit request =>
      processJsonPost()
    }
  }

  def show(id: String): Action[AnyContent] = {
    action.async { implicit request =>
      handler.lookup(UUID.fromString(id)).map { user =>
        Ok(Json.toJson(user))
      }
    }
  }

  private def processJsonPost[A]()(
    implicit request: UserRequest[A]): Future[Result] = {
    def failure(badForm: Form[UserInput]) = {
      Future.successful(BadRequest(badForm.errorsAsJson))
    }

    def success(input: UserInput) = {
      val user = User(id = UUID.randomUUID(), userType = input.userType, name = input.name, phone = input.phone,
        address = input.address, passport = input.passport, email = input.email, createdAt = DateTime.now(), updatedAt = None)
      handler.create(user).map { user =>
        Created(Json.toJson(user))
      }
    }

    form.bindFromRequest().fold(failure, success)
  }
}
