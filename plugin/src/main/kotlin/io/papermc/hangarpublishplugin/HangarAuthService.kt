package io.papermc.hangarpublishplugin

import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters

abstract class HangarAuthService : BuildService<BuildServiceParameters.None> {
    fun fetchJwt(apiEndpoint: String, apiKey: String): String {
        TODO("implement")
    }
}
