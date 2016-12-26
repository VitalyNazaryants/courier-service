package com.example.user

import java.util.UUID

import org.joda.time.DateTime

import scala.concurrent.{ExecutionContext, Future}

/**
 * An implementation dependent DAO.  This could be implemented by Slick, Cassandra, or a REST API.
 */
trait UserDAO {

  def lookup(id: UUID)(implicit ec: UserDAOExecutionContext): Future[Option[User]]

  def all(implicit ec: UserDAOExecutionContext): Future[Seq[User]]

  def update(user: User)(implicit ec: UserDAOExecutionContext): Future[Int]

  def delete(id: UUID)(implicit ec: UserDAOExecutionContext): Future[Int]

  def create(user: User)(implicit ec: UserDAOExecutionContext): Future[Int]

  def close(): Future[Unit]
}

/**
 * Implementation independent aggregate root.
 *
 * Note that this uses Joda Time classes and UUID, which are specifically mapped
 * through the custom postgres driver.
 */
case class User(id: UUID, userType: String, name: String, phone: String, address: Option[String], passport: Option[String],
                email: String, createdAt: DateTime, updatedAt: Option[DateTime])

/**
 * Type safe execution context for operations on UserDAO.
 */
trait UserDAOExecutionContext extends ExecutionContext
