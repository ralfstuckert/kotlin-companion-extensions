package examples.javatypes

import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val germanDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

// The receiver of a companion extension does not need a companion object -
// so types you do not own, including Java types, can be extended on type
// level. Previously this required the author to have written
// `companion object` for you (KEEP problem #1).
companion fun LocalDate.fromGerman(value: String): LocalDate =
    LocalDate.parse(value, germanDateFormatter)

// Properties work as well, including cached ones with a backing field.
companion val LocalDate.GERMAN_FORMATTER: DateTimeFormatter = germanDateFormatter

companion val LocalDate.Millennium: LocalDate = LocalDate.of(2000, 1, 1)

fun main() {
    println(LocalDate.fromGerman("01.10.2026"))
    println(LocalDate.Millennium.format(LocalDate.GERMAN_FORMATTER))

    // Java statics on the same type stay reachable and keep priority:
    println(LocalDate.of(2026, 10, 1))
}
