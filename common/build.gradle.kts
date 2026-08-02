plugins {
    id("skratches.common-conventions")
}

dependencies {
    implementation(libs.skanalyzer.core)

    findProject(":Skript_v2_8")?.let { skript ->
        compileOnly(project(skript.path, "accessWidened"))
    }
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

tasks {
    withType<JavaCompile>().configureEach {
        options.encoding = Charsets.UTF_8.name()
        options.release = 21
        options.compilerArgs.add("-Xlint:deprecation")
    }
}

val compileJavaSkriptAll = tasks.register("compileJava_Skript_all") {
    group = "skratches"
    description = "Compiles main Java sources against all Skript versions"
}

val skriptVersions: List<String> by gradle.extra

skriptVersions.forEach { version ->
    val name = "Skript_v${version.replace('.', '_')}"
    val skript = findProject(":$name") ?: return@forEach

    val compileTask = tasks.register<JavaCompile>("compileJava_$name") {
        group = "skratches"
        description = "Compiles main Java sources against Skript $version"

        source = sourceSets.main.get().java

        classpath = configurations.create(name) {
            isCanBeConsumed = false

            dependencies.add(project.dependencies.project(skript.path, "accessWidened"))

            extendsFrom(configurations.implementation.get())
        }

        destinationDirectory = layout.buildDirectory.dir("classes/java/main_$name")
    }

    compileJavaSkriptAll.configure {
        dependsOn(compileTask)
    }
}
