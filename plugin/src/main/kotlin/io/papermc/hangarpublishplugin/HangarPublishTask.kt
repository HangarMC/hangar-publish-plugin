package io.papermc.hangarpublishplugin

import io.papermc.hangarpublishplugin.internal.HangarVersionPublisher
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.TaskAction

/**
 * Task that publishes a [HangarPublication].
 */
abstract class HangarPublishTask : DefaultTask() {
    @get:Internal
    abstract val auth: Property<HangarAuthService>

    @get:Nested
    abstract val publication: Property<HangarPublication>

    @TaskAction
    fun run() {
        val publication: HangarPublication = publication.get()
        HangarVersionPublisher(auth.get()).uploadVersion(publication)
    }
}
