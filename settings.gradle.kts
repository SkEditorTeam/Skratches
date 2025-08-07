rootProject.name = "Skratches"

listOf(
    "2_12"
).forEach {
    val path = "Skript_v$it"

    if (file(path).exists()) {
        include(":$path")
    }
}
