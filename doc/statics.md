# Statics Without @JvmStatic

[01_CompanionObjectStatic.kt](../src/main/kotlin/examples/04_static/01_CompanionObjectStatic.kt),
[02_CompanionBlockStatic.kt](../src/main/kotlin/examples/04_static/02_CompanionBlockStatic.kt):
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

```kotlin
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

Everything the introduction complained about is simply absent: no `Parser$Companion` class file, no
`Companion` field to initialize, no synthetic accessor, no annotation to remember. Which also disarms the
JUnit trap — `@BeforeClass` needs no `@JvmStatic`, because there is nothing non-static left to opt out of:

[JUnitStaticTest.kt](../src/test/kotlin/examples/04_static/JUnitStaticTest.kt):
```kotlin
class JUnitStaticTest {
    companion {
        lateinit var database: String

        @BeforeClass
        fun setUpClass() { database = "in-memory-db" }
    }
}
```

Static by construction, so the annotation alone is enough. The same property holds across
(multi-)platforms, which takes care of problems #3 and #5: an `expect class` with a `companion` block needs
no `@JvmStatic` or `@JsStatic` anywhere, and on the JVM you can even write
`actual typealias Timeout = java.time.Duration` and let a _Java_ static actualize a companion block member.

Next: [Constructor-like Factories: invoke() and of()](factory_operators.md)
