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

import io.papermc.hangarpublishplugin.HangarPublishExtension
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.PolymorphicDomainObjectContainer
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional

/**
 * Defines a platform for a version publication.
 *
 * See [HangarPublishExtension.publications] for an example registration.
 */
interface PlatformDetails {
    @get:Input
    val name: String

    @get:Internal
    val platform: String
        get() = name

    /**
     * List of supported platform versions, i.e. `listOf("1.18", "1.19")`.
     */
    @get:Input
    val platformVersions: ListProperty<String>

    /**
     * The jar file to publish for this platform.
     *
     * Either this or [url] must be configured, but not both.
     */
    @get:InputFile
    @get:Optional
    val jar: RegularFileProperty

    /**
     * The URL to download the jar for this platform.
     *
     * Either this or [jar] must be configured, but not both.
     */
    @get:Input
    @get:Optional
    val url: Property<String>

    /**
     * Container for [DependencyDetails] of this platform.
     *
     * Supports registering [DependencyDetails.Url] and [DependencyDetails.Hangar].
     *
     * The [urlDependency] and [hangarDependency] convenience methods
     * are provided to simplify registrations.
     */
    @get:Nested
    val dependencies: PolymorphicDomainObjectContainer<DependencyDetails>

    /**
     * Registers a [DependencyDetails.Url] without any extra configuration.
     *
     * @param name dependency name
     * @param url dependency URL
     * @return registration provider
     */
    fun urlDependency(name: String, url: String): NamedDomainObjectProvider<DependencyDetails.Url>

    /**
     * Registers a [DependencyDetails.Url], configuring it with the provided action.
     *
     * @param name dependency name
     * @param url dependency URL
     * @param op configuration action
     * @return registration provider
     */
    fun urlDependency(name: String, url: String, op: Action<DependencyDetails.Url>): NamedDomainObjectProvider<DependencyDetails.Url>

    /**
     * Registers a [DependencyDetails.Url] without any extra configuration.
     *
     * @param name dependency name
     * @param url dependency URL
     * @return registration provider
     */
    fun urlDependency(name: String, url: Provider<String>): NamedDomainObjectProvider<DependencyDetails.Url>

    /**
     * Registers a [DependencyDetails.Url], configuring it with the provided action.
     *
     * @param name dependency name
     * @param url dependency URL
     * @param op configuration action
     * @return registration provider
     */
    fun urlDependency(name: String, url: Provider<String>, op: Action<DependencyDetails.Url>): NamedDomainObjectProvider<DependencyDetails.Url>

    /**
     * Registers a [DependencyDetails.Hangar] without any extra configuration.
     *
     * @param owner dependency owner
     * @param slug dependency slug
     * @return registration provider
     */
    fun hangarDependency(owner: String, slug: String): NamedDomainObjectProvider<DependencyDetails.Hangar>

    /**
     * Registers a [DependencyDetails.Hangar], configuring it with the provided action.
     *
     * @param owner dependency owner
     * @param slug dependency slug
     * @param op configuration action
     * @return registration provider
     */
    fun hangarDependency(owner: String, slug: String, op: Action<DependencyDetails.Hangar>): NamedDomainObjectProvider<DependencyDetails.Hangar>

    /**
     * Registers a [DependencyDetails.Hangar] without any extra configuration.
     *
     * @param owner dependency owner
     * @param slug dependency slug
     * @return registration provider
     */
    fun hangarDependency(owner: Provider<String>, slug: Provider<String>): NamedDomainObjectProvider<DependencyDetails.Hangar>

    /**
     * Registers a [DependencyDetails.Hangar], configuring it with the provided action.
     *
     * @param owner dependency owner
     * @param slug dependency slug
     * @param op configuration action
     * @return registration provider
     */
    fun hangarDependency(owner: Provider<String>, slug: Provider<String>, op: Action<DependencyDetails.Hangar>): NamedDomainObjectProvider<DependencyDetails.Hangar>
}
