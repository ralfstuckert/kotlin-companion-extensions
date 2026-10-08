package examples.motivation

import java.time.LocalDate

// Say some UI library gives us a `Color`. Its author wrote a companion object,
// so `Black` and `fromHex` are reachable through the *type* name.
class Color(val rgb: Int) {
    companion object {
        val Black = Color(0x000000)

        fun fromHex(hex: String) = Color(hex.removePrefix("#").toInt(16))
    }

    override fun toString() = "#%06X".format(rgb)
}

// Extending a type on type level is *not* new: the companion object is an
// ordinary class, so we can write an extension for it. This has worked since
// Kotlin 1.0.
val Color.Companion.BrandPrimary: Color get() = Color(0x6750A4)


fun main() {
    println(Color.Black)
    println(Color.fromHex("#6750A4"))
    println(Color.BrandPrimary) // the companion *object* extension

    // What the compiler actually emits for `Color.Black` is an instance call:
    //   Color.Companion.INSTANCE.getBlack()
    println(Color.Companion.Black)
    println(Color.Companion) // the companion object is a real value
    println(Color)

    println(LocalDate.of(2026, 10, 1))
}
