package examples.resolution

class Example {
    companion object {
        fun foo() = 1
    }
}

companion fun Example.foo() = 2


fun main() {
    println(Example.foo())           // 2 - the companion extension
    println(Example.Companion.foo()) // 1 - companion object, said explicitly

    // The same precedence applies to callable references.
    val viaExtension: () -> Int = Example::foo
    val viaObject: () -> Int = Example.Companion::foo
    println(viaExtension())
    println(viaObject())
}
