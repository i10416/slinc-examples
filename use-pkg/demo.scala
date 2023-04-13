//> using scala "3.3.0-RC3"
//> using javaOpt "--enable-native-access=ALL-UNNAMED"
//> using javaOpt "--add-modules=jdk.incubator.foreign"
//> using lib "dev.i10416::libportaudio:0.1.0-SNAPSHOT"

@main def program =
  import fr.hammons.slinc.runtime.given
  val inst = libportaudio.libportaudio.instance
  val info = inst.Pa_GetVersionInfo()
  println((!info).versionText.copyIntoString(100))
