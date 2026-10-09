# Companion Blocks
Companion extensions are written from the outside, by people who do _not_ own the type. They answer the
first of our two problems: the one where a companion object is missing and we cannot add one.

They do nothing about the second: the allocation, the extra class file, the `@JvmStatic` dance — all of
that belongs to the author of the type, and no amount of extending from outside will fix it. That is what
companion blocks are for. Here is the author of our `Color` library, before and after:

[01_CompanionObjectExtension.kt](../src/main/kotlin/examples/00_motivation/01_CompanionObjectExtension.kt):
```kotlin
class Color(val rgb: Int) {
    companion object {              //  <- today
        val Black = Color(0x000000)
    }
}
```

[04_CompanionBlock.kt](../src/main/kotlin/examples/01_typelevel/04_CompanionBlock.kt):
```kotlin
class Color(val rgb: Int) {
    companion {                     //  <- new
        val Black = Color(0x000000)
    }
}
```

One word shorter, and that word is the whole point. A companion block keeps the _companion_ part and drops
the _object_ part. Its members are reachable through the type name and take part in context-sensitive
resolution, just as before — but there is no classifier and no instance behind them. You see the difference
the moment you try to treat the companion as a thing:

```kotlin
println(Color.Companion)    // does not compile: there is no such classifier
val c: Any = Color          // does not compile: a type name is not an expression
```

With a companion object, both of those are legal. With a companion block they are not, and that is by
design. If you need an object — to implement an interface, to pass around, to use as a receiver — then a
companion object is still the right tool, and it is not going anywhere. What does still work is the
convenience you expect from a class member:

```kotlin
class Color(val rgb: Int) {
    companion {
        val Black = Color(0x000000)
    }

    fun isBlack(): Boolean = rgb == Black.rgb   // unqualified, from inside the class
}
```

Only function and property declarations are allowed in a companion block. In particular there is no `init`
block — deliberately, since class initialization is hard enough to reason about without a general-purpose
hook in it.

It is worth being precise about what a companion block buys you, because it is _not_ expressiveness:
anything you can put in a block you could have put in a companion object. What changes is what the compiler
emits — and that is the whole point, since four of the five problems were about generated code. So the two
features look alike at the call site and are not interchangeable at all.

Next: [Use Cases](use_cases.md)
