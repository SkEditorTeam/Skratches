import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import me.glicz.accesswiden.AccessWidenExtension
import me.glicz.accesswiden.AccessWidenPlugin

plugins {
    id("me.glicz.eyepatch") version "1.0.2"
    id("me.glicz.access-widen") version "2.0.0" apply false
    id("com.gradleup.shadow") version "9.2.0" apply false
}

val skriptVersions: List<String> by gradle.extra

skriptVersions.forEach { version ->
    val name = "Skript_v${version.replace('.', '_')}"

    eyepatch.repositories.create(name) {
        submodule = "work/$name"
        ignoredPrefixes = setOf("build/", "skript-aliases/")
    }

    findProject(":$name")?.run {
        apply<AccessWidenPlugin>()

        val accessWidened by configurations.creating
        val alsoShade by configurations.creating

        afterEvaluate {
            dependencies {
                accessWidened(accessWiden(tasks.named<Jar>("jar")))

                alsoShade(project(":common"))
            }

            tasks.register<ShadowJar>("skratchedJar") {
                dependsOn(accessWidened)

                configurations = listOf(alsoShade)

                from(accessWidened.elements.map { files -> files.map { zipTree(it) } })

                dependencies {
                    include(project(":common"))
                }

                archiveFileName = "Skript-v$version-skratched.jar"
            }
        }

        extensions.configure<AccessWidenExtension> {
            accessWideners.from(rootDir.resolve("build-data/skript.accesswidener"))
        }

        tasks.withType<JavaCompile> {
            options.isFork = true
            options.compilerArgs.addAll(listOf("-Xlint:-deprecation", "-Xlint:-removal"))
        }
    }
}

subprojects {
    apply<JavaPlugin>()

    repositories {
        mavenCentral()
        maven("https://repo.roxymc.net/snapshots")
        maven("https://repo.papermc.io/repository/maven-public/")
    }

    dependencies {
        "compileOnly"("me.glicz:skanalyzer-core:2.0.0-SNAPSHOT") {
            isTransitive = !project.name.startsWith("Skript")
        }
    }
}
