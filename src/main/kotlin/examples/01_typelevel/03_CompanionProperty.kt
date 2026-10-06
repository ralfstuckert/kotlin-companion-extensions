package examples.typelevel

// Properties work the same way - `val`, `var` and `const val` on type level.
companion val User.Anonymous: User
    get() = User("Anonymous")

companion var User.defaultName: String = "Anonymous"

const companion val User.MAX_NAME_LENGTH: Int = 100

fun main() {
    println(User.Anonymous)

    println(User.defaultName)
    User.defaultName = "Guest"
    println(User.defaultName)

    println(User.MAX_NAME_LENGTH)
}
