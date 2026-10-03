import org.gradle.api.tasks.testing.Test

plugins { kotlin("jvm") version "2.2.20" }
repositories { mavenCentral() }

dependencies {
    implementation("io.arrow-kt:arrow-core:2.2.3")
    testImplementation(kotlin("test-junit5"))
    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin { jvmToolchain(21) }

// Green baseline; the separate exercise suite is intentionally red in starter.
tasks.test { useJUnitPlatform { excludeTags("exercise") } }
tasks.register<Test>("exerciseTest") {
    description = "Run Module 11 assignment acceptance tests"
    group = "verification"
    testClassesDirs = sourceSets.test.get().output.classesDirs
    classpath = sourceSets.test.get().runtimeClasspath
    useJUnitPlatform { includeTags("exercise") }
    shouldRunAfter(tasks.test)
}
