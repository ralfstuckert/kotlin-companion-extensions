package examples.references

// A type with a companion *block* ...
class Color(val rgb: Int) {

    companion {
        val Black: Color = Color(0x000000)

        fun fromHex(hex: String): Color = Color(hex.removePrefix("#").toInt(16))
    }

    override fun toString() = "#%06X".format(rgb)
}

// ... and the same thing written with a companion *object*, for comparison.
class LegacyColor(val rgb: Int) {

    companion object {
        val Black = LegacyColor(0x000000)

        fun fromHex(hex: String) = LegacyColor(hex.removePrefix("#").toInt(16))
    }

    override fun toString() = "#%06X".format(rgb)
}

// A great deal of Kotlin infrastructure - serializers, DI, test runners -
// discovers things by asking a class for its companion object and checking
// what it implements. That pattern needs the companion to *be* a value.
interface JsonFactory<T> {
    fun fromJson(json: String): T
}

class User(val name: String) {
    companion object : JsonFactory<User> {
        override fun fromJson(json: String) = User(json.trim('"'))
    }

    override fun toString() = "User($name)"
}

// This cannot be migrated to a companion block: a block has no classifier and
// therefore cannot implement an interface at all.
//
//   class User(val name: String) {
//       companion : JsonFactory<User> { ... }   -> syntax error, there is no
//   }                                              supertype list to write here
