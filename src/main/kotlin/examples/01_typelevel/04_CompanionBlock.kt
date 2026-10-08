package examples.typelevel

class Color(val rgb: Int) {

    companion {
        val Black: Color = Color(0x000000)

        fun fromHex(hex: String): Color = Color(hex.removePrefix("#").toInt(16))
    }

    // Companion block members are visible unqualified inside the class.
    fun isBlack(): Boolean = rgb == Black.rgb

    override fun toString() = "#%06X".format(rgb)
}


fun main() {
    println(Color.Black)
    println(Color.fromHex("#6750A4"))
    println(Color.fromHex("#000000").isBlack())
}
