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
import io.papermc.hangarpublishplugin.PageSyncTask
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.Optional

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
     *
     * In order to sync project pages, the following permissions are also needed:
     *  - `edit_page`
     */
    @get:Input
    val apiKey: Property<String>

    /**
     * Name of the publication.
     */
    @get:Input
    val name: String

    /**
     * The owner (user or organization) of the Hangar project this publication is for.
     */
    @get:Input
    val owner: Property<String>

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
     * Container holding the [PlatformDetails] for each
     * platform in this publication.
     *
     * Each registration's name will be used as the platform. For convenience
     * [Platforms] holds constants for all available platforms
     * on Paper's Hangar instance.
     */
    @get:Nested
    val platforms: NamedDomainObjectContainer<PlatformDetails>

    /**
     * Configures [platforms] with [op].
     *
     * @param op configuration action
     */
    fun platforms(op: Action<NamedDomainObjectContainer<PlatformDetails>>) {
        op.execute(platforms)
    }

    /**
     * Convenience method for setting [HangarPublication.owner] and [HangarPublication.slug].
     *
     * @param owner owner
     * @param slug slug
     */
    fun namespace(owner: String, slug: String) {
        this.owner.set(owner)
        this.slug.set(slug)
    }

    /**
     * Convenience method for setting [HangarPublication.owner] and [HangarPublication.slug].
     *
     * @param owner owner
     * @param slug slug
     */
    fun namespace(owner: Provider<String>, slug: Provider<String>) {
        this.owner.set(owner)
        this.slug.set(slug)
    }

    /**
     * Container for [ProjectPage]s of this publication's project.
     *
     * hangar-publish-plugin will automatically register [PageSyncTask]s
     * named in the format `sync<CapitalizedPublicationName>Publication<CapitalizedPageName>PageToHangar`
     * for each registered publication.
     *
     * Example configuration:
     * ```
     * pages {
     *     // Default resource page:
     *     resourcePage("# My Resource")
     *     // or
     *     resourcePage(provider { file("README.md").readText() })
     *
     *     // Custom page:
     *     register("Custom-Page") {
     *         content.set("# My Resource")
     *         // or
     *         content.set(provider { file("README.md").readText() })
     *     }
     * }
     * ```
     */
    @get:Internal
    val pages: ProjectPageContainer

    /**
     * Configures [pages] with [op].
     *
     * @param op configuration action
     */
    fun pages(op: Action<ProjectPageContainer>) {
        op.execute(pages)
    }
}
