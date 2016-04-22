package j9.logsynth

import java.io.File
import java.util.concurrent.TimeUnit

import akka.actor.{Actor, Props, ActorSystem}
import com.mapr.synth.samplers.SchemaSampler
import com.reactific.helpers.LoggingHelper
import com.typesafe.scalalogging.{Logger => ScalaLogger}
import org.backuity.clist._
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.collection.JavaConverters._

abstract class  LogSynth extends Command(description="Synthesize log files with realistic data") {
  var outputDir = opt[File](default = new File("."), description="Path to directory where output logs will be created")
  var numLoggers = opt[Int](default = 1, description="Number of loggers to write log files in parallel")
  var rate = opt[Int](default = 1, description="Number of times per second log files are written")
  var maxFiles = opt[Int](default = 20, description="Maximum number of log files per logger")
  var maxFileSizeInMB = opt[Int](default = 50, description = "Maximum size of any log file in megabytes")
  var schemas = args[Seq[File]](description = "Paths to schema files defining format of generated log files")
}

/** A continuously writing log synthesizer */
object LogSynth extends LogSynth with App {

  val system = ActorSystem("LogSynth")

  Cli.parse(args).exitCode(1).version("0.1.0").withCommand(this) { case logSynth =>
    try {
      println(
        s"Creating ${logSynth.numLoggers} log file generators to write to ${logSynth.outputDir} $rate times per second"
      )
      val actors = for (i ← 1 to logSynth.numLoggers) yield {
        val schema = new SchemaSampler(schemas(i % schemas.size))
        val props = LogSynthesizingActor.props(outputDir, i, schema, maxFiles, maxFileSizeInMB)
        system.actorOf(props, s"logsynth-$i")
      }
      val everyMS: Long = 1000L / Math.min(Math.max(1L, rate.toLong), 1000L)
      val interval = FiniteDuration(everyMS, TimeUnit.MILLISECONDS)
      implicit val ec: ExecutionContext = system.dispatcher
      system.scheduler.schedule(Duration.Zero, interval) {
        for (ref ← actors) {
          ref ! "write"
        }
      }
    } catch {
      case x: Throwable ⇒
        println(s"Exception at top level: ${x.getClass.getSimpleName}: ${x.getMessage}")
        System.exit(2)
    }
  }
}

object LogSynthesizingActor {
  def props(outDir : File, identity: Int, schema: SchemaSampler, maxFiles: Int, maxFileSizeInMB : Int) : Props = {
    Props(classOf[LogSynthesizingActor], outDir, identity, schema, maxFiles, maxFileSizeInMB)
  }
}

class LogSynthesizingActor(outDir: File, idNum: Int, schema : SchemaSampler, maxFiles : Int, maxFileSizeInMB : Int)
extends Actor with LoggingHelper {

  import LoggingHelper._

  override def createLoggerName = s"LogSynth-$idNum"

  override protected lazy val log : ScalaLogger = {
    val newAppender = makeRollingFileAppender(
      new File(outDir, createLoggerName + ".log"), maxFiles, maxFileSizeInMB, immediateFlush = false, s"LOG-$idNum"
    )
    val logger = ScalaLogger(LoggerFactory.getLogger(createLoggerName))
    logger.setAppender(newAppender)
    logger
  }

  def receive = {
    case "write" ⇒ writeLine()
  }

  def writeLine(): Unit = {
    val fieldNames = schema.getFieldNames
    val sample = schema.sample()
    val string = new StringBuilder
    for (fieldName ← fieldNames.asScala) {
      string.append(fieldName).append("=").append(sample.get(fieldName)).append(", ")
    }
    string.dropRight(2)
    log.info(string.toString)
  }
}
