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
import io.papermc.hangarpublishplugin.model.PlatformDetails
import io.papermc.hangarpublishplugin.model.ProjectPage
import io.papermc.hangarpublishplugin.model.ProjectPageContainer
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.Nested
import org.gradle.kotlin.dsl.domainObjectContainer
import org.gradle.kotlin.dsl.newInstance
import javax.inject.Inject

abstract class HangarPublicationImpl @Inject constructor(
    override val name: String,
    objects: ObjectFactory
) : HangarPublication {
    @get:Nested
    @Suppress("unchecked_cast") // GH:gradle/gradle#23655
    val platformsContainer: NamedDomainObjectContainer<PlatformDetails> =
        objects.domainObjectContainer(PlatformDetailsImpl::class.java) as NamedDomainObjectContainer<PlatformDetails>

    override val platforms: PlatformContainer = objects.newInstance(PlatformContainerImpl::class, platformsContainer)

    @get:Nested
    val pageContainer: NamedDomainObjectContainer<ProjectPage> =
        objects.domainObjectContainer(ProjectPage::class)

    override val pages: ProjectPageContainer = objects.newInstance(ProjectPageContainerImpl::class, pageContainer)
}
