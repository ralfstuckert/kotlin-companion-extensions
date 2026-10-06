package examples.companion.extension

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
