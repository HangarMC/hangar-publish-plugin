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
