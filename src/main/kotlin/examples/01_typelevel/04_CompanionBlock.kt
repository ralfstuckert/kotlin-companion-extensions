package examples.typelevel

// The owner of a type declares type-level members in a `companion` block.
// Same call syntax as an extension, but no object is involved.
class Vector(val x: Double, val y: Double) {

    companion {
        val Zero: Vector = Vector(0.0, 0.0)

        fun polar(angle: Double): Vector = Vector(Math.cos(angle), Math.sin(angle))
    }

    // Companion block members are visible unqualified inside the class.
    fun isOrigin(): Boolean = this == Zero

    override fun toString() = "Vector($x, $y)"
    override fun equals(other: Any?) = other is Vector && other.x == x && other.y == y
    override fun hashCode() = x.hashCode() * 31 + y.hashCode()
}

// A companion block does not create a classifier, so there is nothing to
// reference as a value:
//   println(Vector.Companion)   -> does not compile
//   val v: Any = Vector         -> does not compile

fun main() {
    println(Vector.Zero)
    println(Vector.polar(0.0))
    println(Vector(0.0, 0.0).isOrigin())
}
