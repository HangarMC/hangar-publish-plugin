/*
 * Hangar Publish Plugin Gradle Plugin
 * Copyright (c) 2023 HangarMC Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.papermc.hangarpublishplugin

import io.papermc.hangarpublishplugin.internal.HangarPublishExtensionImpl
import io.papermc.hangarpublishplugin.model.HangarPublication
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

class HangarPublishPlugin : Plugin<Project> {
    private companion object {
        const val PAPER_HANGAR_API: String = "https://hangar.papermc.io/api/v1/"
        const val TASK_GROUP: String = "Hangar Publishing"
        const val EXTENSION_NAME: String = "hangarPublish"
        const val AUTH_SERVICE_NAME: String = "hangar-auth"
    }

    override fun apply(project: Project) {
        val authService = project.gradle.sharedServices.registerIfAbsent(AUTH_SERVICE_NAME, HangarAuthService::class.java) {}

        val ext = project.extensions.create(
            HangarPublishExtension::class.java,
            EXTENSION_NAME,
            HangarPublishExtensionImpl::class.java
        )

        val publishAll = project.tasks.register("publishAllPublicationsToHangar") {
            group = TASK_GROUP
            description = "Publishes all registered Hangar publications for this project."
            doNotTrackState("$name should always run when requested")
        }

        ext.publications.all {
            apiKey.convention(
                project.providers.gradleProperty("io.papermc.hangar-publish-plugin.$name.api-key")
                    .orElse(project.providers.gradleProperty("io.papermc.hangar-publish-plugin.default-api-key"))
            )
            apiEndpoint.convention(PAPER_HANGAR_API)

            val publishTask = project.tasks.register<HangarPublishTask>(taskName()) {
                group = TASK_GROUP
                description = "Publishes the '${this@all.name}' publication to Hangar."
                auth.set(authService)
                usesService(authService)
                publication.set(this@all)
            }

            publishAll.configure {
                dependsOn(publishTask)
            }
        }
    }

    private fun HangarPublication.taskName(): String =
        "publish${name.capitalize().replace(' ', '_')}PublicationToHangar"
}
