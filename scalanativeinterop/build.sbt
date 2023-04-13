import java.nio.file.CopyOption
import scala.scalanative.build._

val copyNativeOut =
  taskKey[Unit]("copy native link output to resource directory")

lazy val native = project
  .in(file("native"))
  .enablePlugins(ScalaNativePlugin)
  .settings(
    scalaVersion := "3.2.2",
    nativeConfig ~= { _.withBuildTarget(BuildTarget.libraryDynamic) }
  )

lazy val jvm = project
  .in(file("jvm"))
  .settings(
    Compile / compile := {
      val o = (native / Compile / nativeLink).value
      java.nio.file.Files.copy(
        o.toPath,
        ((Compile / resourceDirectory).value / "native"/ "libhello.so").toPath,
        java.nio.file.StandardCopyOption.REPLACE_EXISTING
      )
     (Compile / compile).value
    },
    run / fork := true,
    javaOptions ++= Seq(
      "--enable-native-access=ALL-UNNAMED",
      "--add-modules=jdk.incubator.foreign"
    ),
    scalaVersion := "3.2.2",
    libraryDependencies ++= Seq(
      "fr.hammons" %% "slinc-runtime" % "0.1.1-110-7863cb"
    )
  )
  .dependsOn(native)

lazy val root = project
  .in(file("."))
  .aggregate(jvm, native)
