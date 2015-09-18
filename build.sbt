name := "PlaySBT"

version := "1.0"

scalaVersion := "2.11.7"

lazy val playsbt = project.in(file(".")).enablePlugins(PlayScala)

libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.1.3"
libraryDependencies += "joda-time" % "joda-time" % "2.8.2"

routesGenerator := InjectedRoutesGenerator