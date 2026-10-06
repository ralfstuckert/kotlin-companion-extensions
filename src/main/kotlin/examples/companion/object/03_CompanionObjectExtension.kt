package examples.companion.`object`

class ExtensibleUser(val name: String) {
    companion object
}

fun ExtensibleUser.Companion.anonymous(): ExtensibleUser {
    // `this` is the actual ExtensibleUser.Companion singleton.
    println("receiver = $this")
    return ExtensibleUser("Anonymous")
}

fun main() {
    println(ExtensibleUser.anonymous().name)
}
