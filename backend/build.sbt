val V = new {
  val scala = "3.5.2"
  val tapir = "1.11.8"
  val circe = "0.14.0"
  val client3 = "3.9.8"
  val http4s = "0.23.16"
  val catsEffect = "3.5.5"
}

lazy val root = (project in file("."))
  .settings(
    name := "backend",
    scalaVersion := V.scala,
    libraryDependencies ++= Seq(
      "io.circe" %% "circe-core" % V.circe,
      "io.circe" %% "circe-generic" % V.circe,
      "com.softwaremill.sttp.tapir" %% "tapir-core" % V.tapir,
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server" % V.tapir,
      "com.softwaremill.sttp.tapir" %% "tapir-json-circe" % V.tapir,
      "com.softwaremill.sttp.client3" %% "core" % V.client3,
      "org.http4s" %% "http4s-blaze-server" % V.http4s,
      "org.typelevel" %% "cats-effect" % V.catsEffect
    ),
    javacOptions ++= Seq("-source", "11", "-target", "11")
  )
