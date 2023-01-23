import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    id("com.gradle.plugin-publish") version "1.1.0"
    id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
}

group = "io.papermc"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson", "gson", "2.10.1")
    implementation("org.apache.httpcomponents.client5", "httpclient5", "5.2.1")
}

testing {
    suites {
        // Configure the built-in test suite
        val test by getting(JvmTestSuite::class) {
            // Use Kotlin Test test framework
            useKotlinTest("1.7.10")

            dependencies {
                // Use newer version of JUnit Engine for Kotlin Test
                implementation("org.junit.jupiter:junit-jupiter-engine:5.9.1")
            }
        }

        // Create a new test suite
        val functionalTest by registering(JvmTestSuite::class) {
            // Use Kotlin Test test framework
            useKotlinTest("1.7.10")

            dependencies {
                // functionalTest test suite depends on the production code in tests
                implementation(project())

                // Use newer version of JUnit Engine for Kotlin Test
                implementation("org.junit.jupiter:junit-jupiter-engine:5.9.1")
            }

            targets {
                all {
                    // This test suite should run after the built-in test suite has run its tests
                    testTask.configure { shouldRunAfter(test) }
                }
            }
        }
    }
}

gradlePlugin {
    val plugin by plugins.creating {
        id = "io.papermc.hangar-publish-plugin"
        implementationClass = "io.papermc.hangarpublishplugin.HangarPublishPlugin"
    }
}

gradlePlugin.testSourceSets(sourceSets["functionalTest"])

tasks.named<Task>("check") {
    // Include functionalTest as part of the check lifecycle
    dependsOn(testing.suites.named("functionalTest"))
}

// also sets java toolchain
kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.withType<JavaCompile> {
    options.release.set(8)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs += "-Xjdk-release=1.8"
}
