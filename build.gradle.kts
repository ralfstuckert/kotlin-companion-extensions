plugins {
    kotlin("jvm") version "2.5.0-Beta1"
}

repositories {
    mavenCentral()
}

dependencies {
    // Used by 07_references to show what reflection sees of a companion block.
    implementation(kotlin("reflect"))
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
    "examples.motivation._01_CompanionObjectExtensionKt",
    "examples.typelevel._01_InstanceExtensionKt",
    "examples.typelevel._02_CompanionFunctionKt",
    "examples.typelevel._03_CompanionPropertyKt",
    "examples.typelevel._04_CompanionBlockKt",
    "examples.javatypes._01_JavaTypeExtensionKt",
    "examples.javatypes._02_MoreJavaTypesKt",
    "examples.backingfields._01_ExtensionPropertyLimitsKt",
    "examples.backingfields._02_CompanionExtensionBackingFieldKt",
    "examples.backingfields._03_CompanionBlockBackingFieldKt",
    "examples.statics._01_CompanionObjectStaticKt",
    "examples.statics._02_CompanionBlockStaticKt",
    "examples.operators._01_InvokeOperatorKt",
    "examples.operators._02_OfOperatorKt",
    "examples.resolution._01_ResolutionKt",
    "examples.references._02_ReferencesAndReflectionKt",
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

// The motivating example: a companion *object* extension is a static getter on
// the file class, taking the companion as an argument - and has no field.
// The same output also shows what `Color` itself pays for the companion: a
// `Companion` field, a synthetic accessor and a second class file.
tasks.register<Exec>("javapMotivation") {
    group = "examples"
    description = "Shows what an extension on a companion object compiles to"
    dependsOn(tasks.named("compileKotlin"))
    workingDir = layout.buildDirectory.dir("classes/kotlin/main").get().asFile
    commandLine(
        "javap", "-p",
        "examples/motivation/_01_CompanionObjectExtensionKt.class",
        "examples/motivation/Color.class",
        "examples/motivation/Color\$Companion.class",
    )
}

// A regular extension property is a getter taking the receiver as a parameter;
// a companion extension property has a real static field and no parameter at all.
tasks.register<Exec>("javapBackingFields") {
    group = "examples"
    description = "Shows that only companion extension properties get a backing field"
    dependsOn(tasks.named("compileKotlin"))
    workingDir = layout.buildDirectory.dir("classes/kotlin/main").get().asFile
    commandLine(
        "javap", "-p",
        "examples/backingfields/_01_ExtensionPropertyLimitsKt.class",
        "examples/backingfields/_02_CompanionExtensionBackingFieldKt.class",
        "examples/backingfields/Registry.class",
    )
}
