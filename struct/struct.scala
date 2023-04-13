//> using scala "3.3.0-RC3"
//> using lib "fr.hammons::slinc-runtime:0.3.0"
//> using javaOpt "--enable-native-access=ALL-UNNAMED"
//> using javaOpt "--add-modules=jdk.incubator.foreign"

import fr.hammons.slinc.FSet
import fr.hammons.slinc.Struct
import fr.hammons.slinc.types.CInt

case class div_t(quot: Int, rem: Int) derives Struct
trait stdlib derives FSet:
    def div(a:CInt,b:CInt): div_t
@main def program =
    import fr.hammons.slinc.runtime.given
    val stdlib = FSet.instance[stdlib]
    val res = stdlib.div(4,2)
    println(res) // div_t(2,0)
    println(res.quot) // 2

