# Extension Properties With a Backing Field
This one is about a restriction that is lifted _for a reason_, and the reason tells you a lot about what a
companion extension really is. You might have run into this eventually:

[01_ExtensionPropertyLimits.kt](../src/main/kotlin/examples/03_backingfields/01_ExtensionPropertyLimits.kt):
```kotlin
val Config.derived: String get() = "derived from $name"  // recomputed on every access

val Config.cached: String = compute()                    // error: cannot be initialized
```

The reason is the one from the introduction: an extension property is a static getter taking the receiver as
a parameter, and a getter has nowhere to keep anything. A companion extension property is a different
species. Its receiver is the _type_, and there is exactly one of those — so there is no "which instance owns
this field?" to answer, and the KEEP grants an exemption: an initializer, and with it a backing field.

[02_CompanionExtensionBackingField.kt](../src/main/kotlin/examples/03_backingfields/02_CompanionExtensionBackingField.kt):
```kotlin
companion val Config.defaults: List<String> = loadDefaults()  // runs once

companion var Config.activeProfile: String = "dev"  // mutable type-level state
```

The generated code shows where that field lives — and what is missing from it:

```kotlin
private static final List defaults;       // a private static of the file class

public static final List getDefaults();    // no receiver parameter!

static { defaults = loadDefaults(); }
```

A regular extension would have been `getDerived(Config $this$derived)`. A companion extension has no
receiver to pass, so the type is erased from the signature entirely — it only ever told the compiler under
which name you may call this. Inside a `companion` block it works the same, except that the field belongs to
the class itself, and `const val` is initialized before everything else
([03_CompanionBlockBackingField.kt](../src/main/kotlin/examples/03_backingfields/03_CompanionBlockBackingField.kt)).

And the obvious warning: `companion var` is global mutable state. The feature lifts a technical restriction,
it does not improve weird design.

Next: [Statics Without @JvmStatic](statics.md)
