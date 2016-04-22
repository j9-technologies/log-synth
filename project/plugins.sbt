import _root_.sbt.Keys._
import _root_.sbt.Level
import _root_.sbt._

// Comment to get more information during initialization
logLevel := Level.Warn

scalaVersion := "2.10.5"

scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature", "-Xlint")

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-simple" % "1.7.12"
)


// The Typesafe repository
resolvers += "J9 Repository" at "https://nexus.j9tech.com/repository/j9-build"
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
resolvers += "Sonatype repository" at "https://oss.sonatype.org/content/repositories/releases/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.j9tech" % "sbt-project" % "1.0.0")

// Use SBT's Native Packager
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.1.0-RC3")

