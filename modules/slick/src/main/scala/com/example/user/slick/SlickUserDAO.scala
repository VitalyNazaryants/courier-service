package com.example.user.slick

import java.util.UUID
import javax.inject.{Inject, Singleton}

import org.joda.time.DateTime
import slick.driver.JdbcProfile
import slick.jdbc.JdbcBackend.Database
import com.example.user._

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
class SlickUserDAO @Inject()(db: Database)(implicit ec: ExecutionContext) extends UserDAO with Tables {

  // Use the custom postgresql driver.
  override val profile: JdbcProfile = MyPostgresDriver

  import profile.api._

  private val queryById = Compiled(
    (id: Rep[UUID]) => Users.filter(_.id === id))

  def lookup(id: UUID): Future[Option[User]] = {
    val f: Future[Option[UsersRow]] = db.run(queryById(id).result.headOption)
    f.map(maybeRow => maybeRow.map(usersRowToUser))
  }

  def all: Future[Seq[User]] = {
    val f = db.run(Users.result)
    f.map(seq => seq.map(usersRowToUser))
  }

  def update(user: User): Future[Int] = {
    db.run(queryById(user.id).update(userToUsersRow(user)))
  }

  def delete(id: UUID): Future[Int] = {
    db.run(queryById(id).delete)
  }

  def create(user: User): Future[Int] = {
    db.run(
      Users += userToUsersRow(user.copy(createdAt = DateTime.now()))
    )
  }

  def close(): Future[Unit] = {
    Future.successful(db.close())
  }

  private def userToUsersRow(user: User): UsersRow = {
    UsersRow(id = user.id, `type` = user.userType, name = user.name, phone = user.phone, address = user.address,
      passport = user.passport, email = user.email, createdAt = user.createdAt, updatedAt = user.updatedAt)
  }

  private def usersRowToUser(usersRow: UsersRow): User = {
    User(id = usersRow.id, userType = usersRow.`type`, name = usersRow.name, phone = usersRow.phone, address = usersRow.address,
      passport = usersRow.passport, email = usersRow.email, createdAt = usersRow.createdAt, updatedAt = usersRow.updatedAt)
  }
}
