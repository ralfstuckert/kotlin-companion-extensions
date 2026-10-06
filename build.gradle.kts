plugins {
    kotlin("jvm") version "2.5.0-Beta1"
    application
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        freeCompilerArgs.add("-Xcompanion-blocks-and-extensions")
        freeCompilerArgs.add("-Xcollection-literals")
    }
}

application {
    mainClass.set("examples.MainKt")
}
