plugins {
    id("me.glicz.eyepatch") version "1.0.1"
}

eyepatch.repositories {
    listOf(
        "2_12"
    ).forEach {
        create("Skript_v$it") {
            submodule = "work/$name"
            ignoredPrefixes = setOf("skript-aliases")
        }
    }
}
