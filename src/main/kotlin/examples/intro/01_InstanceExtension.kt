package examples.intro

data class User(val name: String)

fun User.greet(): String = "Hello ${this.name}"

fun main() {
    val user = User("Alice")
    println(user.greet())
}
