package examples.companion.extension

// User has no companion object.
data class User(val name: String)

companion fun User.anonymous(): User = User("Anonymous")

companion fun User.named(name: String): User = User(name)

fun main() {
    println(User.anonymous())
    println(User.named("Bob"))

    // There is no User.Companion object and no value receiver available as `this`.
}
