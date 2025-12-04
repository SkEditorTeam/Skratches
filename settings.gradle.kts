import org.gradle.kotlin.dsl.provideDelegate

pluginManagement {
    includeBuild("build-logic")
}

rootProject.name = "skratches-parent"

fun include(path: String, action: ProjectDescriptor.() -> Unit) {
    include(path)
    project(path).action()
}

include(":skratches-common") {
    projectDir = file("common")
}

gradle.extra["skriptVersions"] = listOf(
    "2.8", "2.9", "2.10", "2.11", "2.12", "2.13"
)

val skriptVersions: List<String> by gradle.extra

skriptVersions.forEach { version ->
    val path = "Skript_v${version.replace('.', '_')}"

    if (file(path).exists()) {
        include(":$path")
    }
}
