package examples.references

import java.time.LocalDate
import kotlin.reflect.KProperty0
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.staticFunctions
import kotlin.reflect.full.staticProperties

// Companion block members import like Java statics (KEEP §1.2.6).
import examples.references.Color.fromHex
import examples.references.Color.Black

import examples.javatypes.fromGerman

// Companion extensions get no such treatment yet - importing them alongside
// their type is an intention in the KEEP, not a feature.

private inline fun <reified T : Any> parseWith(json: String): T? {
    @Suppress("UNCHECKED_CAST")
    val factory = T::class.companionObjectInstance as? JsonFactory<T>
    return factory?.fromJson(json)
}

fun main() {
    val hexes = listOf("#6750A4", "#000000")

    // There is no receiver to bind, so a reference to a companion member is
    // simply a function.
    println(hexes.map(Color::fromHex))                 // companion block member
    println(hexes.map(LegacyColor.Companion::fromHex)) // the companion object equivalent

    // Companion extensions too - even on a JDK type.
    println(listOf("01.10.2026", "24.12.2026").map(LocalDate::fromGerman))

    // Note the zero in KProperty0: there is nothing to pass to get().
    val black: KProperty0<Color> = Color::Black
    println(black.get())

    // Imported unqualified, like a Java static import.
    println(fromHex("#6750A4"))
    println(Black)

    // Reflection: ask the class for its companion and there is nothing to get.
    println("companionObject  -> ${Color::class.companionObject}")
    println("memberFunctions  -> ${Color::class.memberFunctions.map { it.name }.sorted()}")
    println("staticFunctions  -> ${Color::class.staticFunctions.map { it.name }.sorted()}")
    println("staticProperties -> ${Color::class.staticProperties.map { it.name }.sorted()}")

    // The members are all there, just filed under *static* rather than
    // *member*. What does not survive is the discovery pattern above:
    println(parseWith<User>("\"Alice\"")) // works: User has a companion object
    println(parseWith<Color>("\"#000000\"")) // null: nothing to find, and nothing to fix
}
