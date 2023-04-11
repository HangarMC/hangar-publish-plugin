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

import io.papermc.hangarpublishplugin.model.ProjectPage
import io.papermc.hangarpublishplugin.model.ProjectPageContainer
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.provider.Provider
import javax.inject.Inject

abstract class ProjectPageContainerImpl @Inject constructor(
    override val backingContainer: NamedDomainObjectContainer<ProjectPage>
) : ProjectPageContainer,
    NamedDomainObjectContainer<ProjectPage> by backingContainer {
    companion object {
        const val RESOURCE_PAGE_ID = "MainResourcePage"
    }

    override fun resourcePage(content: String): NamedDomainObjectProvider<ProjectPage> = register(RESOURCE_PAGE_ID) {
        this.content.set(content)
    }

    override fun resourcePage(content: Provider<String>): NamedDomainObjectProvider<ProjectPage> = register(RESOURCE_PAGE_ID) {
        this.content.set(content)
    }

    override fun resourcePage(): NamedDomainObjectProvider<ProjectPage> = named(RESOURCE_PAGE_ID)
}
