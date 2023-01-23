package io.papermc.hangarpublishplugin

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

abstract class HangarPublishExtension @Inject constructor(
    objects: ObjectFactory
) {
    val publications: NamedDomainObjectContainer<HangarPublication> =
        objects.domainObjectContainer(HangarPublication::class.java)
}
