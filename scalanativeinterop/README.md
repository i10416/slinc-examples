# Call Scala Native shared library from JVM using Slinc!

## About

Scala Native can generate both shared library and static library since 0.4.8 (2022-11-09), so we can write Scala Native binding using JVM FFI.

There's no reason not trying out Slinc with Scala Native.


## Build

```sh
sbt run
// => Hello from Scala Native!
```

## NOTE

As of 2023/04/16, You need to replace the path in `LibraryName` annotation with your actual path until Scala Native supports Scala 3.3.0(-RCN) as Slinc 0.3.0, which supports new annotation `NeedsResource` to load a native library from resource directory, uses Scala 3.3.0-RC3.

```scala
@LibraryName("/PATH/TO/scalanativeinterop/jvm/src/main/resources/native/libhello.so")
object lib derives Library:
    def greet(): Unit = Library.binding
```
After Scala Native supports Scala 3.3.0(-RCN), we can replace the above code with the following.

```scala
@NeedsResource("libhello.so")
trait lib derives FSet:
    def greet(): Unit
object lib:
    val instance = FSet.instance[lib]
```
