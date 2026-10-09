# Companion Extensions
Let's say we have to deal with an existing class `User` that is not in our hands, and it has no companion
object:

[01_InstanceExtension.kt](../src/main/kotlin/examples/01_typelevel/01_InstanceExtension.kt):
```kotlin
data class User(val name: String)
```

With the new feature you can write companion extension properties and functions:

[02_CompanionFunction.kt](../src/main/kotlin/examples/01_typelevel/02_CompanionFunction.kt):
```kotlin
companion val User.Anonymous: User get() = User("Anonymous")

companion fun User.describe(): String {
    // no `this` here - no value receiver, and no User.Companion either
    return "User instances have a name"
}
```

`User` has no companion object here, and it does not need one. As a result, there is no receiver, neither
as a parameter nor as a `this`. That is worth sitting with for a second. The receiver in a companion
extension is not a value you receive; it is a statement about _where the function may be found_. We will
see this again in the generated code later, where it disappears entirely.

Mutable and constant properties work as well:

[03_CompanionProperty.kt](../src/main/kotlin/examples/01_typelevel/03_CompanionProperty.kt):
```kotlin
companion var User.defaultName: String = "Anonymous"

const companion val User.MAX_NAME_LENGTH: Int = 100
```

There is one notable restriction: companion extensions may only be declared at **top level**. The reason is
resolution — allowing them in arbitrary nested scopes would make the rules considerably harder to pin down.
In the days before [context parameters](https://medium.com/@ralf.stuckert/kotlin-context-parameters-0156f2911462)
that would have been a painful limitation. Today it is much less so: if such a function needs additional
scope, it can simply ask for it with a `context` parameter.

Next: [Companion Blocks](companion_blocks.md)
