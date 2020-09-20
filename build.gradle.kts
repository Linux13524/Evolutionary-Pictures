plugins {
    application
    kotlin("jvm") version "1.3.72"
    `kotlin-dsl`
    id("org.openjfx.javafxplugin") version "0.0.8"
}

version = "1.0.2"
group = "org.gradle.sample"

application {
    mainClassName = "de.linus_kloeckner.evolutionary_pictures.MyApp"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("no.tornado:tornadofx:1.7.20")

    // Test dependencies:
    // needed for all the basic junit stuff - like @Test annotation
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.5.0-M1")
    // needed for test execution - without this you will not be able to execute the test
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.5.0-M1")
    // needed for parametrized tests
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.5.0-M1")
    // nice library providing you with some mocking options and annotations for testing units with dependencies
    testImplementation("org.mockito:mockito-core:2.25.1")
    // this contains MockitoExtension - mockito needs it in jUnit 5 (jupiter) environment
    testImplementation("org.mockito:mockito-junit-jupiter:2.25.1")
    // need to make some mockito stuff kotlin-friendly
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.1.0")
}

javafx {
    version = "14"
    modules("javafx.controls")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
