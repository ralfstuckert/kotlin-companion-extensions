package examples.statics

// A companion block compiles to plain static members of the enclosing class -
// no annotation, no Companion class, no instance allocation (KEEP §4.1.1).
class Parser private constructor(val source: String) {

    companion {
        fun parse(source: String) = Parser(source)

        val DEFAULT: Parser = Parser("")
    }
}

// Companion extensions become static members of the file class `...Kt`,
// with the receiver type erased from the signature (KEEP §4.1.3).
// They live outside the class, so they see no private members - unlike a
// member of a companion block, which is part of the class.
companion fun Parser.strict(source: String): Parser = Parser.parse(source.trim())

// Verify it:
//
//   ./gradlew compileKotlin
//   javap -p build/classes/kotlin/main/examples/statics/Parser.class
//   javap -p 'build/classes/kotlin/main/examples/statics/02_CompanionBlockStaticKt.class'
//
// Parser.class:
//   private static final Parser DEFAULT;
//   public static final Parser parse(java.lang.String);
//   public static final Parser getDEFAULT();
//   static {};
//
// There is no Parser$Companion class at all.
//
// Caveat: a static and an instance method may not share a signature on the
// JVM. Use @JvmName on one of them to resolve the clash (KEEP §4.1.2).

fun main() {
    println(Parser.parse("x").source)
    println(Parser.DEFAULT.source)
    println(Parser.strict("  y  ").source)
}
