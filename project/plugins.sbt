resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"

resolvers += "Flyway" at "https://flywaydb.org/repo"

// Database db.migration
addSbtPlugin("org.flywaydb" % "flyway-sbt" % "4.1.2")

// Slick code generation
// https://github.com/tototoshi/sbt-slick-codegen
addSbtPlugin("com.github.tototoshi" % "sbt-slick-codegen" % "1.2.1")

libraryDependencies += "org.postgresql" % "postgresql" % "9.4.1212"

// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.5.15")
