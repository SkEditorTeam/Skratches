plugins {
    java
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.roxymc.net/snapshots")
}

dependencies {
    compileOnly(project(":Skript_v2_8", "accessWidened"))
    compileOnly("me.glicz:skanalyzer-core:2.0.0-SNAPSHOT")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release = 21
    }
}
