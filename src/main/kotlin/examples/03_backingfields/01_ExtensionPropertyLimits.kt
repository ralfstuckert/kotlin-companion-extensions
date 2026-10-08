package examples.backingfields

data class Config(val name: String)

private fun compute(): String = "computed"

//   val Config.cached: String = compute()
//   -> e: Extension property cannot be initialized because it has no backing field.
//
val Config.derived: String
    get() = "derived from ${this.name}"


fun main() {
    val config = Config("prod")
    println(config.derived)
    println(config.derived) // computed again

    println(compute())
}
