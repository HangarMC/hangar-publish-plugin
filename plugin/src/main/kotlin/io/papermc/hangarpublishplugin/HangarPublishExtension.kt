package io.papermc.hangarpublishplugin

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

/**
 * Extension holding configuration for [HangarPublishPlugin].
 */
abstract class HangarPublishExtension @Inject constructor(
    objects: ObjectFactory
) {
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
     *         owner.set("AwesomeDeveloper")
     *         slug.set("AwesomePlugin")
     *         channel.set("Release")
     *         changelog.set("Removed Herobrine")
     *         platforms {
     *             register(HangarPublication.Platform.PAPER) {
     *                 jar.set(tasks.jar.flatMap { it.archiveFile })
     *                 platformVersions.set(listOf("1.18", "1.19"))
     *                 hangarDependency("Someone", "Something") {
     *                     required.set(false)
     *                 }
     *                 urlDependency("https://dependency-plugin-website.com/")
     *             }
     *         }
     *     }
     * }
     * ```
     */
    val publications: NamedDomainObjectContainer<HangarPublication> =
        objects.domainObjectContainer(HangarPublication::class.java)
}
