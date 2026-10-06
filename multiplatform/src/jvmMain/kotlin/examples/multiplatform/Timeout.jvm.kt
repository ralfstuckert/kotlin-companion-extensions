package examples.multiplatform

// `java.time.Duration.ofSeconds` is a *static* Java method and `toMillis` an
// instance method - exactly the shape the expect declaration describes. No
// wrapper class, no facade: the Java type is actualized directly.
//
// A companion extension may use a type alias as its receiver too; it is
// resolved against the expansion (KEEP §1.3.3), which is what makes such an
// actualization usable from common code.
actual typealias Timeout = java.time.Duration
