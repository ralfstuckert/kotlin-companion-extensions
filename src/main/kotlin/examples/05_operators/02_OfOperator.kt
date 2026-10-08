package examples.operators

class Tags private constructor(val values: List<String>) {

    companion {
        operator fun of(vararg values: String): Tags = Tags(values.toList())

        val Empty: Tags = Tags(emptyList())
    }

    override fun toString() = values.joinToString(", ", "Tags[", "]")
}

// companion operator fun Tags.of(...)  -> does not compile

fun main() {
    // Explicit call ...
    println(Tags.of("kotlin", "keep"))

    // ... and the collection literal that desugars to it.
    // Requires the -Xcollection-literals compiler flag.
    val tags: Tags = ["kotlin", "keep"]
    println(tags)

    println(Tags.Empty)
}
