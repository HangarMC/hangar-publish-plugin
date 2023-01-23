package io.papermc.hangarpublishplugin

import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input

interface HangarProjectNamespace {
    @get:Input
    val owner: Property<String>

    @get:Input
    val slug: Property<String>
}
