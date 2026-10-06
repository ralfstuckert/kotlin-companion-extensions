# Kotlin Companion Extensions

Runnable examples for an article about Kotlin companion blocks and companion extensions (KEEP-0449).

The project uses **Kotlin 2.5.0-Beta1** and the experimental compiler option:

```text
-Xcompanion-blocks-and-extensions
```

## Run

```bash
./gradlew run
```

If the Gradle wrapper is not checked in yet, use a local Gradle installation once:

```bash
gradle wrapper
gradle run
```

## Examples

The source files follow the progression used in the article:

1. `01_InstanceExtension.kt` - regular extension functions and their receiver
2. `02_CompanionObject.kt` - traditional companion objects
3. `03_CompanionObjectExtension.kt` - extending an existing `User.Companion`
4. `04_CompanionExtension.kt` - the new companion extension without a companion object
5. `05_CompanionBlock.kt` - companion members without creating an object
6. `06_CompanionProperties.kt` - companion `val`, `var`, `const val`, and backing state
7. `07_JavaTypeExtension.kt` - companion extension on `java.time.LocalDate`
8. `08_CompanionObjectUseCase.kt` - when an actual companion object is still useful

The examples deliberately use `User` throughout most of the project so that instance-level and companion-level APIs can be compared directly.

## Status

Companion blocks and extensions are experimental in Kotlin 2.5.0-Beta1. Syntax and semantics may still change before stabilization.

See KEEP-0449:
https://github.com/Kotlin/KEEP/blob/main/proposals/KEEP-0449-companions-block-extension.md
