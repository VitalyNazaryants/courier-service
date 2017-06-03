package v1.users

import javax.inject.Inject

import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

class UserRouter @Inject()(controller: UserController) extends SimpleRouter {
  val prefix = "/app/v1/users"

  override def routes: Routes = {
    case GET(p"/") =>
      controller.list

    case POST(p"/") =>
      controller.process

    case GET(p"/$id") =>
      controller.show(id)

    case PUT(p"/$id") =>
      controller.update(id)

    case DELETE(p"/$id") =>
      controller.delete(id)
  }
}
