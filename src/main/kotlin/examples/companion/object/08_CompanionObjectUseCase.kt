package examples.companion.`object`

data class User(
    val name: String,
    val age: Int
) {
    // companion implements interface
    companion object : Comparator<User> {
        override fun compare(a: User, b: User) =
            a.name.compareTo(b.name)
    }
}

val users = listOf(
    User("Bob", 42),
    User("Alice", 35)
)

fun main() {
    val sorted = users.sortedWith(User)
    println(sorted)
}
