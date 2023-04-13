## Example: Call C Function with SlinC

### build

```sh
cmake -S . -B build
```

```sh
cmake --build build
```


### Run

NOTE: set "/path/to/libadd.dylib" in `add.scala` and `add.java` to actual path in advance.

```sh
scala-cli add.scala
```

```sh
java --add-modules jdk.incubator.foreign --enable-native-access=ALL-UNNAMED ./add.java  
```



