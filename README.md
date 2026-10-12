# Kotlin Companion Extensions and Blocks
This repository provides examples for the corresponding article on medium
that let you explore the capabilities of Kotlin companion blocks and companion extensions
([KEEP-0449](https://github.com/Kotlin/KEEP/blob/main/proposals/KEEP-0449-companions-block-extension.md)).

- [Introduction](#Introduction)
- [Companion Extensions](doc/companion_extensions.md)
- [Companion Blocks](doc/companion_blocks.md)
- [Use Cases](doc/use_cases.md)
    - [Extending Types You Don't Own](doc/extending_foreign_types.md)
    - [Extension Properties With a Backing Field](doc/backing_fields.md)
    - [Statics Without @JvmStatic](doc/statics.md)
    - [Constructor-like Factories: invoke() and of()](doc/factory_operators.md)
- [Resolution and Limitations](doc/resolution_and_limitations.md)


## Running the Examples
The project uses **Kotlin 2.5.0-Beta1** and the experimental compiler options
`-Xcompanion-blocks-and-extensions`, `-Xcollection-literals` and `-Xcontext-parameters`.

Every example has its own `main`, exposed as a Gradle task in the `examples` group and named
after its file:

```bash
./gradlew run02_CompanionFunction
./gradlew run01_InvokeOperator
```

Run all of them in topic order, or the JUnit example:

```bash
./gradlew runAll
./gradlew test
```

The bytecode shown throughout the chapters can be reproduced with:

```bash
./gradlew javapMotivation
./gradlew javapBackingFields
./gradlew javapStatics
```


# Introduction

> Companion objects have been Kotlin's answer to static members since 1.0. With Kotlin 2.5.0,
> [KEEP-0449](https://github.com/Kotlin/KEEP/blob/main/proposals/KEEP-0449-companions-block-extension.md)
> introduces two new experimental features — companion blocks and companion extensions — that finally
> decouple "accessible through the type name" from "there is an object behind it". After a recap of why
> the companion object was not quite enough, this article walks through the syntax and the use cases.

## Companion Objects
Kotlin has no `static` keyword. Everything that in Java would be a class member goes into a companion
object instead. Say some UI library gives us a `Color`:

[01_CompanionObjectExtension.kt](src/main/kotlin/examples/00_motivation/01_CompanionObjectExtension.kt):
```kotlin
class Color(val rgb: Int) {
    companion object {
        val Black = Color(0x000000)

        fun fromHex(hex: String) = Color(hex.removePrefix("#").toInt(16))
    }
}
```

The companion object is an ordinary object — it can implement interfaces, it can be passed around as a
value, it has a name — with one piece of magic on top: its members are reachable through the name of the
_type_. We can write `Color.Black` and `Color.fromHex("#6750A4")` instead of `Color.Companion.Black`.

Now our app has brand colors. They have no business in a general-purpose UI library, but we would very
much like to reach them the same way we reach `Black`. And because the companion is an ordinary object,
we can simply write an extension for it:

```kotlin
val Color.Companion.BrandPrimary get() = Color(0x6750A4)

val header = Color.BrandPrimary
```

`BrandPrimary` lives in our own file, far away from the library, and is still reachable through the type
name exactly like `Black` is. But let's see what the compiler actually produces for `Color.Black`:

```kotlin
Color.Companion.getBlack()
```

So after all it is just syntactic sugar. `Color.Black` looks like a static function but is just a shorcut
for accessing the companions object member. That observation is the key to this whole article. A companion
object bundles two quite different things: _being reachable through the type name_, and _being an object_.
Everything KEEP-0449 does comes from pulling those two apart.

## So What's Missing
Two things, as it turns out. Extending the companion object only worked because there _was_ one.
`companion object` is an ordinary class, and you cannot write an extension for a class that does not
exist — so if the author of `Color` had not written those two words, `Color.BrandPrimary` would be out of
reach, and we had to introduce some kind of `ColorUtils` object. Whether a type can be extended on type
level is decided by its author, usually without a thought, and the decision is final for everybody else.

This is an old complaint. [KT-11968](https://youtrack.jetbrains.com/issue/KT-11968) —
_"Research and prototype namespace-based solution for statics and static extensions"_ — was filed in
April 2016, and the KEEP still cites it as one of the issues it closes.

And where it does work, what you get is thinner than it looks. `BrandPrimary` is at last the same as a
regular extension property, which means it is a static getter taking the companion as a receiver. There is
no field anywhere to put a value in, which is why an extension property may only ever have a `get()`.

```kotlin
public static final Color getBrandPrimary(Color$Companion $this$BrandPrimary);
```

The second is that even the types we _can_ extend are not in great shape. Here is what `Color` actually
compiles to:

```kotlin
public final class Color {
  public static final Color$Companion Companion;   // a field, always initialized
  private static final Color Black;
  public static final Color access$getBlack$cp();  // plus a synthetic accessor
  static {};
}
```

Plus a second class file, `Color$Companion.class`, for a companion holding one constant. Every type with a
companion object pays for an extra class to load and an object to allocate, touched or not — irrelevant for
one `Color`, and worse on Kotlin/Native, where each access may have to check whether that instance exists yet.

And after all that we still do not have a static. Where something outside Kotlin insists on a real one, we
have to ask for it by hand. JUnit 4, for instance, requires `@BeforeClass` to be `public static void`:

```kotlin
class Tests {
    companion object {
        @BeforeClass     // forget @JvmStatic ...
        fun setUpClass() { database = connect() }
    }
}
```

We forgot about the `@JvmStatic`, and nothing will tell you. It compiles, the test runs, the fixture is
never set up, and gives you a `UninitializedPropertyAccessException`. And for multiplatform projects things
get even more complicated: you have to remember a different annotation for every target in a multiplatform
library — `@JvmStatic`, `@JsStatic`, and so on.

So the real complaint is that `companion object` is doing two jobs at once. It is the only way to say
_"reachable through the type name"_, and it is also — unavoidably — an object with a class, an instance and
an initialization order attached. The five problems the KEEP lists split cleanly along that seam: one
language problem (no companion, no extension) and four codegen problems (`@JvmStatic`, one annotation per
platform, the allocation, and no `expect`/`actual` matching against platform statics).

Which is why the solution comes in two parts.

Next: [Companion Extensions](doc/companion_extensions.md)
