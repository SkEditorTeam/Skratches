import org.gradle.kotlin.dsl.provideDelegate

rootProject.name = "Skratches"

include("common")

gradle.extra["skriptVersions"] = listOf(
    "2.8", "2.9", "2.10", "2.12"
)

val skriptVersions: List<String> by gradle.extra

skriptVersions.forEach { version ->
    val path = "Skript_v${version.replace('.', '_')}"

    if (file(path).exists()) {
        include(":$path")
    }
}
