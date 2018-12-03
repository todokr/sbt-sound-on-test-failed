import Dependencies._
import javax.sound.sampled.{AudioSystem, Clip, DataLine}
import scala.Console._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.example",
      scalaVersion := "2.12.7",
      version      := "0.1.0-SNAPSHOT"
    )),
    name := "sbt-sound-on-test-failed",
    libraryDependencies += scalaTest % Test,
    (test in Test) := {
      (test in Test).result.value match {
        case Inc(inc) =>
          val ais = AudioSystem.getAudioInputStream((resourceDirectory in Test).value / "Glass.aiff")
          val format = ais.getFormat
          val dataLine = new DataLine.Info(classOf[Clip], format)
          AudioSystem.getLine(dataLine) match {
            case c: Clip =>
              c.open(ais)
              c.loop(0)
              c.flush()
              while(c.isActive) { Thread.sleep(100) }
            case _ => println(RED + "Not a Clip" + RESET)
          }
          ais.close()
          throw inc
        case Value(value) => value
      }
    }
  )

