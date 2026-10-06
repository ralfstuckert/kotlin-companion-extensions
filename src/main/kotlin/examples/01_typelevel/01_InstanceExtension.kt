package examples.typelevel

data class User(val name: String)

// A classic extension: the receiver is an *instance* of User.
fun User.greet(): String = "Hello ${this.name}"

fun main() {
    val user = User("Alice")
    println(user.greet())

    // There is no way to call this through the type itself:
    //   User.greet()   -> does not compile
}
