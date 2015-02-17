organization := "org.mmarini"

name := "briscala"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.5"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
 
libraryDependencies += "org.scala-lang" % "scala-swing" % "2.11.0-M7"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.3.9"

libraryDependencies += "com.github.scala-incubator.io" %% "scala-io-file" % "0.4.3-1"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.2"

libraryDependencies += "org.scalanlp" %% "breeze" % "0.10"

libraryDependencies += "com.github.nscala-time" % "nscala-time_2.11" % "1.8.0"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % Test

libraryDependencies += "org.scalamock" %% "scalamock-core" % "3.1.2" % Test

libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.11.6" % Test

lazy val root = project in file(".")
