# Kotlin Companion Extensions

Runnable examples for an article about Kotlin companion blocks and companion
extensions ([KEEP-0449](https://github.com/Kotlin/KEEP/blob/main/proposals/KEEP-0449-companions-block-extension.md)).

The project uses **Kotlin 2.5.0-Beta1** and the experimental compiler options:

```text
-Xcompanion-blocks-and-extensions
-Xcollection-literals
```

## Topics

The examples are ordered by topic. Each topic is one numbered directory under
`src/main/kotlin/examples/`; the package names inside are plain
(`examples.typelevel`, `examples.statics`, ...) so only the directory carries
the ordering.

### 0. Why this is not enough — `00_motivation/`

| File | Shows |
| --- | --- |
| `01_CompanionObjectExtension.kt` | extending a companion *object* — possible since Kotlin 1.0, but only if the author wrote one |

It also shows the second half of the problem: the extension compiles to a
static getter on the *file* class, taking the companion as an argument, so it
has no backing field and recomputes on every access.

```bash
./gradlew javapMotivation
```

### 1. Extension functions and properties on type level — `01_typelevel/`

| File | Shows |
| --- | --- |
| `01_InstanceExtension.kt` | the baseline: an extension with an *instance* receiver |
| `02_CompanionFunction.kt` | `companion fun User.parse(line)` — no companion object needed |
| `03_CompanionProperty.kt` | `companion val` / `var` / `const val` on type level |
| `04_CompanionBlock.kt` | the same `Color` from the type owner's side, via a `companion { }` block |

### 2. Extending existing Java classes — `02_javatypes/`

| File | Shows |
| --- | --- |
| `01_JavaTypeExtension.kt` | `companion fun LocalDate.fromGerman(...)` on a type you do not own |
| `02_MoreJavaTypes.kt` | `UUID`, `File`, `String`, and the restrictions on companion receivers (§1.3.2) |

### 3. Backing fields — `03_backingfields/`

| File | Shows |
| --- | --- |
| `01_ExtensionPropertyLimits.kt` | why a regular extension property recomputes on every access |
| `02_CompanionExtensionBackingField.kt` | companion extension properties may have initializers and backing fields (§1.3.5) |
| `03_CompanionBlockBackingField.kt` | backing fields, `const val` and initialization order in a companion block |

### 4. Compiled to static whenever possible — `04_static/`

| File | Shows |
| --- | --- |
| `01_CompanionObjectStatic.kt` | the old way: `companion object` + `@JvmStatic`, plus the `Companion` allocation |
| `02_CompanionBlockStatic.kt` | companion blocks become static members, companion extensions become statics on the file class |
| `03_MultiplatformAndExpect.kt` | pointer to the `multiplatform` subproject (see below) |
| `src/test/kotlin/examples/04_static/JUnitStaticTest.kt` | JUnit `@BeforeClass` works **without** `@JvmStatic` |

Proof in bytecode:

```bash
./gradlew javapStatics
```

`Parser` has `public static final Parser parse(String)` and no `Parser$Companion`
at all, while `LegacyParser` still carries `LegacyParser$Companion` and a static
`Companion` field.

#### The `multiplatform` subproject

Per-platform static annotations (problem #3) and `expect`/`actual` matching
(problem #5) need more than one target, so they live in `multiplatform/`
(JVM + JS):

| File | Shows |
| --- | --- |
| `commonMain/.../Clock.kt` | `expect class` with a `companion` block — no `@JvmStatic`/`@JsStatic` anywhere |
| `jvmMain/.../Clock.jvm.kt` | the actual, compiled to a plain JVM static |
| `jsMain/.../Clock.js.kt` | the actual for JS |
| `commonMain/.../Timeout.kt` | `expect class Timeout { companion { fun ofSeconds(...) } }` |
| `jvmMain/.../Timeout.jvm.kt` | `actual typealias Timeout = java.time.Duration` — a **Java static** actualizes the companion block member |

```bash
./gradlew :multiplatform:runJvm
./gradlew :multiplatform:jsNodeDevelopmentRun
./gradlew :multiplatform:javapMultiplatform
```

Two details worth knowing:

- The `companion` block itself carries no `actual`; only its members do.
- KEEP §4.2.1 specifies JS `static` class members, but Kotlin 2.5.0-Beta1 still
  lowers them to module-level functions. The spec is ahead of the JS backend here.

### 5. `invoke()` and `of()` operators — `05_operators/`

| File | Shows |
| --- | --- |
| `01_InvokeOperator.kt` | fake constructors in a companion block, and `invoke` as a companion extension on a Java type |
| `02_OfOperator.kt` | `of` backing collection literals — block only, never an extension (§1.3.4) |

### 6. Resolution — `06_resolution/`

| File | Shows |
| --- | --- |
| `01_Resolution.kt` | a companion extension wins over a companion object member; `T.Companion.foo()` to say otherwise |

### 7. References, imports and reflection — `07_references/`

| File | Shows |
| --- | --- |
| `01_ReferenceTypes.kt` | the same type as a companion block and as a companion object, plus a `JsonFactory` discovery pattern |
| `02_ReferencesAndReflection.kt` | `Color::fromHex` in `map()`, `Color::Black` as a `KProperty0`, static imports, and what `kotlin-reflect` sees |

Reflection finds companion block members under `staticFunctions` /
`staticProperties`, never under `memberFunctions`, and `companionObject` is
`null`. Patterns that ask a class for its companion object as a *value* cannot
be migrated to a companion block at all — a block has no classifier and so
cannot implement an interface.

## Run

Every example has its own `main`. Each one is a Gradle task in the `examples`
group, named after its file:

```bash
./gradlew run02_CompanionFunction
./gradlew run01_InvokeOperator
```

Run all of them in topic order:

```bash
./gradlew runAll
```

Run the JUnit example:

```bash
./gradlew test
```

## Status

Companion blocks and extensions are experimental in Kotlin 2.5.0-Beta1.
Syntax and semantics may still change before stabilization.
