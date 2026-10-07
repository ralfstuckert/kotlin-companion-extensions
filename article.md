# Kotlin Companion Extensions

*statics, without the object*

> Companion objects have been Kotlin's answer to static members since 1.0. With Kotlin 2.5.0, KEEP-0449
> introduces two new experimental features — *companion blocks* and *companion extensions* — that finally
> decouple "accessible through the type name" from "there is an object behind it". After a recap of why the
> companion object was not quite enough, this article walks through the syntax and the use cases.

- [Companion Objects](#1-companion-objects)
- [What's Missing](#2-whats-missing)
- [Companion Extensions](#3-companion-extensions)
- [Companion Blocks](#4-companion-blocks)
- [Use Cases](#5-use-cases)
  - [Extending Types You Don't Own](#extending-types-you-dont-own)
  - [Extension Properties With a Backing Field](#extension-properties-with-a-backing-field)
  - [Statics Without @JvmStatic](#statics-without-jvmstatic)
  - [Constructor-like Factories: invoke() and of()](#constructor-like-factories-invoke-and-of)
- [Resolution and Limitations](#6-resolution-and-limitations)
- [References, Imports and Reflection](#7-references-imports-and-reflection)
- [Conclusion](#8-conclusion)

## Companion Objects

Kotlin has no `static` keyword. Everything that in Java would be a class member goes into a companion
object instead. Say some UI library gives us a `Color`:

```kotlin
class Color(val rgb: Int) {
    companion object {
        val Black = Color(0x000000)

        fun fromHex(hex: String) = Color(hex.removePrefix("#").toInt(16))
    }
}
```

The companion object is an ordinary object — it can implement interfaces, it can be passed around
as a value, it has a name — with one piece of magic on top: its members are reachable through the name of
the *type*. We can write `Color.Black` and `Color.fromHex("#6750A4")` instead of `Color.Companion.Black`.
Now our app has brand colors. They have no business in a general-purpose UI library, but we would very much
like to reach them the same way we reach `Black`. And because the companion is an ordinary object, we can
simply write an extension for it:

```kotlin
val Color.Companion.BrandPrimary get() = Color(0x6750A4)

val header = Color.BrandPrimary
```

`BrandPrimary` lives in our own file, far away from the library, and is still reachable through the type
name exactly like `Black` is. This is worth stressing, because extending a type on type level is precisely
what companion extensions are usually introduced with — and we have been able to do it since Kotlin 1.0.
Hold that thought for the next section.

So far so good. But let's do what we did with extension functions last time, and look at what the compiler
actually produces for `Color.Black`:

```java
Color.Companion.INSTANCE.getBlack()
```

There it is. A nested `Companion` class, a singleton instance of it, and an instance method call. The
"static member" we wrote is not static at all — it is an instance member of an object that happens to be
named after its type.

That observation is the key to this whole article. A companion object bundles two quite different things:
*being reachable through the type name*, and *being an object*. Everything KEEP-0449 does comes from
pulling those two apart.

## 2. What's Missing

Two things, as it turns out.

The first is a catch in what we just did. Extending the companion object only worked because there *was*
one. `companion object` is an ordinary class, and you cannot write an extension for a class that does not
exist — so if the author of `Color` had not written those two words, `Color.BrandPrimary` would be out of
reach, and we would be back to a `ColorUtils` object. Whether a type can be extended on type level is
decided by its author, usually without a thought, and the decision is final for everybody else.

This is an old complaint. [KT-11968](https://youtrack.jetbrains.com/issue/KT-11968) — *"Research and
prototype namespace-based solution for statics and static extensions"* — was filed in April 2016, and the
KEEP still cites it as one of the issues it closes.

The second is that even the types we *can* extend are not in great shape. Here is what `Color` actually
compiles to:

```java
public final class Color {
  public static final Color$Companion Companion;   // a field, always initialized
  private static final Color Black;
  public static final Color access$getBlack$cp();  // plus a synthetic accessor
  static {};
}
```

Plus a second class file, `Color$Companion.class`, for a companion holding one constant. Every type with a
companion object pays for an extra class to load and an object to allocate, touched or not — irrelevant for
one `Color`, measurable start-up cost across a codebase the size of IntelliJ IDEA, and worse on
Kotlin/Native, where each access may have to check whether that instance exists yet.

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

... and nothing will tell you. It compiles, the test runs, the fixture is never set up, and you are left
debugging a `NullPointerException` three stack frames away from the cause. And for multiplatform projects things 
get even more complicated: you have to remember a different annotation for every
target in a multiplatform library — `@JvmStatic`, `@JsStatic`, and so on.

So the real complaint is that `companion object` is doing two jobs at once. It is the only way to say
*"reachable through the type name"*, and it is also, unavoidably, an object — with a class, an instance and
an initialization order attached. The five problems the KEEP lists split cleanly along that seam: one
language problem (no companion, no extension) and four codegen problems (`@JvmStatic`, one annotation per
platform, the allocation, and no `expect`/`actual` matching against platform statics).

Which is why the solution comes in two parts.

## 3. Companion Extensions

The first one is a single modifier:

```kotlin
companion fun User.anonymous(): User = User("Anonymous")
companion val User.Anonymous: User get() = User("Anonymous")
```

```kotlin
println(User.anonymous())   // User(name=Anonymous)
println(User.Anonymous)     // User(name=Anonymous)
```

`User` has no companion object here, and it does not need one. The `companion` modifier lifts the receiver
from the *instance* to the *type*: where `fun User.greet()` is called on a `User`, `companion fun
User.anonymous()` is called on `User` itself.

Which means there is nothing to call it *on*, and so there is no `this`:

```kotlin
companion fun User.describe(): String {
    // no `this` here - no value receiver, and no User.Companion either
    return "User instances have a name"
}
```

That is worth sitting with for a second. The receiver in a companion extension is not a value you receive;
it is a statement about *where the function may be found*. We will see this again in the generated code
later, where it disappears entirely.

Mutable and constant properties work as well:

```kotlin
companion var User.defaultName: String = "Anonymous"
const companion val User.MAX_NAME_LENGTH: Int = 100
```

There is one notable restriction: companion extensions may only be declared at **top level**. The reason is
resolution — allowing them in arbitrary nested scopes would make the rules considerably harder to pin down.
In the days before context parameters that would have been a painful limitation. Today it is much less so:
if such a function needs additional scope, it can simply ask for it with a `context` parameter.

## 4. Companion Blocks

Companion extensions are written from the outside, by people who do *not* own the type. They answer the
first of our two problems: the one where a companion object is missing and we cannot add one.

They do nothing about the second. The allocation, the extra class file, the `@JvmStatic` dance — all of
that belongs to the author of the type, and no amount of extending from outside will fix it. That is what
companion blocks are for. Here is the author of our `Color` library, before and after:

```kotlin
class Color(val rgb: Int) {
    companion object {              //  <- today
        val Black = Color(0x000000)
    }
}

class Color(val rgb: Int) {
    companion {                     //  <- new
        val Black = Color(0x000000)
    }
}
```

One word shorter, and that word is the whole point. A companion block keeps the *companion* part and drops
the *object* part. Its members are reachable through the type name and take part in context-sensitive
resolution, just as before — but there is no classifier and no instance behind them.

You see the difference the moment you try to treat the companion as a thing:

```kotlin
println(Color.Companion)    // does not compile: there is no such classifier
val c: Any = Color          // does not compile: a type name is not an expression
```

With a companion object, both of those are legal. With a companion block they are not, and that is by
design. If you need an object — to implement an interface, to pass around, to use as a receiver — then a
companion object is still the right tool, and it is not going anywhere.

What does still work is the convenience you expect from a class member:

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

It is worth being precise about what a companion block buys you, because it is *not* expressiveness:
anything you can put in a block you could have put in a companion object. What changes is what the compiler
emits — and that is the whole point, since four of the five problems were about generated code.

So the two features look alike at the call site and are not interchangeable at all:

| | companion block | companion extension |
| --- | --- | --- |
| written by | the author of the type | anybody |
| declared | inside the class body | at top level, in any file |
| sees `private` members | yes, it is part of the class | no, it is outside |
| needs an existing companion | — | no, that is the point |
| compiles to | a static member of the class | a static member of the file class |
| solves | the codegen problems (#2–#5) | the gatekeeper problem (#1) |

The `private` row is the one that bites in practice. A companion block member can call a private
constructor — which is exactly how a validating factory is normally built — while a companion extension
sees the type the way any other caller does. So you can add `LocalDate.fromGerman()`, because
`LocalDate.parse()` is public, but you cannot write a factory for a type that hides its constructor. The
author still decides how far you get.

## 5. Use Cases

### Extending Types You Don't Own

German dates are written `01.10.2026`, and `LocalDate.parse()` does not accept that, so a
`LocalDate.fromGerman()` would be genuinely useful. Until now it could not exist, for the reason from
section 2 — there is nothing to hang it on:

```kotlin
fun LocalDate.Companion.fromGerman(value: String) = LocalDate.parse(value, germanFormatter)
```

```text
e: Unresolved reference 'Companion'.
```

A JSR-310 author in 2011 did not write a companion object for a language that did not exist yet, so for a
decade the answer has been a `DateUtils` object holding a function that everybody can see belongs on
`LocalDate`. One modifier, and that is over:

```kotlin
companion fun LocalDate.fromGerman(value: String) = LocalDate.parse(value, germanFormatter)
companion fun UUID.zero() = UUID(0L, 0L)
companion fun File.temp(name: String) = File(System.getProperty("java.io.tmpdir"), name)
```

Java types, Kotlin built-ins, final classes, interfaces — the receiver only has to be a declared classifier
without type arguments. So `companion fun <T> T.broken()` and `companion fun List<Int>.broken()` are both
rejected, as are objects.

The statics that are already on the type keep working, and keep their priority:

```kotlin
LocalDate.of(2026, 10, 1)           // still the Java static
LocalDate.fromGerman("01.10.2026")  // yours
```

Delete the `Utils` object.

### Extension Properties With a Backing Field

That one was about a restriction that is simply lifted. This one is about a restriction that is lifted
*for a reason*, and the reason tells you a lot about what a companion extension really is.

Every Kotlin developer runs into this eventually:

```kotlin
val Config.derived: String get() = "derived from $name"  // recomputed on every access
val Config.cached: String = compute()                    // error: cannot be initialized
```

The reason is the one we saw with `max()`: an extension property is just a static getter taking the
receiver as a parameter. There is no field to put the value in, and Kotlin cannot add one to a class it
does not own.

A companion extension property is a different animal. Its receiver is the *type*, and there is exactly one
of those — so there is no "which instance owns this field?" to answer, and the KEEP grants an exemption:
an initializer, and with it a backing field.

```kotlin
companion val Config.defaults: List<String> = loadDefaults()  // runs once
companion var Config.activeProfile: String = "dev"            // mutable type-level state
```

The generated code shows where that field lives — and what is missing from it:

```java
private static final List defaults;         // a private static of the file class
public static final List getDefaults();     // no parameter!
static { defaults = loadDefaults(); }
```

A regular extension would have been `getDerived(Config $this$derived)`. A companion extension has no
receiver to pass, so the type is erased from the signature entirely — it only ever told the compiler under
which name you may call this.

Inside a `companion` block it works the same, except that the field belongs to the class itself, and
`const val` is initialized before everything else.

And the obvious warning: `companion var` is global mutable state. The feature lifts a technical
restriction, it does not improve the design.

### Statics Without @JvmStatic

```kotlin
class LegacyParser private constructor(val source: String) {
    companion object {
        @JvmStatic fun parse(source: String) = LegacyParser(source)
        val DEFAULT: LegacyParser = LegacyParser("")
    }
}

class Parser private constructor(val source: String) {
    companion {
        fun parse(source: String) = Parser(source)
        val DEFAULT: Parser = Parser("")
    }
}
```

Same call site, `Parser.parse("x")`, either way. The difference only shows up underneath:

```java
public final class LegacyParser {
  public static final LegacyParser$Companion Companion;   // allocated, always
  public static final LegacyParser parse(java.lang.String);
  public static final LegacyParser access$getDEFAULT$cp();
}

public final class Parser {
  public static final Parser parse(java.lang.String);
  public static final Parser getDEFAULT();
}
```

Everything section 2 complained about is simply absent: no `Parser$Companion` class file, no `Companion`
field to initialize, no synthetic accessor, no annotation to remember. Which also disarms the JUnit trap —
`@BeforeClass` needs no `@JvmStatic`, because there is nothing non-static left to opt out of:

```kotlin
class JUnitStaticTest {
    companion {
        lateinit var database: String

        @BeforeClass fun setUpClass() { database = "in-memory-db" }
    }
}
```

Static by construction, so the annotation alone is enough.

The same property holds across platforms, which takes care of problems #3 and #5: an `expect class` with a
`companion` block needs no `@JvmStatic` or `@JsStatic` anywhere, and on the JVM you can even write `actual
typealias Timeout = java.time.Duration` and let a *Java* static actualize a companion block member.

### Constructor-like Factories: invoke() and of()

Operators are forbidden in companion blocks, for the reason we saw in section 4 — if a type name is not a
value, `Money + 1` must never become legal. There are exactly two exceptions, both cases where the type
name acts as a scope rather than as a value.

```kotlin
class Money private constructor(val cents: Long) {
    companion {
        operator fun invoke(euros: Long) = Money(euros * 100)
    }
}

Money(5)   // 5.00 EUR - no public constructor in sight
```

```kotlin
class Tags private constructor(val values: List<String>) {
    companion {
        operator fun of(vararg values: String) = Tags(values.toList())
    }
}

val tags: Tags = ["kotlin", "keep"]   // Tags[kotlin, keep]
```

`of` is what collection literals desugar to, behind `-Xcollection-literals`. One asymmetry worth half a
sentence: `invoke` may also be a companion extension, so you can bolt a constructor-like factory onto a
foreign type, while `of` may not.

## 6. Resolution and Limitations

What happens when a type has both? The rule is short:

```kotlin
class Example {
    companion object {
        fun foo() = 1
    }
}

companion fun Example.foo() = 2

Example.foo()   // 2
```

`T.foo()` is resolved against companion blocks and extensions first, and only then as `T.Companion.foo()`.
Blocks and extensions form their own receiver, which is exhausted before the companion object is consulted.
If you need the other one, say so explicitly: `Example.Companion.foo()`.

> This is the part I would keep an eye on in practice. A companion extension declared in your own code
> takes precedence over a companion object member declared in a library — silently, and from anywhere at
> top level in that file's scope. Simple rule, but I suspect it will surprise somebody eventually.

The rest of the limitations, briefly:

- Companion extensions are **top-level only**.
- The receiver must be a plain classifier: no type parameters, no type arguments, no objects.
- No `init` in a companion block; only functions and properties.
- No classifier means no interface implementation and no passing it as a value — still a job for
  `companion object`.
- On the JVM, a static and an instance method may not share a signature. Use `@JvmName` if they clash.

## 7. References, Imports and Reflection

A new kind of member raises a practical question: does the rest of the language know about it? Function
references, mostly, behave the way you would hope. Since there is no receiver to bind, a reference to a
companion member is simply a function:

```kotlin
val hexes = listOf("#6750A4", "#000000")

hexes.map(Color::fromHex)                  // companion block member
hexes.map(LegacyColor.Companion::fromHex)  // the companion object equivalent

listOf("01.10.2026", "24.12.2026").map(LocalDate::fromGerman)   // a companion extension, on a JDK type
// [2026-10-01, 2026-12-24]
```

`Color::Black` works too, as a `KProperty0` — note the zero: nothing to pass to `get()`.

The resolution rule from the previous section turns out not to be a special case for calls; references
follow it exactly:

```kotlin
Example::foo             // the companion extension
Example.Companion::foo   // the companion object member
```

Which is worth knowing precisely because it is quiet. `Example::foo` handed to a `map()` somewhere picks
your extension over a library's companion member, with nothing at the call site to suggest a choice was
made.

Imports are the other half of day-to-day use, and here companion blocks behave like Java statics — the
KEEP says that is deliberate:

```kotlin
import com.example.Color.fromHex
import com.example.Color.Black

val c = fromHex("#6750A4")
```

Companion *extensions* get no such treatment yet. Automatically importing them alongside their type is
something the KEEP would like to explore, not something it specifies.

And then there is reflection, which is where a companion block stops being a drop-in replacement. Ask a
class for its companion and you get nothing, because there is nothing to get:

```text
Color::class.companionObject   -> null
Color::class.memberFunctions   -> [equals, hashCode, toString]
Color::class.staticFunctions   -> [fromHex]
Color::class.staticProperties  -> [Black]
```

The members are all there, just filed under *static* rather than *member* — the `staticXXX` family of
`kotlin-reflect` returns them, the `memberXXX` family does not.

That reshuffling is survivable. What is not is the pattern underneath it. A great deal of Kotlin
infrastructure — serializers, dependency injection, test runners — discovers things by asking for the
companion object and checking what it implements:

```kotlin
class User(val name: String) {
    companion object : JsonFactory<User> {
        override fun fromJson(json: String) = User(json.trim('"'))
    }
}

val factory = type.companionObjectInstance as? JsonFactory<T>   // how the framework finds it
```

Drop the `object` here and the framework silently finds nothing — and you cannot repair it, because a
companion block has no classifier and therefore cannot implement an interface at all. This is not a
migration wrinkle; it is a wall. If a companion object is being used as a *value* by anything, reflective
or not, it has to stay one.

All of this is **experimental** in Kotlin 2.5.0, behind `-Xcompanion-blocks-and-extensions` (plus
`-Xcollection-literals` for the `of` examples). Syntax and semantics may still change.

## 8. Conclusion

Two features, two different problems. **Companion extensions** remove a gate that should never have been
there: you can now add type-level functions and properties to any type, including ones from Java, without
the original author having had the foresight to write a `companion object`. **Companion blocks** change
what the compiler produces: real statics on the platforms that have them, no nested class, no singleton,
no annotation per platform.

What they do not do is replace companion objects. The moment you need the companion to *be* something — an
interface implementation, a value you pass around, a receiver, or something a framework can reflect over —
you need an object, and `companion object` remains exactly the right tool. The rule of thumb I would suggest: keep `companion object` where it is part
of your public API, and reach for `companion { }` in new code where it was only ever standing in for
`static`.

Ten years after KT-11968 was filed, it can probably be closed.

---

*All examples in this article are runnable: [github.com/ralfstuckert/kotlin-companion-extensions](https://github.com/ralfstuckert/kotlin-companion-extensions).
The proposal itself is [KEEP-0449](https://github.com/Kotlin/KEEP/blob/main/proposals/KEEP-0449-companions-block-extension.md).*
