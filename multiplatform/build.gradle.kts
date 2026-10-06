plugins {
    kotlin("multiplatform")
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(17)

    jvm {
        binaries {
            executable {
                mainClass.set("examples.multiplatform.Main_jvmKt")
            }
        }
    }
    js {
        nodejs()
        binaries.executable()
    }

    compilerOptions {
        freeCompilerArgs.add("-Xcompanion-blocks-and-extensions")
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }
}

// Shows that the expect/actual companion block really became a JVM static.
tasks.register<Exec>("javapMultiplatform") {
    group = "examples"
    description = "Shows the static members generated for the actual Clock"
    dependsOn(tasks.named("compileKotlinJvm"))
    workingDir = layout.buildDirectory.dir("classes/kotlin/jvm/main").get().asFile
    commandLine("javap", "-p", "examples/multiplatform/Clock.class")
}
