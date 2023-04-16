# Pointer Gotcha
## About

assigning value and dereference to/from pointer are NOT direct memory manupulation.
They copy data back and forth between JVM and native.

For example, if you indend to mutate struct fields,

1. replace whole struct with new one
2. use pointer type for mutable struct fields





