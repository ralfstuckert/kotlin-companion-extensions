package examples.statics

class Parser private constructor(val source: String) {

    companion {
        fun parse(source: String) = Parser(source)

        val DEFAULT: Parser = Parser("")
    }
}

companion fun Parser.strict(source: String): Parser = Parser.parse(source.trim())


fun main() {
    println(Parser.parse("x").source)
    println(Parser.DEFAULT.source)
    println(Parser.strict("  y  ").source)
}
