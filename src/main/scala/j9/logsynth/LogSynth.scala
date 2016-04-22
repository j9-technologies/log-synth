package j9.logsynth

import java.io.File

import org.backuity.clist._

abstract class  LogSynth extends Command(description="Synthesize log files with realistic data") {
  var outputDir = opt[File](default = new File("."), description="Path to directory where output logs will be created")
  var logFiles = opt[Int](description="Number of log files to write (in parallel)")
  var schemas = args[Seq[File]](description = "Paths to schema files defining format of generated log files")
}

/** A continuously writing log synthesizer */
object LogSynth extends LogSynth with App {

  Cli.parse(args).version("0.1.0").withCommand(this) { case logSynth =>
    // the new Cat instance will be mutated to receive the command-line arguments
    println(s"Writing ${logSynth.logFiles} log files to ${logSynth.outputDir}")
  }
}
