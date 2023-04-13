//> using scala "3.3.0-RC3"
//> using lib "fr.hammons::slinc-runtime:0.3.0"
//> using javaOpt "--enable-native-access=ALL-UNNAMED"
//> using javaOpt "--add-modules=jdk.incubator.foreign"

import fr.hammons.slinc.FSet
import fr.hammons.slinc.annotations.NeedsFile

@NeedsFile("build/libadd.so")
trait libadd derives FSet:
  def add(a: Int, b: Int): Int

@main def run =
  import fr.hammons.slinc.runtime.*
  import fr.hammons.slinc.runtime.given
  val libadd = FSet.instance[libadd]
  println(libadd.add(21, 21))
