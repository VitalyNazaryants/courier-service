package v1.requests

import java.util.UUID
import javax.inject.Inject

import com.example.delivery.request.{DeliveryRequest, DeliveryRequestDAO}
import org.joda.time.DateTime
import play.api.data.Form
import play.api.libs.json.{JsValue, Json, Writes}
import play.api.mvc.{Controller, _}

import scala.concurrent.{ExecutionContext, Future}

case class DeliveryRequestInput(userId: UUID, cargoInfo: Option[String], deliverBy: String, maxWeight: Int,
                                insurance: Option[Int], loadUnload: Option[Boolean], sms: Option[Boolean], confirmed: Option[Boolean])

class DeliveryRequestController @Inject()(action: DeliveryRequestAction, handler: DeliveryRequestDAO)(implicit ec: ExecutionContext)
  extends Controller {

  implicit val implicitWrites = new Writes[DeliveryRequest] {
    override def writes(deliveryRequest: DeliveryRequest): JsValue = {
      Json.obj(
        "id" -> deliveryRequest.id,
        "userId" -> deliveryRequest.userId,
        "cargoInfo" -> deliveryRequest.cargoInfo,
        "deliverBy" -> deliveryRequest.deliverBy,
        "maxWeight" -> deliveryRequest.maxWeight,
        "insurance" -> deliveryRequest.insurance,
        "loadUnload" -> deliveryRequest.loadUnload,
        "sms" -> deliveryRequest.sms,
        "confirmed" -> deliveryRequest.confirmed,
        "created" -> deliveryRequest.createdAt,
        "updated" -> deliveryRequest.updatedAt
      )
    }
  }

  private val form: Form[DeliveryRequestInput] = {
    import play.api.data.Forms._

    Form(
      mapping(
        "userId" -> uuid,
        "cargoInfo" -> optional(text),
        "deliverBy" -> nonEmptyText,
        "maxWeight" -> number,
        "insurance" -> optional(number),
        "loadUnload" -> optional(boolean),
        "sms" -> optional(boolean),
        "confirmed" -> optional(boolean)
      )(DeliveryRequestInput.apply)(DeliveryRequestInput.unapply)
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
      try {
        handler.lookup(UUID.fromString(id)).map { user =>
          Ok(Json.toJson(user))
        }
      } catch {
        case _: IllegalArgumentException => Future.successful(BadRequest)
      }
    }
  }

  def update(id: String): Action[AnyContent] = {
    action.async { implicit request =>
      processJsonPut(id)
    }
  }

  def delete(id: String): Action[AnyContent] = {
    action.async { implicit request =>
      try {
        handler.delete(UUID.fromString(id)).map(_ => Ok)
      } catch {
        case _: IllegalArgumentException => Future.successful(BadRequest)
      }
    }
  }

  private def processJsonPost[A]()(
    implicit request: DeliveryRequestRequest[A]): Future[Result] = {
    def failure(badForm: Form[DeliveryRequestInput]) = {
      Future.successful(BadRequest(badForm.errorsAsJson))
    }

    def success(input: DeliveryRequestInput) = {
      val deliveryRequest = DeliveryRequest(id = UUID.randomUUID(), userId = input.userId, cargoInfo = input.cargoInfo, deliverBy = input.deliverBy,
        maxWeight = input.maxWeight, insurance = input.insurance, loadUnload = input.loadUnload, sms = input.sms, confirmed = input.confirmed,
        createdAt = DateTime.now(), updatedAt = None)
      handler.create(deliveryRequest).map { _ =>
        Created(Json.toJson(deliveryRequest))
      }
    }

    form.bindFromRequest().fold(failure, success)
  }

  private def processJsonPut[A](id: String)(
    implicit request: DeliveryRequestRequest[A]): Future[Result] = {
    def failure(badForm: Form[DeliveryRequestInput]) = {
      Future.successful(BadRequest(badForm.errorsAsJson))
    }

    def success(input: DeliveryRequestInput) = {
      try {
        val uuid = UUID.fromString(id)
        handler.lookup(uuid).flatMap {
          case Some(deliveryRequest) =>
            val updatedDeliveryRequest = DeliveryRequest(uuid, input.userId, input.cargoInfo, input.deliverBy, input.maxWeight, input.insurance,
              input.loadUnload, input.sms, input.confirmed, deliveryRequest.createdAt, Some(DateTime.now()))
            handler.update(updatedDeliveryRequest).map(_ => Ok(Json.toJson(updatedDeliveryRequest)))
          case None =>
            processJsonPost()
        }
      } catch {
        case _: IllegalArgumentException => Future.successful(BadRequest)
      }
    }

    form.bindFromRequest().fold(failure, success)
  }
}
