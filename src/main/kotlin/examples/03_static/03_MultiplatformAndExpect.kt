package examples.statics

// --- Problem #3: one annotation per platform -------------------------------
//
// With companion objects, "make this a static member" has to be spelled out
// per target, and the annotations are not available in common code:
//
//   expect class Clock {
//       companion object {
//           fun system(): Clock          // commonMain - no annotation possible
//       }
//   }
//
//   actual class Clock {
//       actual companion object {
//           @JvmStatic actual fun system(): Clock = ...   // jvmMain
//           @JsStatic  actual fun system(): Clock = ...   // jsMain
//       }
//   }
//
// A companion block replaces all of them: it is the declaration itself that
// says "type level", and every backend maps it to its own notion of static -
// JVM static members (§4.1.1), JS `static` members (§4.2.1), Swift
// `@_nonoverride static` type methods (§4.3.3).
//
//   expect class Clock {
//       companion {
//           fun system(): Clock
//       }
//   }

// --- Problem #5: expect/actual matching ------------------------------------
//
// Members of a companion block are the only ones that may stay abstract in a
// companion block, and only because they are `expect` (KEEP §1.2.5).
// An `expect` declaration has no body, so nothing is compiled to a static
// member on the expect side - the static only materializes in the actual.
//
// This is what finally allows an expect class to be actualized by a Java type
// whose members are statics (KEEP §4.1.5):
//
//   // commonMain
//   expect class Duration {
//       companion {
//           fun ofSeconds(seconds: Long): Duration
//       }
//   }
//
//   // jvmMain - java.time.Duration.ofSeconds is a Java static method
//   actual typealias Duration = java.time.Duration
//
// With a companion *object* this does not work: the expected
// `Duration.Companion.ofSeconds` has no counterpart in the Java class.
//
// A companion extension receiver may itself be a type alias, and is resolved
// against the expansion (KEEP §1.3.3) - which is what makes the actualization
// above usable from common code.

fun main() {
    println("See the comments in this file: multiplatform statics and expect/actual.")
}
