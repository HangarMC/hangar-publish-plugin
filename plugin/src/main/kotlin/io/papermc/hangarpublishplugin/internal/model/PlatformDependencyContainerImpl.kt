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
import io.papermc.hangarpublishplugin.model.PlatformDependencyContainer
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.PolymorphicDomainObjectContainer
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.kotlin.dsl.register
import javax.inject.Inject

abstract class PlatformDependencyContainerImpl @Inject constructor(
    override val backingContainer: PolymorphicDomainObjectContainer<DependencyDetails>,
    private val providers: ProviderFactory
) : PlatformDependencyContainer,
    PolymorphicDomainObjectContainer<DependencyDetails> by backingContainer {
    override fun url(name: String, url: String): NamedDomainObjectProvider<DependencyDetails.Url> =
        url(name, url) {}

    override fun url(name: String, url: String, op: Action<DependencyDetails.Url>): NamedDomainObjectProvider<DependencyDetails.Url> =
        url(name, providers.provider { url }, op)

    override fun url(name: String, url: Provider<String>): NamedDomainObjectProvider<DependencyDetails.Url> =
        url(name, url) {}

    override fun url(name: String, url: Provider<String>, op: Action<DependencyDetails.Url>): NamedDomainObjectProvider<DependencyDetails.Url> =
        register<DependencyDetails.Url>(name) {
            this.url.set(url)
            this.url.disallowChanges()
            op.execute(this)
        }

    override fun hangar(id: String): NamedDomainObjectProvider<DependencyDetails.Hangar> =
        hangar(id) {}

    override fun hangar(id: String, op: Action<DependencyDetails.Hangar>): NamedDomainObjectProvider<DependencyDetails.Hangar> =
        hangar(providers.provider { id }, op)

    override fun hangar(id: Provider<String>): NamedDomainObjectProvider<DependencyDetails.Hangar> =
        hangar(id) {}

    override fun hangar(id: Provider<String>, op: Action<DependencyDetails.Hangar>): NamedDomainObjectProvider<DependencyDetails.Hangar> =
        register<DependencyDetails.Hangar>(dummyDependencyName()) {
            this.id.set(id)
            this.id.disallowChanges()
            op.execute(this)
        }

    private fun dummyDependencyName(): String {
        val prefix = "hangarManagedDependency"
        val existingOfType = names.filter {
            it.startsWith(prefix) && it.substringAfter(prefix).toIntOrNull() != null
        }.size
        return prefix + existingOfType
    }
}
