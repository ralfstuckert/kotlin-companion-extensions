package examples.operators

// A type name is not a value, so operators are generally forbidden in
// companion blocks - `Money + 1` must not become legal.
//
// `invoke` and `of` are the two exceptions: there the type name acts as a
// scoping mechanism, not as a value.
class Money private constructor(val cents: Long) {

    companion {
        // Fake constructor: Money(...) without exposing the real constructor.
        operator fun invoke(euros: Long): Money = Money(euros * 100)

        operator fun invoke(euros: Long, cents: Long): Money = Money(euros * 100 + cents)
    }

    override fun toString() = "%d.%02d EUR".format(cents / 100, cents % 100)
}

// `invoke` is also the only operator allowed as a companion *extension*
// (KEEP §1.3.4) - so fake constructors can be added to foreign types too.
companion operator fun Money.invoke(formatted: String): Money {
    val (euros, cents) = formatted.split(".")
    return Money(euros.toLong(), cents.toLong())
}

fun main() {
    println(Money(5))
    println(Money(5, 42))
    println(Money("5.42")) // via the companion extension
}
