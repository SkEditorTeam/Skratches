dependencies {
    val skript = ":Skript_v2_8"
    if (findProject(skript) != null) {
        compileOnly(project(skript, "accessWidened"))
    }
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
