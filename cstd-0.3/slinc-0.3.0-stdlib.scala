//> using scala "3.3.0-RC3"
//> using lib "fr.hammons::slinc-runtime:0.3.0"
//> using javaOpt "--enable-native-access=ALL-UNNAMED"
//> using javaOpt "--add-modules=jdk.incubator.foreign"

import fr.hammons.slinc.FSet
import fr.hammons.slinc.Ptr
import fr.hammons.slinc.Scope
import fr.hammons.slinc.types.CChar
import fr.hammons.slinc.types.CLong
trait stdlib derives FSet:
  def strlen(str: Ptr[CChar]): CLong
@main def run =
  import fr.hammons.slinc.runtime.given
  Scope.confined {
    val foo = "foo"
    val fooptr = Ptr.copy(foo)

    val len = FSet.instance[stdlib].strlen(fooptr)
    println(len)
  }
