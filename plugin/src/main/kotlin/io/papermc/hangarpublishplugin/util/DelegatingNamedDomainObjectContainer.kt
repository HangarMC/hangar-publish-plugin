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
package io.papermc.hangarpublishplugin.util

import org.gradle.api.NamedDomainObjectContainer

/**
 * Delegating [NamedDomainObjectContainer].
 */
interface DelegatingNamedDomainObjectContainer<T> : NamedDomainObjectContainer<T> {
    /**
     * The backing [NamedDomainObjectContainer] wrapped by this [DelegatingNamedDomainObjectContainer].
     *
     * [NamedDomainObjectContainer] is annotated with [org.gradle.internal.HasInternalProtocol],
     * so in some situations a plugin-implemented wrapper may not be suitable.
     */
    val backingContainer: NamedDomainObjectContainer<T>
}
