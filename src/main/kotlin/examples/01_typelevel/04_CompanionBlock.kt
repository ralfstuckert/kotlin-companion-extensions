package examples.typelevel

// The same `Color` as in 00_motivation, written the way its author would write
// it with KEEP-0449: `companion object` loses the `object`.
//
//   companion object { val Black = Color(0x000000) }   // today
//   companion        { val Black = Color(0x000000) }   // new
//
// Same call syntax, but no classifier and no instance behind it.
class Color(val rgb: Int) {

    companion {
        val Black: Color = Color(0x000000)

        fun fromHex(hex: String): Color = Color(hex.removePrefix("#").toInt(16))
    }

    // Companion block members are visible unqualified inside the class.
    fun isBlack(): Boolean = rgb == Black.rgb

    override fun toString() = "#%06X".format(rgb)
}

// A companion block does not create a classifier, so there is nothing to
// reference as a value:
//   println(Color.Companion)   -> does not compile: no such classifier
//   val c: Any = Color         -> does not compile: a type name is not an expression
//
// Only functions and properties are allowed in the block - in particular there
// is no `init`.

// A companion block member is part of the class and therefore sees its private
// members; a companion extension is written from the outside and does not. So a
// companion extension can never be a factory for a type that hides its
// constructor.

fun main() {
    println(Color.Black)
    println(Color.fromHex("#6750A4"))
    println(Color.fromHex("#000000").isBlack())
}
