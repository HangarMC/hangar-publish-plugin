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

import io.papermc.hangarpublishplugin.model.DependencyDetails
import io.papermc.hangarpublishplugin.model.PlatformDetails
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.PolymorphicDomainObjectContainer
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.kotlin.dsl.polymorphicDomainObjectContainer
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.registerBinding
import javax.inject.Inject

abstract class PlatformDetailsImpl @Inject constructor(
    override val name: String,
    private val providers: ProviderFactory,
    objects: ObjectFactory
) : PlatformDetails {
    override val dependencies: PolymorphicDomainObjectContainer<DependencyDetails> = objects.polymorphicDomainObjectContainer(DependencyDetails::class).also {
        it.registerBinding(DependencyDetails.Hangar::class, AbstractDependencyDetails.HangarDependencyDetails::class)
        it.registerBinding(DependencyDetails.Url::class, AbstractDependencyDetails.UrlDependencyDetails::class)
    }

    override fun urlDependency(name: String, url: String): NamedDomainObjectProvider<DependencyDetails.Url> =
        urlDependency(name, url) {}

    override fun urlDependency(name: String, url: String, op: Action<DependencyDetails.Url>): NamedDomainObjectProvider<DependencyDetails.Url> =
        urlDependency(name, providers.provider { url }, op)

    override fun urlDependency(name: String, url: Provider<String>): NamedDomainObjectProvider<DependencyDetails.Url> =
        urlDependency(name, url) {}

    override fun urlDependency(name: String, url: Provider<String>, op: Action<DependencyDetails.Url>): NamedDomainObjectProvider<DependencyDetails.Url> =
        dependencies.register<DependencyDetails.Url>(name) {
            this.url.set(url)
            this.url.disallowChanges()
            op.execute(this)
        }

    override fun hangarDependency(owner: String, slug: String): NamedDomainObjectProvider<DependencyDetails.Hangar> =
        hangarDependency(owner, slug) {}

    override fun hangarDependency(owner: String, slug: String, op: Action<DependencyDetails.Hangar>): NamedDomainObjectProvider<DependencyDetails.Hangar> =
        hangarDependency(providers.provider { owner }, providers.provider { slug }, op)

    override fun hangarDependency(owner: Provider<String>, slug: Provider<String>): NamedDomainObjectProvider<DependencyDetails.Hangar> =
        hangarDependency(owner, slug) {}

    override fun hangarDependency(owner: Provider<String>, slug: Provider<String>, op: Action<DependencyDetails.Hangar>): NamedDomainObjectProvider<DependencyDetails.Hangar> =
        dependencies.register<DependencyDetails.Hangar>(dummyDependencyName()) {
            this.owner.set(owner)
            this.owner.disallowChanges()
            this.slug.set(slug)
            this.slug.disallowChanges()
            op.execute(this)
        }

    private fun dummyDependencyName(): String {
        val prefix = "hangarManagedDependency"
        val existingOfType = dependencies.names.filter {
            it.startsWith(prefix) && it.substringAfter(prefix).toIntOrNull() != null
        }.size
        return prefix + existingOfType
    }
}
