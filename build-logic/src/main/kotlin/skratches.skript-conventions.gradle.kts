import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    java
    id("me.glicz.access-widen")
}

val libs = rootProject.the<LibrariesForLibs>()

val accessWidened: Configuration by configurations.creating
val alsoShade: Configuration by configurations.creating

dependencies {
    accessWidened(accessWiden(tasks.jar))

    alsoShade(project(":skratches-common"))
}

accessWiden {
    accessWideners.from(rootDir.resolve("build-data/skript.accesswidener"))
}

tasks {
    register<Zip>("skratchedJar") {
        group = "skratches"

        archiveBaseName = "Skript"
        archiveVersion = project.name.split('_', limit = 2)[1].replace('_', '.')
        archiveClassifier = "skratched"
        archiveExtension = Jar.DEFAULT_EXTENSION

        destinationDirectory = base.libsDirectory
        setMetadataCharset(Charsets.UTF_8.name())

        fun configuration(configuration: Configuration) =
            configuration.elements.map { files -> files.map(::zipTree) }

        from(configuration(accessWidened))
        from(configuration(alsoShade)) {
            exclude("META-INF/**")
        }

        duplicatesStrategy = DuplicatesStrategy.FAIL
    }

    withType<JavaCompile> {
        options.isFork = true
        options.compilerArgs.addAll(listOf("-Xlint:-deprecation", "-Xlint:-removal"))
    }

    withType<AbstractArchiveTask>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }
}
