package com.example.delivery.request

import java.util.UUID

import org.joda.time.DateTime

import scala.concurrent.Future

/**
 * An implementation dependent DAO.  This could be implemented by Slick, Cassandra, or a REST API.
 */
trait DeliveryRequestDAO {

  def lookup(id: UUID): Future[Option[DeliveryRequest]]

  def all: Future[Seq[DeliveryRequest]]

  def update(request: DeliveryRequest): Future[Int]

  def delete(id: UUID): Future[Int]

  def create(request: DeliveryRequest): Future[Int]

  def close(): Future[Unit]
}

/**
 * Implementation independent aggregate root.
 *
 * Note that this uses Joda Time classes and UUID, which are specifically mapped
 * through the custom postgres driver.
 */
case class DeliveryRequest(id: UUID, userId: UUID, cargoInfo: Option[String], deliverBy: String, maxWeight: Int,
                           insurance: Option[Int], loadUnload: Option[Boolean], sms: Option[Boolean], confirmed: Option[Boolean],
                           createdAt: DateTime, updatedAt: Option[DateTime])
