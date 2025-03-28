rootProject.name = "hangar-publish-plugin-parent"

pluginManagement {
    includeBuild("plugin")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

include("tester")
