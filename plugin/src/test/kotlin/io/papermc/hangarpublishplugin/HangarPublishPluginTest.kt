package io.papermc.hangarpublishplugin

import org.gradle.kotlin.dsl.the
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class HangarPublishPluginTest {
    @Test
    fun `plugin registers task`() {
        val project = ProjectBuilder.builder().build()
        project.plugins.apply("io.papermc.hangar-publish-plugin")
        project.the<HangarPublishExtension>().publications.register("taskRegister")
        assertNotNull(project.tasks.findByName("publishTaskRegisterPublicationToHangar"))
    }
}
