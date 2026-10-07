package examples.typelevel

// `User` has no companion object - and it does not need one.
// The `companion` modifier lifts the extension from the instance to the *type*.
companion fun User.parse(line: String): User = User(line.substringBefore(','))

companion fun User.named(name: String): User = User(name)

// Companion extensions are top-level only (KEEP §1.3.1) and the receiver type
// must be a plain classifier (KEEP §1.3.2):
//   companion fun <T> T.broken(): T   -> does not compile

companion fun User.describe(): String {
    // There is no `this` here: a companion extension has no value receiver,
    // and `User.Companion` does not exist either.
    return "User instances have a name"
}

fun main() {
    println(User.parse("Alice,42"))
    println(User.named("Bob"))
    println(User.describe())
}
