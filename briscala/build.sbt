lazy val commonSettings = Seq(
  organization := "org.mmarini",
  name := "briscala",
  version := "0.0.1-SNAPSHOT",
  scalaVersion := "2.11.4"
)

lazy val scalaToolVersion = "2.11"



lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    libraryDependencies += "com.github.scala-incubator.io" %% "scala-io-file" % "0.4.3-1",
	libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
	libraryDependencies += "org.scalanlp" %% "breeze" % "0.10",
	
	libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.2" % Test,
	libraryDependencies += "org.scalamock" %% "scalamock-core" % "3.1.2" % Test,
	libraryDependencies += "org.scalacheck" %% "scalacheck" % "1.11.6" % Test
  )
