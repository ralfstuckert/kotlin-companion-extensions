package examples.multiplatform

// KEEP §4.2.1 specifies JS `static` class members for companion blocks.
// Kotlin 2.5.0-Beta1 still lowers them to module-level functions
// (`Clock$system()` in the generated .js) - the spec is ahead of the backend
// here. What already holds on every target: no companion object instance and
// no per-platform annotation.
actual class Clock(actual val epochMillis: Long) {
    companion {
        actual fun system(): Clock = Clock(kotlin.js.Date.now().toLong())
    }
}
