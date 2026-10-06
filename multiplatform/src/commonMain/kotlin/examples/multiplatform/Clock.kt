package examples.multiplatform

// Problem #3 in the KEEP: with a companion *object*, "make this a static
// member" has to be spelled out once per target, and the annotations are not
// available in common code at all:
//
//   expect class Clock {
//       companion object {
//           fun system(): Clock                           // commonMain
//       }
//   }
//   actual class Clock {
//       actual companion object {
//           @JvmStatic actual fun system(): Clock = ...   // jvmMain
//           @JsStatic  actual fun system(): Clock = ...   // jsMain
//       }
//   }
//
// A companion block replaces all of them: the declaration itself says
// "type level", and each backend maps it to its own notion of static.
//
// Members of a companion block may omit their body only when they are
// `expect` or `external` (KEEP §1.2.5), so nothing static is generated on the
// expect side - the static materializes in the actual.
expect class Clock {
    companion {
        fun system(): Clock
    }

    val epochMillis: Long
}
