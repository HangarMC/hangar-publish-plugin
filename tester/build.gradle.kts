import io.papermc.hangarpublishplugin.HangarPublication

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
        version.set("0.0.4")
        owner.set("hangarPublishPlugin")
        slug.set("test-project")
        channel.set("Release")
        changelog.set("Removed Herobrine")
        platforms {
            register(HangarPublication.Platform.PAPER) {
                jar.set(paper)
                platformVersions.set(listOf("1.18", "1.19"))
            }
            register(HangarPublication.Platform.WATERFALL) {
                jar.set(waterfall)
                platformVersions.set(listOf("1.19"))
            }
            register(HangarPublication.Platform.VELOCITY) {
                jar.set(velocity)
                platformVersions.set(listOf("3.1"))
            }
        }
    }
}
