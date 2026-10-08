package examples.backingfields

private fun loadDefaults(): List<String> {
    println("  ... expensive load happens exactly once")
    return listOf("timeout=30", "retries=3")
}

companion val Config.defaults: List<String> = loadDefaults()

companion var Config.activeProfile: String = "dev"

// The backing field is a private static field of the enclosing `...Kt` class,
// so `@JvmField` and friends are available too.
@JvmField
companion val Config.VERSION: String = "1.0"


fun main() {
    println(Config.defaults)
    println(Config.defaults) // same instance, no recomputation
    println(Config.defaults === Config.defaults)

    println(Config.activeProfile)
    Config.activeProfile = "prod"
    println(Config.activeProfile)

    println(Config.VERSION)
}
