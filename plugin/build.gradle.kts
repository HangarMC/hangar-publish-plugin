import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    id("com.gradle.plugin-publish") version "1.2.1"
    id("net.kyori.indra.license-header") version "3.1.3"
    id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
}

group = "io.papermc"
version = "0.1.4-SNAPSHOT"

repositories {
    mavenCentral()
}

license {
    header = resources.text.fromFile("../LICENSE_HEADER")
}

tasks.register("format") {
    dependsOn(tasks.ktlintFormat)
    dependsOn(tasks.licenseFormat)
}

dependencies {
    implementation("com.google.code.gson", "gson", "2.10.1")
    implementation("org.apache.httpcomponents.client5", "httpclient5", "5.3")
}

testing {
    suites {
        // Configure the built-in test suite
        val test by getting(JvmTestSuite::class) {
            // Use Kotlin Test test framework
            useKotlinTest(embeddedKotlinVersion)

            dependencies {
                // Use newer version of JUnit Engine for Kotlin Test
                implementation("org.junit.jupiter:junit-jupiter-engine:5.12.2")
            }
        }

        // Create a new test suite
        val functionalTest by registering(JvmTestSuite::class) {
            // Use Kotlin Test test framework
            useKotlinTest(embeddedKotlinVersion)

            dependencies {
                // functionalTest test suite depends on the production code in tests
                implementation(project())

                // Use newer version of JUnit Engine for Kotlin Test
                implementation("org.junit.jupiter:junit-jupiter-engine:5.12.2")
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

publishing.repositories.maven("https://repo.papermc.io/repository/maven-snapshots/") {
    name = "paper"
    credentials(PasswordCredentials::class)
    mavenContent { snapshotsOnly() }
}

gradlePlugin {
    website = "https://github.com/HangarMC/hangar-publish-plugin"
    vcsUrl = "https://github.com/HangarMC/hangar-publish-plugin"
    plugins.create("hangar-publish-plugin") {
        id = "io.papermc.hangar-publish-plugin"
        displayName = "Hangar Publish Plugin"
        description = "Gradle plugin for publishing artifacts to Hangar"
        tags = listOf("hangar", "publishing", "minecraft")
        implementationClass = "io.papermc.hangarpublishplugin.HangarPublishPlugin"
    }
}

gradlePlugin.testSourceSets(sourceSets["functionalTest"])

tasks.named<Task>("check") {
    // Include functionalTest as part of the check lifecycle
    dependsOn(testing.suites.named("functionalTest"))
}

kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(8)
    }
}

tasks.withType<JavaCompile> {
    // options.release = 8
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    // kotlinOptions.freeCompilerArgs += "-Xjdk-release=1.8"
}
