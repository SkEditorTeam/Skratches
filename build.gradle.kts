import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import me.glicz.accesswiden.AccessWidenExtension
import me.glicz.accesswiden.AccessWidenPlugin

plugins {
    id("me.glicz.eyepatch") version "1.0.1"
    id("me.glicz.access-widen") version "2.0.0" apply false
    id("com.gradleup.shadow") version "9.2.0" apply false
}

listOf(
    "2_8",
    "2_12"
).forEach { version ->
    val name = "Skript_v$version"

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

                archiveFileName = "Skript-v${version.replace('_', '.')}-skratched.jar"
            }
        }

        extensions.configure<AccessWidenExtension> {
            accessWideners.from(rootDir.resolve("build-data/skript.accesswidener"))
        }
    }
}
