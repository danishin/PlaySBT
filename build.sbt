name := "PlaySBT"

version := "1.0"

scalaVersion := "2.11.7"

lazy val playsbt = project.in(file(".")).enablePlugins(PlayScala)

/* Play Framework Libraries */
libraryDependencies ++= Seq(
  jdbc,
  ws,
  filters
)

/* Java Libraries */
libraryDependencies += "org.postgresql" % "postgresql" % "9.4-1202-jdbc42"

/* Scala Libraries */
libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.1.3"
libraryDependencies += "com.typesafe.play" %% "anorm" % "2.5.0"

routesGenerator := InjectedRoutesGenerator