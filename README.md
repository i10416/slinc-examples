# SlinC examples

NOTE: this example uses libportaudio built for osx arm64. If your machine is not osx arm64, please use a proper libportaudio binary targeted for your machine os and cpu architecture. 

## About

This repository contains a set of small examples demonstrating how to use SlinC.

Before trying use-pkg, portaudio-wav, run `cd pkg && sbt publishLocal` to locally publish libportaudio binding.

- cstd: example of calling c function from standard library in Scala with SlinC 0.1.1-110-7863cb and in Java 
- cstd-0.3: example of calling c function from standard library in Scala with SlinC 0.3.0
- sharedlib: example of calling function from shared library in Scala 3 with SlinC 0.1.1-110-7863cb and in Java
- sharedlib-0.3: example of calling function from shared library in Scala 3 with SlinC 0.3.0
- struct: example of dealing with struct
- portaudio-sinewave: example of playing a sine wave audio using libportaudio
- portaudio-wav: example of playing a wav file using libportaudio
- pkg: package native library as a Scala library
- use-pkg: use Scala library containing native library
- scalanativeinterop: call scala native shared library from jvm!




