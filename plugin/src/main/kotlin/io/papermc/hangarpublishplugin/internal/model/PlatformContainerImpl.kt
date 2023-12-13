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

import io.papermc.hangarpublishplugin.internal.util.maybeRegister
import io.papermc.hangarpublishplugin.model.PlatformContainer
import io.papermc.hangarpublishplugin.model.PlatformDetails
import io.papermc.hangarpublishplugin.model.Platforms
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.NamedDomainObjectProvider
import javax.inject.Inject

abstract class PlatformContainerImpl @Inject constructor(
    override val backingContainer: NamedDomainObjectContainer<PlatformDetails>
) : PlatformContainer,
    NamedDomainObjectContainer<PlatformDetails> by backingContainer {
    override fun paper(op: Action<PlatformDetails>): NamedDomainObjectProvider<PlatformDetails> =
        maybeRegister(Platforms.PAPER, op)

    override fun velocity(op: Action<PlatformDetails>): NamedDomainObjectProvider<PlatformDetails> =
        maybeRegister(Platforms.VELOCITY, op)

    override fun waterfall(op: Action<PlatformDetails>): NamedDomainObjectProvider<PlatformDetails> =
        maybeRegister(Platforms.WATERFALL, op)
}
