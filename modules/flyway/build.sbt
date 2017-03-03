// Database Migrations:
// run with "sbt flywayMigrate"
// http://flywaydb.org/getstarted/firststeps/sbt.html

//$ export DB_DEFAULT_URL="jdbc:h2:/tmp/example.db"
//$ export DB_DEFAULT_USER="sa"
//$ export DB_DEFAULT_PASSWORD=""

libraryDependencies ++= Seq(
  "org.flywaydb" % "flyway-core" % "4.0.3",
  "org.flywaydb" %% "flyway-play" % "3.0.1"
)

lazy val databaseUrl = sys.env.getOrElse("DB_DEFAULT_URL", "jdbc:postgresql://localhost:5432/myapp")
lazy val databaseUser = sys.env.getOrElse("DB_DEFAULT_USER", "myuser")
lazy val databasePassword = sys.env.getOrElse("DB_DEFAULT_PASSWORD", "mypass")

flywayLocations := Seq("classpath:db/db.migration")

flywayUrl := databaseUrl
flywayUser := databaseUser
flywayPassword := databasePassword
