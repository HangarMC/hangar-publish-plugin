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

val paperJar = copyJar("paper")
val waterfallJar = copyJar("waterfall")
val velocityJar = copyJar("velocity")

hangarPublish {
    publications.register("publishPluginTest") {
        apiEndpoint = "https://hangar.papermc.dev/api/v1/" // staging
        version = System.currentTimeMillis().toString()
        id = "test-project"
        channel = "Release"
        changelog = "Removed Herobrine"
        platforms {
            paper {
                jar = paperJar
                platformVersions = listOf("1.17-1.19")
                dependencies {
                    hangar("WonderfulProject") {
                        required = false
                    }
                }
            }
            waterfall {
                jar = waterfallJar
                platformVersions = listOf("1.19")
                dependencies.url("Google", "https://google.com")
            }
            velocity {
                jar = velocityJar
                platformVersions = listOf("3.1")
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
