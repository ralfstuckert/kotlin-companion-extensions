package examples.resolution

// What happens when a type has both a companion object and a companion
// extension of the same name?
class Example {
    companion object {
        fun foo() = "companion object"
    }
}

companion fun Example.foo() = "companion extension"

// The rule: `T.foo()` is resolved against companion blocks and extensions
// first, and only then as `T.Companion.foo()`. Blocks and extensions form
// their own receiver, which is exhausted before the companion object is
// consulted.
//
// Note that this is silent: an extension in your own code wins over a
// companion object member declared in a library, with nothing at the call
// site to suggest a choice was made.

fun main() {
    println(Example.foo())           // companion extension
    println(Example.Companion.foo()) // companion object - say so explicitly

    // The same precedence applies to callable references.
    val viaExtension: () -> String = Example::foo
    val viaObject: () -> String = Example.Companion::foo
    println(viaExtension())
    println(viaObject())
}
