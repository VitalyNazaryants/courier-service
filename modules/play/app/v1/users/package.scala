package v1

import play.api.i18n.Messages

package object users {
  implicit def requestToMessages[A](implicit r: UserRequest[A]): Messages = {
    r.messages
  }
}
