plugins {
    alias(libs.plugins.eyepatch)
    id("skratches.skript-conventions") apply false
}

subprojects {
    repositories {
        mavenCentral()
        maven("https://repo.roxymc.net/snapshots")
        maven("https://repo.papermc.io/repository/maven-public/")
    }

    afterEvaluate {
        // Some older Skript versions still use deprecated Sonatype snapshot URLs,
        // which breaks the build. It's easier to just remove them like this and minimize the diff.
        repositories.removeIf {
            it is MavenArtifactRepository && it.url.toString().contains("oss.sonatype.org")
        }
    }
}

val skriptVersions: List<String> by gradle.extra

skriptVersions.forEach { version ->
    val name = "Skript_v${version.replace('.', '_')}"

    eyepatch.repositories.create(name) {
        submodule = "work/$name"
        ignoredPrefixes = setOf("build/", "skript-aliases/")
    }

    findProject(":$name")?.apply(plugin = "skratches.skript-conventions")
}
