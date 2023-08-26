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
package io.papermc.hangarpublishplugin.model

import io.papermc.hangarpublishplugin.util.DelegatingPolymorphicDomainObjectContainer
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.PolymorphicDomainObjectContainer
import org.gradle.api.provider.Provider

/**
 * Extension of [PolymorphicDomainObjectContainer] for [DependencyDetails], adding convenience methods
 * for registering supported types.
 *
 * Supports registering [DependencyDetails.Url] and [DependencyDetails.Hangar].
 */
interface PlatformDependencyContainer : DelegatingPolymorphicDomainObjectContainer<DependencyDetails> {
    /**
     * Registers a [DependencyDetails.Url] without any extra configuration.
     *
     * @param name dependency name
     * @param url dependency URL
     * @return registration provider
     */
    fun url(name: String, url: String): NamedDomainObjectProvider<DependencyDetails.Url>

    /**
     * Registers a [DependencyDetails.Url], configuring it with the provided action.
     *
     * @param name dependency name
     * @param url dependency URL
     * @param op configuration action
     * @return registration provider
     */
    fun url(name: String, url: String, op: Action<DependencyDetails.Url>): NamedDomainObjectProvider<DependencyDetails.Url>

    /**
     * Registers a [DependencyDetails.Url] without any extra configuration.
     *
     * @param name dependency name
     * @param url dependency URL
     * @return registration provider
     */
    fun url(name: String, url: Provider<String>): NamedDomainObjectProvider<DependencyDetails.Url>

    /**
     * Registers a [DependencyDetails.Url], configuring it with the provided action.
     *
     * @param name dependency name
     * @param url dependency URL
     * @param op configuration action
     * @return registration provider
     */
    fun url(name: String, url: Provider<String>, op: Action<DependencyDetails.Url>): NamedDomainObjectProvider<DependencyDetails.Url>

    /**
     * Registers a [DependencyDetails.Hangar] without any extra configuration.
     *
     * @param id dependency project id
     * @return registration provider
     */
    fun hangar(id: String): NamedDomainObjectProvider<DependencyDetails.Hangar>

    /**
     * Registers a [DependencyDetails.Hangar], configuring it with the provided action.
     *
     * @param id dependency project id
     * @param op configuration action
     * @return registration provider
     */
    fun hangar(id: String, op: Action<DependencyDetails.Hangar>): NamedDomainObjectProvider<DependencyDetails.Hangar>

    /**
     * Registers a [DependencyDetails.Hangar] without any extra configuration.
     *
     * @param id dependency project id
     * @return registration provider
     */
    fun hangar(id: Provider<String>): NamedDomainObjectProvider<DependencyDetails.Hangar>

    /**
     * Registers a [DependencyDetails.Hangar], configuring it with the provided action.
     *
     * @param id dependency project id
     * @param op configuration action
     * @return registration provider
     */
    fun hangar(id: Provider<String>, op: Action<DependencyDetails.Hangar>): NamedDomainObjectProvider<DependencyDetails.Hangar>
}
