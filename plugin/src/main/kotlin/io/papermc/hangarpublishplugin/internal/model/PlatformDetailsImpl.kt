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
import io.papermc.hangarpublishplugin.model.PlatformDetails
import org.gradle.api.PolymorphicDomainObjectContainer
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.Nested
import org.gradle.kotlin.dsl.polymorphicDomainObjectContainer
import org.gradle.kotlin.dsl.registerBinding
import javax.inject.Inject

abstract class PlatformDetailsImpl @Inject constructor(
    override val name: String,
    objects: ObjectFactory
) : PlatformDetails {
    @get:Nested
    val dependencyContainer: PolymorphicDomainObjectContainer<DependencyDetails> = objects.polymorphicDomainObjectContainer(DependencyDetails::class).also {
        it.registerBinding(DependencyDetails.Hangar::class, AbstractDependencyDetails.HangarDependencyDetails::class)
        it.registerBinding(DependencyDetails.Url::class, AbstractDependencyDetails.UrlDependencyDetails::class)
    }

    override val dependencies: PlatformDependencyContainer = objects.newInstance(PlatformDependencyContainerImpl::class.java, dependencyContainer)
}
