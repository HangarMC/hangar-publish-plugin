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

import io.papermc.hangarpublishplugin.model.HangarPublication
import org.gradle.api.NamedDomainObjectContainer

/**
 * Extension holding configuration for [HangarPublishPlugin].
 */
interface HangarPublishExtension {
    /**
     * Container holding Hangar publications for a project.
     *
     * hangar-publish-plugin will automatically register [HangarPublishTask]s
     * named in the format `publish<CapitalizedPublicationName>PublicationToHangar`
     * for each registered publication.
     *
     * Example publication configuration:
     * ```
     * hangarPublish {
     *     publications.register("myPublication") {
     *         version.set(project.version as String)
     *         id.set("AwesomePlugin")
     *         channel.set("Release")
     *         changelog.set("Removed Herobrine")
     *         platforms {
     *             register(Platforms.PAPER) {
     *                 jar.set(tasks.jar.flatMap { it.archiveFile })
     *                 platformVersions.set(listOf("1.18", "1.19"))
     *                 dependencies {
     *                     hangar("Something") {
     *                         required.set(false)
     *                     }
     *                     url("Some Dependency", "https://dependency-plugin-website.com/")
     *                 }
     *             }
     *         }
     *     }
     * }
     * ```
     */
    val publications: NamedDomainObjectContainer<HangarPublication>
}
