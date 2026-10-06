package examples.companion.block

class RegisteredUser  constructor(val name: String) {
    companion {
        fun create(name: String) = RegisteredUser(name)

        val Anonymous: RegisteredUser
            get() = RegisteredUser("Anonymous")

        operator fun invoke(number: Long) = RegisteredUser("RegisteredUser #$number")

        operator fun of(vararg names: String): RegisteredUser = RegisteredUser(names.joinToString(", ", "RegisteredUser of [", "]"))
    }
}

companion operator fun String.invoke(value:String): RegisteredUser = RegisteredUser(value) // extension function on String companion

fun main() {
    println(RegisteredUser.create("Carol").name)
    println(RegisteredUser.Anonymous.name)
    println(RegisteredUser(42).name)
    // No RegisteredUser.Companion object is created by the companion block.
    //    println(RegisteredUser.Companion)  -> does not compile
    println(RegisteredUser.of("Alice", "    Bob").name)
    println(String("Eve").name)
}
