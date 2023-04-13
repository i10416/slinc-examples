//> using scala "3.2.2"
//> using lib "fr.hammons::slinc-runtime:0.1.1-110-7863cb"
//> using javaOpt "--enable-native-access=ALL-UNNAMED"
//> using javaOpt "--add-modules=jdk.incubator.foreign"

import fr.hammons.slinc.runtime.*
import fr.hammons.slinc.runtime.given
import fr.hammons.slinc.LibraryName

@LibraryName("/path/to/libadd.dylib")
object libadd derives Library:
  def add(a: Int, b: Int): Int = Library.binding

@main def run =
  println(libadd.add(21, 21))







   