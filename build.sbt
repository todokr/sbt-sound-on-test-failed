import Dependencies._
import javax.sound.sampled.{AudioSystem, Clip, DataLine}
import sbt.io.Using

import scala.Console._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "2.12.7",
      version      := "0.1.0-SNAPSHOT"
    )),
    shellPrompt := { state =>
      s"👍 $GREEN[%s]$RESET & ".format(Project.extract(state).currentProject.id)
    },
    name := "sbt-sound-on-test-failed",
    run in Compile := {
      (run in Compile).evaluated
      scala.sys.process.Process("yarn serve", baseDirectory.value / "frontend-src").!
    },
    libraryDependencies += scalaTest % Test
  )

//lazy val serveSpa = taskKey[Unit]("Run `yarn serve`")
//serveSpa := {
//  scala.sys.process.Process("yarn serve", baseDirectory.value / "frontend-src").!
//}
//(run in Compile) := ((run in Compile) dependsOn serveSpa).evaluated


playSound(test in Test, Some("Glass.aiff"), Some("out.wav"))

def playSound[T](taskKey: TaskKey[T], soundOnSuccess: Option[String] = None, soundOnFail: Option[String] = None): Setting[Task[T]] =
  taskKey := {
    (taskKey.result.value, soundOnSuccess, soundOnFail) match {
      case (Inc(inc), _, Some(soundFile)) =>
        play((resourceDirectory in Test).value / soundFile)
        throw inc
      case (Inc(inc), _, _) => throw inc
      case (Value(value), Some(soundFile), _) =>
        play((resourceDirectory in Test).value / soundFile)
        value
      case (Value(value), _, _) => value
    }
  }

def play(file: File): Unit = Using.fileInputStream(file) { stream =>
  val ais = AudioSystem.getAudioInputStream(stream)
  val format = ais.getFormat
  val dataLine = new DataLine.Info(classOf[Clip], format)
  AudioSystem.getLine(dataLine) match {
    case c: Clip =>
      c.open(ais)
      c.loop(0)
      c.start()
      while (c.isRunning) { Thread.sleep(100) }
    case _ => println(RED + "Not a Clip" + RESET)
  }
}