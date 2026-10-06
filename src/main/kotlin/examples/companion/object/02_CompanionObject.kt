package examples.companion.`object`

class UserWithCompanion private constructor(val name: String) {
    companion object {
        fun anonymous() = UserWithCompanion("Anonymous")
    }
}

fun main() {
    val user = UserWithCompanion.anonymous()
    val companion = UserWithCompanion.Companion

    println(user.name)
    println(companion)
}
