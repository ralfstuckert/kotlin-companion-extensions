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

// But the extension is thinner than it looks. It is not added to the companion
// object - that class was compiled long ago - it becomes a static function in
// *this* file's class, taking the companion as an argument:
//
//   ./gradlew javapMotivation
//
//   public final class examples.motivation._01_CompanionObjectExtensionKt {
//     public static final Color getBrandPrimary(Color$Companion);
//   }
//
// A getter, and nothing else: there is no field anywhere to put a value in, so
// an extension property may only ever have a `get()`.
//
//   val Color.Companion.BrandPrimary: Color = Color(0x6750A4)
//   -> e: Extension property cannot be initialized because it has no backing field.
//
// Every access therefore allocates a new Color, while `Black` - a real static
// field on the other side of the fence - is read once. See 03_backingfields for
// how a companion extension lifts exactly this restriction.

// The catch: it only works because there *is* a companion object. The author
// of `LocalDate` never wrote one, so there is nothing to extend:
//
//   fun LocalDate.Companion.fromGerman(value: String): LocalDate   // <- does not compile
//   e: Unresolved reference 'Companion'.
//
// See 02_javatypes/01_JavaTypeExtension.kt for how companion extensions fix this.

fun main() {
    println(Color.Black)
    println(Color.fromHex("#6750A4"))
    println(Color.BrandPrimary) // the companion *object* extension

    // What the compiler actually emits for `Color.Black` is an instance call:
    //   Color.Companion.INSTANCE.getBlack()
    println(Color.Companion.Black)
    println(Color.Companion) // the companion object is a real value

    println(LocalDate.of(2026, 10, 1))
}
