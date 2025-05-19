rootProject.name = "hangar-publish-plugin-parent"

pluginManagement {
    includeBuild("plugin")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include("tester")
