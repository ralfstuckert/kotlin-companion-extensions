package examples.multiplatform

actual class Timeout(private val millis: Long) {
    companion {
        actual fun ofSeconds(seconds: Long): Timeout = Timeout(seconds * 1000)
    }

    actual fun toMillis(): Long = millis
}
