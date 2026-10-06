package examples.multiplatform

// Problem #5 in the KEEP: expect/actual matching.
//
// A companion block is what finally lets an `expect` class be actualized by a
// platform type whose members are statics (KEEP §4.1.5). With a companion
// *object* this is impossible: the expected `Timeout.Companion.ofSeconds`
// has no counterpart in a Java class.
expect class Timeout {
    companion {
        fun ofSeconds(seconds: Long): Timeout
    }

    fun toMillis(): Long
}

fun describeTimeout(): String {
    val timeout = Timeout.ofSeconds(30)
    return "timeout   = ${timeout.toMillis()} ms"
}
