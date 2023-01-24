package io.papermc.hangarpublishplugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register

class HangarPublishPlugin : Plugin<Project> {
    companion object {
        const val PAPER_HANGAR_API = "https://hangar.papermc.io/api/v1/"
        const val TASK_GROUP = "Hangar Publication"
    }

    override fun apply(project: Project) {
        val authService = project.gradle.sharedServices.registerIfAbsent("hangar-auth", HangarAuthService::class.java) {}

        val ext = project.extensions.create<HangarPublishExtension>("hangarPublish")

        ext.publications.all {
            apiKey.convention(
                project.providers.gradleProperty("io.papermc.hangar-publish-plugin.$name.api-key")
                    .orElse(project.providers.gradleProperty("io.papermc.hangar-publish-plugin.default-api-key"))
            )
            apiEndpoint.convention(PAPER_HANGAR_API)

            project.tasks.register<HangarPublishTask>("publish${name.capitalize()}PublicationToHangar") {
                group = TASK_GROUP
                description = "Publishes the '${this@all.name}' publication to Hangar."
                outputs.upToDateWhen { false } // always run when requested
                auth.set(authService)
                usesService(authService)
                publication.set(this@all)
            }
        }
    }
}
