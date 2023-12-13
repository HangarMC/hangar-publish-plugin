# [hangar-publish-plugin](https://plugins.gradle.org/plugin/io.papermc.hangar-publish-plugin)

Gradle plugin for publishing to [Hangar](https://hangar.papermc.io/).

## Basic Usage

To use hangar-publish-plugin, simply apply the plugin and configure publications.

hangar-publish-plugin is published to the Gradle plugin portal under the `io.papermc.hangar-publish-plugin` id.

<details>
<summary>Kotlin DSL example</summary>

```kotlin
plugins {
    id("io.papermc.hangar-publish-plugin") version "VERSION"
}

hangarPublish {
    publications.register("plugin") {
        version = project.version as String // use project version as publication version
        id = "hangar-project"
        channel = "Release"
        changelog = "Removed Herobrine" // optional

        // your api key.
        // defaults to the `io.papermc.hangar-publish-plugin.[publicationName].api-key` or `io.papermc.hangar-publish-plugin.default-api-key` Gradle properties
        apiKey = "api key"
        
        // register platforms
        platforms {
            paper {
                jar = tasks.jar.flatMap { it.archiveFile }
                platformVersions = listOf("1.19.3")
                dependencies {
                    hangar("TestProject") {
                        required = false
                    }
                }
            }
        }
    }
}
```
</details>

<details>
<summary>Groovy DSL example</summary>

```groovy
plugins {
    id 'io.papermc.hangar-publish-plugin' version 'VERSION'
}

hangarPublish {
    publications.register('plugin') {
        version = project.version as String // use project version as publication version
        id = 'hangar-project'
        channel = 'Release'
        changelog = 'Removed Herobrine' // optional

        // your api key.
        // defaults to the `io.papermc.hangar-publish-plugin.[publicationName].api-key` or `io.papermc.hangar-publish-plugin.default-api-key` Gradle properties
        apiKey = 'api key'
        
        // register platforms
        platforms {
            paper {
                jar = tasks.jar.archiveFile
                platformVersions = ['1.19.3']
                dependencies {
                    hangar('TestProject') {
                        required = false
                    }
                }
            }
        }
    }
}
```
</details>

Once you have configured your publication(s), you can publish them with the `publishAllPublicationsToHangar` or `publish[CapitalizedPublicationName]PublicationToHangar` tasks.

## Automating Publishing
See [our docs page on how to set up automatic build publishing via Github Actions](https://docs.papermc.io/misc/hangar-publishing).

## Note
hangar-publish-plugin is still in the early stages (pre-1.0). This means things are still subject to change (possibly in breaking ways). Because of this there is not yet documentation beyond the JavaDoc and this README. If you need help with the plugin, feel free to ask in `#build-tooling-help` on the [Paper discord](https://discord.gg/papermc).
