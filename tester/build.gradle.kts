import io.papermc.hangarpublishplugin.model.Platforms

plugins {
    java
    id("io.papermc.hangar-publish-plugin")
}

fun copyJar(name: String): Provider<RegularFile> {
    val copyTask = tasks.register("copy$name") {
        val from = tasks.jar.flatMap { it.archiveFile }
        val to = layout.file(from.map {
            it.asFile.parentFile.resolve("$name.jar")
        })
        inputs.file(from)
        outputs.file(to)
        doLast {
            from.get().asFile.copyTo(to.get().asFile, true)
        }
    }
    return layout.file(copyTask.map { it.outputs.files.single() })
}

val paper = copyJar("paper")
val waterfall = copyJar("waterfall")
val velocity = copyJar("velocity")

hangarPublish {
    publications.register("publishPluginTest") {
        apiEndpoint.set("https://hangar.papermc.dev/api/v1/") // staging
        version.set(System.currentTimeMillis().toString())
        id.set("test-project")
        channel.set("Release")
        changelog.set("Removed Herobrine")
        platforms {
            register(Platforms.PAPER) {
                jar.set(paper)
                platformVersions.set(listOf("1.17-1.19"))
                dependencies {
                    hangar("WonderfulProject") {
                        required.set(false)
                    }
                }
            }
            register(Platforms.WATERFALL) {
                jar.set(waterfall)
                platformVersions.set(listOf("1.19"))
                dependencies.url("Google", "https://google.com")
            }
            register(Platforms.VELOCITY) {
                jar.set(velocity)
                platformVersions.set(listOf("3.1"))
            }
        }

        pages.resourcePage("""
            # My Epic Resource Page

            ## Epic Features
            - does nothing
            - tests hangar
        """.trimIndent())
    }
}
