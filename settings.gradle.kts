rootProject.name = "Skratches"

include("common")

listOf(
    "2_8",
    "2_12"
).forEach {
    val path = "Skript_v$it"

    if (file(path).exists()) {
        include(":$path")
    }
}
