package io.papermc.hangarpublishplugin

import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.TaskAction

abstract class HangarPublishTask : DefaultTask() {
    @get:Internal
    abstract val auth: Property<HangarAuthService>

    @get:Nested
    abstract val publication: Property<HangarPublication>

    init {
        init()
    }

    private fun init() {
        usesService(auth)
    }

    @TaskAction
    fun run() {
        val publication: HangarPublication = publication.get()
    }
}
