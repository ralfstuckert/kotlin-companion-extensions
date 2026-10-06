plugins {
    kotlin("jvm") version "2.5.0-Beta1"
    // Applied by the `multiplatform` subproject; declared here so both share a version.
    kotlin("multiplatform") version "2.5.0-Beta1" apply false
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("junit:junit:4.13.2")
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        freeCompilerArgs.add("-Xcompanion-blocks-and-extensions")
        freeCompilerArgs.add("-Xcollection-literals")
    }
}

tasks.test {
    useJUnit()
    testLogging {
        showStandardStreams = true
    }
}

// The examples, in the order used by the article. Each entry is the JVM class
// generated for the corresponding file (a leading digit becomes `_`).
val examples = listOf(
    "examples.typelevel._01_InstanceExtensionKt",
    "examples.typelevel._02_CompanionFunctionKt",
    "examples.typelevel._03_CompanionPropertyKt",
    "examples.typelevel._04_CompanionBlockKt",
    "examples.backingfields._01_ExtensionPropertyLimitsKt",
    "examples.backingfields._02_CompanionExtensionBackingFieldKt",
    "examples.backingfields._03_CompanionBlockBackingFieldKt",
    "examples.statics._01_CompanionObjectStaticKt",
    "examples.statics._02_CompanionBlockStaticKt",
    "examples.statics._03_MultiplatformAndExpectKt",
    "examples.javatypes._01_JavaTypeExtensionKt",
    "examples.javatypes._02_MoreJavaTypesKt",
    "examples.operators._01_InvokeOperatorKt",
    "examples.operators._02_OfOperatorKt",
)

fun taskNameOf(fqName: String) =
    "run" + fqName.substringAfterLast('.').removePrefix("_").removeSuffix("Kt")

examples.forEach { fqName ->
    tasks.register<JavaExec>(taskNameOf(fqName)) {
        group = "examples"
        description = "Runs $fqName"
        mainClass.set(fqName)
        classpath = sourceSets["main"].runtimeClasspath
    }
}

tasks.register("runAll") {
    group = "examples"
    description = "Runs every example in article order"
    dependsOn(examples.map { taskNameOf(it) })
}

// Prints the bytecode signatures used in the "compiled to static" examples.
tasks.register<Exec>("javapStatics") {
    group = "examples"
    description = "Shows that companion blocks produce static members"
    dependsOn(tasks.named("compileKotlin"))
    workingDir = layout.buildDirectory.dir("classes/kotlin/main").get().asFile
    commandLine(
        "javap", "-p",
        "examples/statics/Parser.class",
        "examples/statics/_02_CompanionBlockStaticKt.class",
        "examples/statics/LegacyParser.class",
        "examples/statics/LegacyParser\$Companion.class",
    )
}
