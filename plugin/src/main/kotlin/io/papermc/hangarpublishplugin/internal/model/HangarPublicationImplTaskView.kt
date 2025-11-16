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
package io.papermc.hangarpublishplugin.internal.model

import io.papermc.hangarpublishplugin.model.HangarPublication
import io.papermc.hangarpublishplugin.model.PlatformContainer
import io.papermc.hangarpublishplugin.model.ProjectPageContainer
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Nested
import org.gradle.kotlin.dsl.newInstance
import javax.inject.Inject

abstract class HangarPublicationImplTaskView
    @Inject
    constructor(
        viewOf: HangarPublicationImpl,
        objects: ObjectFactory,
    ) : HangarPublication {
        @get:Nested
        val platformsContainer = viewOf.platformsContainer

        override val platforms: PlatformContainer = objects.newInstance(PlatformContainerImpl::class, platformsContainer)

        override val pages: ProjectPageContainer
            get() = throw UnsupportedOperationException("Pages are not accessible from task views")

        override val apiEndpoint: Property<String> = viewOf.apiEndpoint

        override val apiKey: Property<String> = viewOf.apiKey

        override val name: String = viewOf.name

        override val id: Property<String> = viewOf.id

        override val version: Property<String> = viewOf.version

        override val channel: Property<String> = viewOf.channel

        override val changelog: Property<String> = viewOf.changelog
    }
