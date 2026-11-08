plugins {
    java
}

tasks {
    withType<JavaCompile>().configureEach {
        options.isFork = true
        options.compilerArgs.addAll(listOf("-Xlint:-deprecation", "-Xlint:-removal", "-Xlint:-dep-ann"))
    }

    withType<AbstractArchiveTask>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }
}
