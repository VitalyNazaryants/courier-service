package v1.requests

import javax.inject.Inject

import play.api.http.HttpVerbs
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.{ActionBuilder, Request, Result, WrappedRequest}

import scala.concurrent.{ExecutionContext, Future}

/**
  * A wrapped request for post resources.
  *
  * This is commonly used to hold request-specific information like
  * security credentials, and useful shortcut methods.
  */
class DeliveryRequestRequest[A](request: Request[A], val messages: Messages)
  extends WrappedRequest(request)

/**
  * The default action for the User resource.
  *
  * This is the place to put logging, metrics, to augment
  * the request with contextual data, and manipulate the
  * result.
  */
class DeliveryRequestAction @Inject()(messagesApi: MessagesApi)(
  implicit ec: ExecutionContext)
  extends ActionBuilder[DeliveryRequestRequest]
    with HttpVerbs {

  type PostRequestBlock[A] = DeliveryRequestRequest[A] => Future[Result]

  private val logger = org.slf4j.LoggerFactory.getLogger(this.getClass)

  override def invokeBlock[A](request: Request[A],
                              block: PostRequestBlock[A]): Future[Result] = {
    if (logger.isTraceEnabled()) {
      logger.trace(s"invokeBlock: request = $request")
    }

    val messages = messagesApi.preferred(request)
    val future = block(new DeliveryRequestRequest(request, messages))

    future.map { result =>
      request.method match {
        case GET | HEAD =>
          result.withHeaders("Cache-Control" -> s"max-age: 100")
        case _ =>
          result
      }
    }
  }
}
