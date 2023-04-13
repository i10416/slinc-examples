import scala.scalanative.unsafe.*

object lib:
    @exported
    def greet():Unit = println("Hello from Scala Native!")
    @exported
    def ask(question:CString): CInt = 
        if fromCString(question) == "the meaning of life" then 
            42 
        else 0
