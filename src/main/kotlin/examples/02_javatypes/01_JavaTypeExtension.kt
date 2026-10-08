package examples.javatypes

import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val germanFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

companion fun LocalDate.fromGerman(value: String): LocalDate =
    LocalDate.parse(value, germanFormatter)

// Properties work as well, including cached ones with a backing field.
companion val LocalDate.GERMAN_FORMATTER: DateTimeFormatter = germanFormatter

companion val LocalDate.Millennium: LocalDate = LocalDate.of(2000, 1, 1)

fun main() {
    println(LocalDate.fromGerman("01.10.2026"))
    println(LocalDate.Millennium.format(LocalDate.GERMAN_FORMATTER))

    // Java statics on the same type stay reachable and keep priority:
    println(LocalDate.of(2026, 10, 1))

    // So please: delete the DateUtils object ;-)
}
