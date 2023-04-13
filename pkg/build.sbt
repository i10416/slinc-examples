val pkg = project
  .in(file("."))
  .settings(
    organization := "dev.i10416",
    name := "libportaudio",
    scalaVersion := "3.3.0-RC3",
    version := "0.1.0-SNAPSHOT",
    libraryDependencies ++= Seq(
      "fr.hammons" %% "slinc-runtime" % "0.3.0"
    )
  )
