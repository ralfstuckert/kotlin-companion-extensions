# Extending Types You Don't Own
German dates are written `01.10.2026`, and `LocalDate.parse()` does not accept that, so a
`LocalDate.fromGerman()` would be genuinely useful. But `LocalDate` is a plain Java class with no companion
class behind it to extend, so for a decade the answer has been a `DateUtils` object holding a function that
everybody can see belongs on `LocalDate`. But with companion extension functions this becomes quite natural:

[01_JavaTypeExtension.kt](../src/main/kotlin/examples/02_javatypes/01_JavaTypeExtension.kt),
[02_MoreJavaTypes.kt](../src/main/kotlin/examples/02_javatypes/02_MoreJavaTypes.kt):
```kotlin
companion fun LocalDate.fromGerman(value: String) = LocalDate.parse(value, germanFormatter)

companion fun UUID.zero() = UUID(0L, 0L)

companion fun File.temp(name: String) = File(System.getProperty("java.io.tmpdir"), name)
```

The statics that are already on the type keep working, and keep their priority:

```kotlin
LocalDate.of(2026, 10, 1)           // still the Java static
LocalDate.fromGerman("01.10.2026")  // yours
```

So please: delete the `DateUtils` object ;-)

Next: [Extension Properties With a Backing Field](backing_fields.md)
