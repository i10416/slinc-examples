//> using scala "3.2.2"
//> using lib "fr.hammons::slinc-runtime:0.1.1-110-7863cb"
//> using javaOpt "--enable-native-access=ALL-UNNAMED"
//> using javaOpt "--add-modules=jdk.incubator.foreign"

import fr.hammons.slinc.runtime.*
import fr.hammons.slinc.runtime.given
import fr.hammons.slinc.{Ptr, Scope}

object stdlib derives Library:
  def strlen(arg: Ptr[CChar]): CLong = Library.binding
@main def program = Scope.confined {
  val foo = "foo"
  val fooptr = Ptr.copy(foo)
  val len = stdlib.strlen(fooptr)
  assert(len.`as`[Long] == 3)
  println(len)
}

