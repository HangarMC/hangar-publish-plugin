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

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional
import org.gradle.kotlin.dsl.newInstance
import javax.inject.Inject

/**
 * Defines a Hangar Version publication.
 *
 * See [HangarPublishExtension.publications] for an example registration.
 */
interface HangarPublication {
    /**
     * The Hangar API endpoint to use. Defaults to Paper's instance.
     */
    @get:Input
    val apiEndpoint: Property<String>

    /**
     * The API key to use for publishing.
     *
     * If not configured, will default to checking the
     * `io.papermc.hangar-publish-plugin.<publicationName>.api-key`,
     * and then the `io.papermc.hangar-publish-plugin.default-api-key`
     * Gradle properties.
     *
     * See [the Gradle docs](https://docs.gradle.org/current/userguide/build_environment.html)
     * for information on how to configure these properties.
     *
     * API keys can be created from `<hangar-url>/<user|organization>/settings/api-keys`.
     *
     * In order to publish versions the provided key will need the following permissions:
     *  - `create_version`
     */
    @get:Input
    val apiKey: Property<String>

    /**
     * The owner (user or organization) of the Hangar project this publication is for.
     */
    @get:Input
    val owner: Property<String>

    /**
     * Name of the publication.
     */
    @get:Input
    val name: String

    /**
     * The slug/id of the Hangar project this publication is for.
     */
    @get:Input
    val slug: Property<String>

    /**
     * The version for this publication.
     */
    @get:Input
    val version: Property<String>

    /**
     * The channel for this publication, i.e. "Release" or "Snapshot".
     */
    @get:Input
    val channel: Property<String>

    /**
     * An optional changelog for this version publication. Formatted in markdown.
     */
    @get:Input
    @get:Optional
    val changelog: Property<String>

    /**
     * Container holding the [HangarPublication.PlatformDetails] for each
     * platform in this publication.
     *
     * Each registration's name will be used as the platform. For convenience
     * [HangarPublication.Platform] holds constants for all available platforms
     * on Paper's Hangar instance.
     */
    @get:Nested
    val platforms: NamedDomainObjectContainer<PlatformDetails>

    /**
     * Defines a platform for a version publication.
     *
     * See [HangarPublishExtension.publications] for an example registration.
     */
    interface PlatformDetails {
        @get:Inject
        val objects: ObjectFactory

        @get:Inject
        val providers: ProviderFactory

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
         * Prefer using the [urlDependency] and [hangarDependency] convenience methods
         * to create registrations.
         */
        @get:Nested
        val dependencies: NamedDomainObjectContainer<DependencyDetails>

        /**
         * Registers a URL dependency without any extra configuration.
         *
         * @param url dependency URL
         * @return registration provider
         */
        fun urlDependency(url: String): NamedDomainObjectProvider<DependencyDetails> =
            urlDependency(url) {}

        /**
         * Registers a URL dependency, configuring it with the provided action.
         *
         * @param url dependency URL
         * @param op configuration action
         * @return registration provider
         */
        fun urlDependency(url: String, op: Action<DependencyDetails>): NamedDomainObjectProvider<DependencyDetails> =
            urlDependency(providers.provider { url }, op)

        /**
         * Registers a URL dependency without any extra configuration.
         *
         * @param url dependency URL
         * @return registration provider
         */
        fun urlDependency(url: Provider<String>): NamedDomainObjectProvider<DependencyDetails> =
            urlDependency(url) {}

        /**
         * Registers a URL dependency, configuring it with the provided action.
         *
         * @param url dependency URL
         * @param op configuration action
         * @return registration provider
         */
        fun urlDependency(url: Provider<String>, op: Action<DependencyDetails>): NamedDomainObjectProvider<DependencyDetails> =
            dependencies.register(createDependencyName("url")) {
                this.url.set(url)
                this.url.disallowChanges()
                hangarNamespace.disallowChanges()

                op.execute(this)
            }

        /**
         * Registers a Hangar dependency without any extra configuration.
         *
         * @param owner dependency owner
         * @param slug dependency slug
         * @return registration provider
         */
        fun hangarDependency(owner: String, slug: String): NamedDomainObjectProvider<DependencyDetails> =
            hangarDependency(owner, slug) {}

        /**
         * Registers a Hangar dependency, configuring it with the provided action.
         *
         * @param owner dependency owner
         * @param slug dependency slug
         * @param op configuration action
         * @return registration provider
         */
        fun hangarDependency(owner: String, slug: String, op: Action<DependencyDetails>): NamedDomainObjectProvider<DependencyDetails> =
            hangarDependency(providers.provider { owner }, providers.provider { slug }, op)

        /**
         * Registers a Hangar dependency without any extra configuration.
         *
         * @param owner dependency owner
         * @param slug dependency slug
         * @return registration provider
         */
        fun hangarDependency(owner: Provider<String>, slug: Provider<String>): NamedDomainObjectProvider<DependencyDetails> =
            hangarDependency(owner, slug) {}

        /**
         * Registers a Hangar dependency, configuring it with the provided action.
         *
         * @param owner dependency owner
         * @param slug dependency slug
         * @param op configuration action
         * @return registration provider
         */
        fun hangarDependency(owner: Provider<String>, slug: Provider<String>, op: Action<DependencyDetails>): NamedDomainObjectProvider<DependencyDetails> =
            dependencies.register(createDependencyName("hangar")) {
                val ns = objects.newInstance<HangarProjectNamespace>()
                ns.owner.set(owner)
                ns.owner.disallowChanges()
                ns.slug.set(slug)
                ns.slug.disallowChanges()

                hangarNamespace.set(ns)
                hangarNamespace.disallowChanges()
                url.disallowChanges()

                op.execute(this)
            }

        private fun createDependencyName(type: String): String {
            val prefix = type + "ManagedDependency"
            val existingOfType = dependencies.names.filter {
                it.startsWith(prefix) && it.substringAfter(prefix).toIntOrNull() != null
            }.size
            return prefix + existingOfType
        }
    }

    /**
     * Defines a plugin dependency for a platform.
     *
     * Create and register instances using [PlatformDetails.urlDependency]
     * and [PlatformDetails.hangarDependency].
     */
    abstract class DependencyDetails {
        /**
         * The name of the dependency. Not currently used for anything,
         * the built-in factory methods will simply ensure each new
         * dependency has a unique name to others in the collection.
         */
        @get:Input
        abstract val name: String

        /**
         * Whether this is an optional or required dependency. Defaults to true/required.
         */
        @get:Input
        abstract val required: Property<Boolean>

        @get:Nested
        @get:Optional
        abstract val hangarNamespace: Property<HangarProjectNamespace>

        @get:Input
        @get:Optional
        abstract val url: Property<String>

        init {
            init()
        }

        private fun init() {
            required.convention(true)
        }
    }

    /**
     * Constants for the available platforms on Paper's Hangar instance.
     */
    companion object Platform {
        const val PAPER = "PAPER"
        const val WATERFALL = "WATERFALL"
        const val VELOCITY = "VELOCITY"
    }
}
