plugins {
    java
}

tasks {
    withType<JavaCompile>().configureEach {
        options.isFork = true
    }

    withType<AbstractArchiveTask>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }
}
