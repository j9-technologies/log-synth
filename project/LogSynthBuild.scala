
import com.j9tech.sbt.ProjectPlugin
import com.reactific.sbt.PublishUniversalPlugin
import com.reactific.sbt.settings._
import com.typesafe.sbt.packager.archetypes.JavaAppPackaging
import com.typesafe.sbt.packager.Keys._
import com.typesafe.sbt.packager.universal.UniversalPlugin.autoImport.Universal
import sbt._
import sbt.Keys._

import scala.language.postfixOps

object LogSynthBuild extends Build {

  val sonatype_releases       = "Sonatype Releases"  at "http://oss.sonatype.org/content/repositories/releases"
  val sonatype_snapshots      = "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
  val bintray_typesafe        = "Bintray/Typesafe" at "https://dl.bintray.com/typesafe/ivy-releases/"
  val jcenter_bintray         = "JCenter" at "http://jcenter.bintray.com/"
  val clapper_repo            = "Clapper Repository" at "http://maven.clapper.org/"
  val splunk_repo             = "Splunk Repository" at "http://splunk.artifactoryonline.com/splunk/ext-releases-local/"
  val scalaz_bintray          = "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
  val all_resolvers           = Seq ( bintray_typesafe, sonatype_releases, sonatype_snapshots, jcenter_bintray,
    clapper_repo, splunk_repo, scalaz_bintray )

  val JavaFastPFOR = "me.lemire.integercompression" % "JavaFastPFOR" % "0.0.13"
  val mahout_math = "org.apache.mahout" % "mahout-math" % "0.9"
  val stream  =      "com.clearspring.analytics" % "stream"  % "2.5.0" % "test"
  val junit = "junit" % "junit" % "4.8.2"   % "test"
  val args4j = "args4j" % "args4j" % "2.0.23"
  val jackson_databind = "com.fasterxml.jackson.core" % "jackson-databind" % "2.4.0"
  val woodstox_core_asl = "org.codehaus.woodstox" % "woodstox-core-asl" % "4.1.4"
  val jackson_dataformat_xml = "com.fasterxml.jackson.dataformat" % "jackson-dataformat-xml" % "2.4.0"
  val freemarker = "org.freemarker" % "freemarker" % "2.3.21"
  val stax_api = "stax" % "stax-api" % "1.0.1"
  val core = "org.processing" % "core" % "3.0b6"
  val t_digest = "com.tdunning" % "t-digest" % "3.0"
  val t_digest_test = "com.tdunning" % "t-digest" % "3.0"   % "test"
  val clist_core = "org.backuity.clist" %% "clist-core"   % "2.0.2"
  val clist_macros = "org.backuity.clist" %% "clist-macros" % "2.0.2" % "provided"
  val logback_classic = "ch.qos.logback"  % "logback-classic" % "1.1.7"
  val akka_actors = "com.typesafe.akka" %% "akka-actor" % "2.4.4"
  val reactific_helpers = "com.reactific" %% "helpers" % "0.3.7"

  val all_dependencies = Seq (
    JavaFastPFOR, mahout_math, stream , junit, args4j, jackson_databind, woodstox_core_asl,
    jackson_dataformat_xml, freemarker, t_digest, stax_api, core, t_digest, t_digest_test, clist_core, clist_macros,
    logback_classic, akka_actors, reactific_helpers
  )

  lazy val log_synth =
    sbt.Project("log-synth", file(".")).
      enablePlugins(ProjectPlugin, JavaAppPackaging, PublishUniversalPlugin).
      settings(
        codePackage := "j9.logsynth",
        sbtbuildinfo.BuildInfoKeys.buildInfoPackage := "j9.logsynth",
        sbtbuildinfo.BuildInfoKeys.buildInfoObject := "BuildInfo",
        packageSummary := "JDKPackagerPlugin example package thingy",
        packageDescription := "A test package using Oracle's JDK bundled javapackager tool.",
        mappings in Universal <+= packageBin in Compile map { p => file("README.md") -> "README" },
        titleForDocs := "J9 Log Maker",
        javacOptions := javacOptions.value.filterNot { opt ⇒ opt.equals("-Xdoclint:all") || opt.equals("-Werror") },
        resolvers := all_resolvers,
        mainClass in Compile := Some("j9.logsynth.LogSynth"),
        libraryDependencies ++= all_dependencies
      )

  override def rootProject = Some(log_synth)

}
