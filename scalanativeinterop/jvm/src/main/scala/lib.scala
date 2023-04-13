import fr.hammons.slinc.*
import fr.hammons.slinc.runtime.{*,given}
import fr.hammons.slinc.LibraryLocation.Resource
import fr.hammons.slinc.LibraryName

// NOTE: Loading native library from resources dir is not available until slinc 0.3.0, which depends on Scala 3.3.0-RC3.
//       However, Scala Native latest has not support Scala 3.3.0 yet. So, I put an absolute path to my native lib.
//       When Scala Native for Scala 3.3.0 has been released, we can use `@NeedsResource` from Slinc 0.3.0 
@LibraryName("/Users/yoichiroito/tmp/scalanativeinterop/jvm/src/main/resources/native/libhello.so")
object lib derives Library:
    def greet():Unit = Library.binding
    def ask(c:Ptr[CChar]):CInt = Library.binding


@main def debug = 
    lib.greet()
    Scope.confined {
        val question = Ptr.copy("the meaning of life")
        val answer = lib.ask(question)
        println(answer)
    }
