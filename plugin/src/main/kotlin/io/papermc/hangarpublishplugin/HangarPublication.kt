package io.papermc.hangarpublishplugin

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
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
import javax.inject.Inject

interface HangarPublication {
    @get:Input
    val apiEndpoint: Property<String>

    @get:Input
    val apiKey: Property<String>

    @get:Input
    val owner: Property<String>

    @get:Input
    val name: String

    @get:Input
    val slug: Property<String>

    @get:Input
    val version: Property<String>

    @get:Input
    val channel: Property<String>

    @get:Input
    @get:Optional
    val changelog: Property<String>

    @get:Nested
    val platforms: NamedDomainObjectContainer<PlatformDetails>

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

        @get:Input
        val platformVersions: ListProperty<String>

        @get:InputFile
        @get:Optional
        val jar: RegularFileProperty

        @get:Nested
        val dependencies: NamedDomainObjectContainer<DependencyDetails>

        fun urlDependency(url: String) {
            urlDependency(url) {}
        }

        fun urlDependency(url: String, op: Action<DependencyDetails>) {
            urlDependency(providers.provider { url }, op)
        }

        fun urlDependency(url: Provider<String>) {
            urlDependency(url) {}
        }

        fun urlDependency(url: Provider<String>, op: Action<DependencyDetails>) {
            dependencies.register(dependencies.size.toString()) {
                this.url.set(url)
                op.execute(this)
            }
        }

        fun hangarDependency(owner: String, slug: String) {
            hangarDependency(owner, slug) {}
        }

        fun hangarDependency(owner: String, slug: String, op: Action<DependencyDetails>) {
            hangarDependency(providers.provider { owner }, providers.provider { slug }, op)
        }

        fun hangarDependency(owner: Provider<String>, slug: Provider<String>) {
            hangarDependency(owner, slug) {}
        }

        fun hangarDependency(owner: Provider<String>, slug: Provider<String>, op: Action<DependencyDetails>) {
            dependencies.register(dependencies.size.toString()) {
                val ns = objects.newInstance(HangarProjectNamespace::class.java)
                ns.owner.set(owner)
                ns.slug.set(slug)
                hangarNamespace.set(ns)
                op.execute(this)
            }
        }
    }

    abstract class DependencyDetails {
        @get:Input
        abstract val name: String

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

    companion object Platform {
        const val PAPER = "PAPER"
        const val WATERFALL = "WATERFALL"
        const val VELOCITY = "VELOCITY"
    }
}
