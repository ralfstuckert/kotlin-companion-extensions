package examples.statics

// The classic way to get a JVM static member out of Kotlin:
// a companion *object* plus a platform-specific annotation.
class LegacyParser private constructor(val source: String) {

    companion object {
        @JvmStatic
        fun parse(source: String) = LegacyParser(source)

        // Without @JvmStatic this is only reachable from Java as
        // LegacyParser.Companion.getDEFAULT()
        val DEFAULT: LegacyParser = LegacyParser("")
    }
}

// Even with @JvmStatic the Companion class and its singleton instance are
// still generated and allocated (KEEP problem #4):
//
//   ./gradlew compileKotlin
//   javap -p -c build/classes/kotlin/main/examples/statics/LegacyParser*.class
//
// shows `LegacyParser$Companion`, a `public static final Companion Companion`
// field, and the static `parse` delegating to `Companion.INSTANCE.parse(...)`.

fun main() {
    println(LegacyParser.parse("x").source)
    println(LegacyParser.Companion) // the companion object is a real value
}
