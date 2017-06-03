package com.example.user.slick

import java.util.UUID
import javax.inject.{Inject, Singleton}

import com.example.request.{DeliveryRequest, DeliveryRequestDAO}
import org.joda.time.DateTime
import slick.driver.JdbcProfile
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.{ExecutionContext, Future}
import scala.language.implicitConversions

/**
  * A User DAO implemented with Slick, leveraging Slick code gen.
  *
  * Note that you must run "flyway/flywayMigrate" before "compile" here.
  *
  * @param db the slick database that this user DAO is using internally, bound through Module.
  * @param ec a CPU bound execution context.  Slick manages blocking JDBC calls with its
  *           own internal thread pool, so Play's default execution context is fine here.
  */
@Singleton
abstract class SlickDeliveryRequestDAO @Inject()(db: Database)(implicit ec: ExecutionContext) extends DeliveryRequestDAO with Tables {

  // Use the custom postgresql driver.
  override val profile: JdbcProfile = MyPostgresDriver

  import profile.api._

  private val queryById = Compiled(
    (id: Rep[UUID]) => Requests.filter(_.id === id))

  def lookup(id: UUID): Future[Option[DeliveryRequest]] = {
    val f: Future[Option[RequestsRow]] = db.run(queryById(id).result.headOption)
    f.map(maybeRow => maybeRow.map(requestsRowToRequest))
  }

  def all: Future[Seq[DeliveryRequest]] = {
    val f = db.run(Requests.result)
    f.map(seq => seq.map(requestsRowToRequest))
  }

  def update(user: DeliveryRequest): Future[Int] = {
    db.run(queryById(user.id).update(requestToRequestsRow(user)))
  }

  def delete(id: UUID): Future[Int] = {
    db.run(queryById(id).delete)
  }

  def create(user: DeliveryRequest): Future[Int] = {
    db.run(
      Requests += requestToRequestsRow(user.copy(createdAt = DateTime.now()))
    )
  }

  def close(): Future[Unit] = {
    Future.successful(db.close())
  }

  private def requestToRequestsRow(deliveryRequest: DeliveryRequest): RequestsRow = {
    RequestsRow(id = deliveryRequest.id, userId = deliveryRequest.userId, cargoInfo = deliveryRequest.cargoInfo,
      deliverBy = deliveryRequest.deliverBy, maxWeight = deliveryRequest.maxWeight,
      insurance = deliveryRequest.insurance, loadUnload = deliveryRequest.loadUnload, sms = deliveryRequest.sms,
      confirmed = deliveryRequest.confirmed, createdAt = deliveryRequest.createdAt, updatedAt = deliveryRequest.updatedAt)
  }

  private def requestsRowToRequest(requestsRow: RequestsRow): DeliveryRequest = {
    DeliveryRequest(id = requestsRow.id, userId = requestsRow.userId, cargoInfo = requestsRow.cargoInfo,
      deliverBy = requestsRow.deliverBy, maxWeight = requestsRow.maxWeight,
      insurance = requestsRow.insurance, loadUnload = requestsRow.loadUnload, sms = requestsRow.sms,
      confirmed = requestsRow.confirmed, createdAt = requestsRow.createdAt, updatedAt = requestsRow.updatedAt)
  }
}
