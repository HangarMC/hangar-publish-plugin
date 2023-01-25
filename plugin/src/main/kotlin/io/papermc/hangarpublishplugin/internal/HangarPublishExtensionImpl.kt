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
package io.papermc.hangarpublishplugin.internal

import io.papermc.hangarpublishplugin.HangarPublishExtension
import io.papermc.hangarpublishplugin.internal.model.HangarPublicationImpl
import io.papermc.hangarpublishplugin.model.HangarPublication
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

abstract class HangarPublishExtensionImpl @Inject constructor(
    objects: ObjectFactory
) : HangarPublishExtension {
    override val publications: NamedDomainObjectContainer<HangarPublication> = objects.domainObjectContainer(HangarPublication::class.java) { name ->
        objects.newInstance(HangarPublicationImpl::class.java, name)
    }
}
