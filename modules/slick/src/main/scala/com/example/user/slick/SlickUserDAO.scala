package com.example.user.slick

import java.util.UUID
import javax.inject.{Inject, Singleton}

import org.joda.time.DateTime
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.JdbcProfile
import com.example.user._

import scala.concurrent.Future
import scala.language.implicitConversions

/**
 * A User DAO implemented with Slick, leveraging Slick code gen.
 *
 * Note that you must run "flyway/flywayMigrate" before "compile" here.
 */
@Singleton
class SlickUserDAO @Inject()(db: Database) extends UserDAO with Tables {

  // Use the custom postgresql driver.
  override val profile: JdbcProfile = MyPostgresDriver

  import profile.api._

  private val queryById = Compiled(
    (id: Rep[UUID]) => Users.filter(_.id === id))

  def lookup(id: UUID)(implicit ec: UserDAOExecutionContext): Future[Option[User]] = {
    val f: Future[Option[UsersRow]] = db.run(queryById(id).result.headOption)
    f.map(maybeRow => maybeRow.map(usersRowToUser))
  }

  def all(implicit ec: UserDAOExecutionContext): Future[Seq[User]] = {
    val f = db.run(Users.result)
    f.map(seq => seq.map(usersRowToUser))
  }

  def update(user: User)(implicit ec: UserDAOExecutionContext): Future[Int] = {
    db.run(queryById(user.id).update(userToUsersRow(user)))
  }

  def delete(id: UUID)(implicit ec: UserDAOExecutionContext): Future[Int] = {
    db.run(queryById(id).delete)
  }

  def create(user: User)(implicit ec: UserDAOExecutionContext): Future[Int] = {
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
