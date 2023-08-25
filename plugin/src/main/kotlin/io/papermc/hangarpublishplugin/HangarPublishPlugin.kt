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
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.register

class HangarPublishPlugin : Plugin<Project> {
    private companion object {
        const val PAPER_HANGAR_API: String = "https://hangar.papermc.io/api/v1/"
        const val TASK_GROUP: String = "Hangar Publish Plugin"
        const val EXTENSION_NAME: String = "hangarPublish"
        const val AUTH_SERVICE_NAME: String = "hangar-auth"

        private fun HangarPublication.publishTaskName(): String =
            "publish${name.sanitizeNameForTaskName()}PublicationToHangar"

        private fun String.sanitizeNameForTaskName() = capitalize().replace(' ', '_')

        private fun syncTaskName(publicationName: String, pageName: String): String =
            "sync${publicationName.sanitizeNameForTaskName()}Publication${pageName.sanitizeNameForTaskName()}PageToHangar"
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

        val globalSyncAll = project.tasks.register("syncAllPagesToHangar") {
            group = TASK_GROUP
            description = "Syncs all registered pages of all registered Hangar publications for this project."
            doNotTrackState("$name should always run when requested")
        }

        ext.publications.all {
            handlePublication(project, authService, this, publishAll, globalSyncAll)
        }
    }

    private fun handlePublication(
        target: Project,
        authService: Provider<HangarAuthService>,
        publication: HangarPublication,
        publishAll: TaskProvider<*>,
        globalSyncAll: TaskProvider<*>
    ) {
        publication.apiKey.convention(
            target.providers.gradleProperty("io.papermc.hangar-publish-plugin.${publication.name}.api-key")
                .orElse(target.providers.gradleProperty("io.papermc.hangar-publish-plugin.default-api-key"))
        )
        publication.apiEndpoint.convention(PAPER_HANGAR_API)

        val publishTask = target.tasks.register<HangarPublishTask>(publication.publishTaskName()) {
            group = TASK_GROUP
            description = "Publishes the '${publication.name}' publication to Hangar."
            auth.set(authService)
            usesService(authService)
            this.publication.set(publication)
        }

        publishAll.configure {
            dependsOn(publishTask)
        }

        val syncAllTask = target.tasks.register("syncAll${publication.name.sanitizeNameForTaskName()}PublicationPagesToHangar") {
            group = TASK_GROUP
            description = "Syncs all pages for the '${publication.name}' publication to Hangar."
            doNotTrackState("$name should always run when requested")
        }

        globalSyncAll.configure {
            dependsOn(syncAllTask)
        }

        publication.pages.all {
            val thisPage = this@all

            val syncTask = target.tasks.register<PageSyncTask>(syncTaskName(publication.name, name)) {
                group = TASK_GROUP
                description = "Syncs the '${thisPage.name}' page of the '${publication.name}' publication to Hangar."
                auth.set(authService)
                usesService(authService)
                slug.set(publication.slug)
                page.set(thisPage)
                apiKey.set(publication.apiKey)
                apiEndpoint.set(publication.apiEndpoint)
            }
            syncAllTask.configure {
                dependsOn(syncTask)
            }
        }
    }
}
