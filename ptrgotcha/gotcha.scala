//> using scala "3.3.0-RC3"
//> using lib "fr.hammons::slinc-runtime:0.3.0"
//> using javaOpt "--enable-native-access=ALL-UNNAMED"
//> using javaOpt "--add-modules=jdk.incubator.foreign"
import fr.hammons.slinc.Struct
import fr.hammons.slinc.Scope
import fr.hammons.slinc.Ptr
import fr.hammons.slinc.runtime.given
case class Point(var x: Int, y: Int) derives Struct

@main def program = Scope.confined {
  val p = Point(1, 1)
  val pptr = Ptr.copy(p)
  (!pptr).x = 2
  println(!pptr)
}
