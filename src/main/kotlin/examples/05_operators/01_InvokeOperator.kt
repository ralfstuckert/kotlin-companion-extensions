package examples.operators

import java.util.UUID

class Money private constructor(val cents: Long) {

    companion {
        // Fake constructor: Money(...) without exposing the real constructor.
        operator fun invoke(euros: Long): Money = Money(euros * 100)
    }

    override fun toString() = "%d.%02d EUR".format(cents / 100, cents % 100)
}

companion operator fun UUID.invoke(value: String): UUID = UUID.fromString(value)

fun main() {
    println(Money(5)) // 5.00 EUR - no public constructor in sight

    // A Java type gains a constructor-like factory via a companion extension.
    println(UUID("00000000-0000-0000-0000-000000000001"))
}
