package examples.backingfields

data class Config(val name: String)

// A regular extension property can never have a backing field: it is pure
// syntax sugar for a getter, so every access recomputes the value.
//
//   val Config.cached: String = compute()   -> "extension property cannot be initialized"
//
val Config.derived: String
    get() = "derived from ${this.name}"

fun main() {
    val config = Config("prod")
    println(config.derived)
    println(config.derived) // computed again
}
