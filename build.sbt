name := "PlaySBT"

version := "1.0"

scalaVersion := "2.11.7"

lazy val playsbt = project.in(file(".")).enablePlugins(PlayScala)

libraryDependencies += "io.reactivex" %% "rxscala" % "0.25.0"
libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.1.3"

routesGenerator := InjectedRoutesGenerator