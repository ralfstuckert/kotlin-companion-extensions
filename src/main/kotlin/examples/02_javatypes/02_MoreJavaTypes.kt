package examples.javatypes

import java.io.File
import java.util.UUID

// Final Java classes, interfaces and Kotlin built-ins alike - the receiver
// only has to be a declared classifier without type arguments (KEEP §1.3.2).
companion fun UUID.zero(): UUID = UUID(0L, 0L)

companion fun File.temp(name: String): File = File(System.getProperty("java.io.tmpdir"), name)

companion fun String.ofCodePoints(vararg points: Int): String =
    String(points, 0, points.size)

// Rejected receivers:
//   companion fun <T> T.broken()          -> type parameters are not allowed
//   companion fun List<Int>.broken()      -> no type arguments allowed
//   companion fun Unit.broken()           -> objects are not allowed

fun main() {
    println(UUID.zero())
    println(File.temp("demo.txt").name)
    println(String.ofCodePoints(75, 116))
}
