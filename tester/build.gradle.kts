import io.papermc.hangarpublishplugin.HangarPublication

plugins {
    java
    id("io.papermc.hangar-publish-plugin")
}

hangarPublish {
    publications.register("publishPluginTest") {
        version.set("0.0.3")
        owner.set("hangarPublishPlugin")
        slug.set("test-project")
        channel.set("Release")
        changelog.set("Removed Herobrine")
        platforms {
            register(HangarPublication.Platform.PAPER) {
                jar.set(tasks.jar.flatMap { it.archiveFile })
                platformVersions.set(listOf("1.18", "1.19"))
            }
            register(HangarPublication.Platform.WATERFALL) {
                jar.set(tasks.jar.flatMap { it.archiveFile })
                platformVersions.set(listOf("1.19"))
            }
            register(HangarPublication.Platform.VELOCITY) {
                jar.set(tasks.jar.flatMap { it.archiveFile })
                platformVersions.set(listOf("3.1"))
            }
        }
    }
}
