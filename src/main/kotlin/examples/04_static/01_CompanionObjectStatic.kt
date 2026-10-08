package examples.statics

class LegacyParser private constructor(val source: String) {

    companion object {
        @JvmStatic
        fun parse(source: String) = LegacyParser(source)

        // Without @JvmStatic this is only reachable from Java as
        // LegacyParser.Companion.getDEFAULT()
        val DEFAULT: LegacyParser = LegacyParser("")
    }
}


fun main() {
    println(LegacyParser.parse("x").source)
    println(LegacyParser.Companion) // the companion object is a real value
}
