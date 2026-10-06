package examples.backingfields

class Registry private constructor(val id: Int) {

    companion {
        // Constants are initialized before everything else (KEEP §3.2).
        const val CAPACITY: Int = 8

        // Backing fields turn into private static fields, initialized in the
        // static initializer in program order.
        val Empty: Registry = Registry(0)

        var created: Int = 0
            private set

        fun next(): Registry {
            created++
            return Registry(created)
        }
    }

    override fun toString() = "Registry#$id"
}

fun main() {
    println(Registry.CAPACITY)
    println(Registry.Empty)
    println(Registry.Empty === Registry.Empty) // one field, one value

    println(Registry.next())
    println(Registry.next())
    println("created = ${Registry.created}")
}
