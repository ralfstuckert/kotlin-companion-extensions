package examples.statics

// The multiplatform side of the story - per-platform static annotations
// (problem #3) and expect/actual matching (problem #5) - needs more than one
// target, so it lives in its own subproject:
//
//   multiplatform/src/commonMain/.../Clock.kt      expect class + companion block
//   multiplatform/src/jvmMain/.../Clock.jvm.kt     actual, compiled to a JVM static
//   multiplatform/src/jsMain/.../Clock.js.kt       actual for JS
//   multiplatform/src/commonMain/.../Timeout.kt    expect class ...
//   multiplatform/src/jvmMain/.../Timeout.jvm.kt   ... actualized by java.time.Duration
//
// Run it:
//
//   ./gradlew :multiplatform:runJvm
//   ./gradlew :multiplatform:jsNodeDevelopmentRun
//   ./gradlew :multiplatform:javapMultiplatform

fun main() {
    println("See the `multiplatform` subproject - ./gradlew :multiplatform:runJvm")
}
