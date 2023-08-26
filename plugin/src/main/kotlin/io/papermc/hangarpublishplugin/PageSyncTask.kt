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

import com.google.gson.JsonObject
import io.papermc.hangarpublishplugin.internal.addBody
import io.papermc.hangarpublishplugin.internal.model.ProjectPageContainerImpl
import io.papermc.hangarpublishplugin.internal.send
import io.papermc.hangarpublishplugin.model.ProjectPage
import org.apache.hc.client5.http.classic.methods.HttpPatch
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.UntrackedTask

@UntrackedTask(because = "PageSyncTask should always run when requested. Additionally, it's outputs are stored remotely and cannot be tracked properly.")
abstract class PageSyncTask : DefaultTask() {
    @get:Internal
    abstract val auth: Property<HangarAuthService>

    @get:Input
    abstract val id: Property<String>

    @get:Nested
    abstract val page: Property<ProjectPage>

    @get:Input
    abstract val apiEndpoint: Property<String>

    @get:Input
    abstract val apiKey: Property<String>

    @TaskAction
    fun run() {
        val page = this.page.get()
        val content = page.content.get()
        val methodEndpoint = "pages/edit/${this.id.get()}"
        send(this.auth.get(), this.apiEndpoint.get(), methodEndpoint, this.apiKey.get(), ::HttpPatch) { entity ->
            val body = JsonObject()
            body.addProperty("path", page.name.takeIf { it != ProjectPageContainerImpl.RESOURCE_PAGE_ID } ?: "")
            body.addProperty("content", content)
            entity.addBody(body)
        }
    }
}
