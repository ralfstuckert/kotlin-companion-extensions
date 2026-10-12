# Resolution and Limitations
What happens when a type has both? The rule is short:

[01_Resolution.kt](../src/main/kotlin/examples/06_resolution/01_Resolution.kt):
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

The rest of the limitations, briefly:

- Companion extensions are **top-level only**. If one needs additional scope, ask for it with a `context`
  parameter — see [Companion Extensions](companion_extensions.md).
- The receiver must be a plain classifier: no type parameters, no type arguments, no objects.
- No `init` in a companion block; only functions and properties.
- No classifier means no interface implementation and no passing it as a value — still a job for
  `companion object`.
- On the JVM, a static and an instance method may not share a signature. Use `@JvmName` if they clash.

How this plays out for callable references, imports and reflection is shown in
[01_ReferenceTypes.kt](../src/main/kotlin/examples/07_references/01_ReferenceTypes.kt) and
[02_ReferencesAndReflection.kt](../src/main/kotlin/examples/07_references/02_ReferencesAndReflection.kt).
