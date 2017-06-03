package v1

import play.api.i18n.Messages

package object requests {
  implicit def requestToMessages[A](implicit r: DeliveryRequestRequest[A]): Messages = {
    r.messages
  }
}
