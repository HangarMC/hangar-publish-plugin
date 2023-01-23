package io.papermc.hangarpublishplugin

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Nested

interface HangarPublication {
    @get:Input
    val name: String

    @get:Input
    val apiEndpoint: Property<String>

    @get:Input
    val author: Property<String>

    @get:Input
    val slug: Property<String>

    @get:Input
    val apiKey: Property<String>

    @get:Input
    val version: Property<String>

    @get:Input
    val channel: Property<String>

    @get:Nested
    val platforms: NamedDomainObjectContainer<PlatformDetails>

    fun registerPlatform(platform: Platform, op: Action<PlatformDetails>) {
        platforms.register(platform.name) {
            this.platform.set(platform)
            op.execute(this)
        }
    }

    interface PlatformDetails {
        @get:Input
        val name: String

        @get:Input
        val platform: Property<Platform>

        @get:InputFile
        val jar: RegularFileProperty
    }

    // todo don't use enum
    enum class Platform {
        PAPER,
        WATERFALL,
        VELOCITY
    }
}
