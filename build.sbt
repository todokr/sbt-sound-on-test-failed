import Dependencies._

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
          val res = (resourceDirectory in Test).value / "test.txt"
          println(IO.read(res))
          println("test failed!!!!!!!!!!!!!!!")
          throw inc
        case Value(value) =>
          value
      }
    }
  )