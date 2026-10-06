package examples.multiplatform

// Common code calls the companion block members directly. On the JVM this
// resolves to Java statics, on JS to the generated JS equivalent - without a
// single platform annotation in sight.
fun report(platform: String): String = buildString {
    appendLine("platform  = $platform")
    appendLine("clock     = ${Clock.system().epochMillis > 0}")
    appendLine(describeTimeout())
}
