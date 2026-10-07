package examples.operators

import java.util.UUID

// A type name is not a value, so operators are generally forbidden in
// companion blocks - `Money + 1` must not become legal.
//
// `invoke` and `of` are the two exceptions: there the type name acts as a
// scoping mechanism, not as a value.
class Money private constructor(val cents: Long) {

    companion {
        // Fake constructor: Money(...) without exposing the real constructor.
        operator fun invoke(euros: Long): Money = Money(euros * 100)
    }

    override fun toString() = "%d.%02d EUR".format(cents / 100, cents % 100)
}

// `invoke` is also the only operator allowed as a companion *extension*
// (KEEP §1.3.4) - so constructor-like factories can be bolted onto foreign
// types, including Java ones that never had a companion object to extend.
companion operator fun UUID.invoke(value: String): UUID = UUID.fromString(value)

fun main() {
    println(Money(5)) // 5.00 EUR - no public constructor in sight

    // A Java type gains a constructor-like factory via a companion extension.
    println(UUID("00000000-0000-0000-0000-000000000001"))
}
