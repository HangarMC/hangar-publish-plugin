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

import org.gradle.api.Named
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input

/**
 * Defines a plugin dependency for a platform.
 */
interface DependencyDetails : Named {
    /**
     * The name of this [DependencyDetails]. The only requirement for names is that they are
     * unique within the collection.
     *
     * - For [Url] dependencies, the name will be displayed on Hangar under the dependencies section.
     * - For [Hangar] dependencies, the name is not used for display purposes as this information
     *   is fetched from the references Hangar project instead.
     *
     * @see [Named.getName]
     */
    @Input
    override fun getName(): String

    /**
     * Whether this is an optional or required dependency. Defaults to true/required.
     */
    @get:Input
    val required: Property<Boolean>

    /**
     * [DependencyDetails] for a Hangar project dependency.
     */
    interface Hangar : DependencyDetails {
        /**
         * The owner (user or organization) of the Hangar project dependency.
         */
        @get:Input
        val owner: Property<String>

        /**
         * The slug/id of the Hangar project dependency.
         */
        @get:Input
        val slug: Property<String>
    }

    /**
     * [DependencyDetails] for a URL project dependency.
     */
    interface Url : DependencyDetails {
        /**
         * The URL where the dependency is located.
         */
        @get:Input
        val url: Property<String>
    }
}
