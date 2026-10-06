package examples.companion.extension

import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val germanDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

// LocalDate is a Java type and has no Kotlin companion object.
companion fun LocalDate.fromGerman(value: String): LocalDate =
    LocalDate.parse(value, germanDateFormatter)

fun main() {
    println(LocalDate.fromGerman("01.10.2026"))
}
