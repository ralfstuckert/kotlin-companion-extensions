# Constructor-like Factories: invoke() and of()
Operators are forbidden in companion blocks, since a type name is not a value, e.g. something like
`Money + 1` must never become legal. There are exactly two exceptions, the `invoke` operator:

[01_InvokeOperator.kt](../src/main/kotlin/examples/05_operators/01_InvokeOperator.kt):
```kotlin
class Money private constructor(val cents: Long) {
    companion {
        operator fun invoke(euros: Long) = Money(euros * 100)
    }
}

Money(5)   // 5.00 EUR - no public constructor in sight
```

so you can setup constructor like functions, even for Java types using companition extensions :-)

And the `of` operator:

[02_OfOperator.kt](../src/main/kotlin/examples/05_operators/02_OfOperator.kt):
```kotlin
class Tags private constructor(val values: List<String>) {
    companion {
        operator fun of(vararg values: String) = Tags(values.toList())
    }
}

val tags: Tags = ["kotlin", "keep"]   // Tags[kotlin, keep]
```

`of` is what collection literals desugar to, behind `-Xcollection-literals`. One asymmetry worth half a
sentence: as already said, `invoke` may also be a companion extension, so you can bolt a constructor-like
factory onto a foreign type, while `of` may not.

Next: [Resolution and Limitations](resolution_and_limitations.md)
