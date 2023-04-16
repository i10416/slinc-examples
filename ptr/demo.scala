//> using scala "3.3.0-RC3"
//> using lib "fr.hammons::slinc-runtime:0.3.0"
//> using javaOpt "--enable-native-access=ALL-UNNAMED"
//> using javaOpt "--add-modules=jdk.incubator.foreign"

import fr.hammons.slinc.Scope
import fr.hammons.slinc.Ptr
import fr.hammons.slinc.types.CInt
import fr.hammons.slinc.runtime.given
@main def program = Scope.confined {
  val iptr = Ptr.blank[CInt]
  !iptr = 1
  println(!iptr)
}
    
