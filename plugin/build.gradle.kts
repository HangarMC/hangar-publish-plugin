import org.gradle.api.attributes.plugin.GradlePluginApiVersion
import org.gradle.plugin.compatibility.compatibility
import org.gradle.util.GradleVersion
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    id("com.gradle.plugin-publish") version "2.1.1"
    id("net.kyori.indra.licenser.spotless") version "4.0.0"
    id("org.jlleitschuh.gradle.ktlint") version "14.2.0"
}

group = "io.papermc"
version = "0.1.5-SNAPSHOT"

repositories {
    mavenCentral()
}

indraSpotlessLicenser {
    licenseHeaderFile(rootProject.file("../LICENSE_HEADER"))
}

tasks.register("format") {
    dependsOn(tasks.ktlintFormat)
    dependsOn(tasks.spotlessApply)
}

dependencies {
    implementation("com.google.code.gson:gson:2.14.0")
    implementation("org.apache.httpcomponents.client5:httpclient5:5.5.1")
}

testing {
    suites {
        // Configure the built-in test suite
        val test =
            getByName<JvmTestSuite>("test") {
                // Use Kotlin Test test framework
                useKotlinTest(embeddedKotlinVersion)

                dependencies {
                    // Use newer version of JUnit Engine for Kotlin Test
                    implementation("org.junit.jupiter:junit-jupiter-engine:6.1.3")
                }
            }

        // Create a new test suite
        val functionalTest =
            register<JvmTestSuite>("functionalTest") {
                // Use Kotlin Test test framework
                useKotlinTest(embeddedKotlinVersion)

                dependencies {
                    // functionalTest test suite depends on the production code in tests
                    implementation(project())

                    // Use newer version of JUnit Engine for Kotlin Test
                    implementation("org.junit.jupiter:junit-jupiter-engine:6.1.3")
                }

                targets {
                    all {
                        // This test suite should run after the built-in test suite has run its tests
                        testTask.configure {
                            shouldRunAfter(test)
                            failOnNoDiscoveredTests = false
                        }
                    }
                }
            }
    }
}

publishing.repositories.maven("https://artifactory.papermc.io/artifactory/snapshots/") {
    name = "paper"
    credentials(PasswordCredentials::class)
    mavenContent { snapshotsOnly() }
}

gradlePlugin {
    website = "https://github.com/HangarMC/hangar-publish-plugin"
    vcsUrl = "https://github.com/HangarMC/hangar-publish-plugin"
    plugins {
        create("hangar-publish-plugin") {
            id = "io.papermc.hangar-publish-plugin"
            displayName = "Hangar Publish Plugin"
            description = "Gradle plugin for publishing artifacts to Hangar"
            tags = listOf("hangar", "publishing", "minecraft")
            implementationClass = "io.papermc.hangarpublishplugin.HangarPublishPlugin"
            compatibility {
                features {
                    configurationCache = true
                }
            }
        }
    }
}

gradlePlugin.testSourceSets(sourceSets["functionalTest"])

tasks.named<Task>("check") {
    // Include functionalTest as part of the check lifecycle
    dependsOn(testing.suites.named("functionalTest"))
}

kotlin {
    jvmToolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks.withType<JavaCompile> {
    options.release = 17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
        freeCompilerArgs.add("-Xjdk-release=17")
    }
}

configurations.runtimeElements {
    attributes.attribute(GradlePluginApiVersion.GRADLE_PLUGIN_API_VERSION_ATTRIBUTE, objects.named(GradleVersion.current().version))
}
