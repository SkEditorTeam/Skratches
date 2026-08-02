import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("skratches.common-conventions")
    id("me.glicz.access-widen")
}

val libs = rootProject.the<LibrariesForLibs>()

val accessWidened = configurations.create("accessWidened")
val alsoShade = configurations.create("alsoShade") {
    isCanBeConsumed = false

    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named<Usage>(Usage.JAVA_API))
    }
}

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
        description = "Same as jar, but skratched!"

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

    withType<JavaCompile>().configureEach {
        options.compilerArgs.addAll(listOf("-Xlint:-deprecation", "-Xlint:-removal", "-Xlint:-dep-ann"))
    }
}
