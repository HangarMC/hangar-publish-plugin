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
package io.papermc.hangarpublishplugin

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import io.papermc.hangarpublishplugin.internal.HangarAuthorizationToken
import org.apache.hc.client5.http.classic.HttpClient
import org.apache.hc.client5.http.classic.methods.HttpPost
import org.apache.hc.core5.http.ClassicHttpResponse
import org.apache.hc.core5.http.io.entity.EntityUtils
import org.gradle.api.GradleException
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters

abstract class HangarAuthService : BuildService<BuildServiceParameters.None> {
    companion object {
        val GSON: Gson = GsonBuilder().create()
        val LOGGER: Logger = Logging.getLogger(HangarAuthService::class.java)
    }

    private val cache: MutableMap<String, HangarAuthorizationToken> = HashMap()

    @Synchronized
    fun jwt(client: HttpClient, apiEndpoint: String, apiKey: String): HangarAuthorizationToken {
        val key = "$apiEndpoint:$apiKey"
        val get = cache[key]
        if (get != null && !get.shouldRenew()) {
            return get
        }
        val fetch = fetchJwt(client, apiEndpoint, apiKey)
            ?: throw GradleException("Error getting JWT")
        cache[key] = fetch
        return fetch
    }

    private fun fetchJwt(client: HttpClient, apiEndpoint: String, apiKey: String): HangarAuthorizationToken? {
        val post = HttpPost(apiEndpoint + "authenticate?apiKey=" + apiKey)
        return client.execute(post, ::handleResponse)
    }

    private fun handleResponse(response: ClassicHttpResponse): HangarAuthorizationToken? {
        if (response.code == 400) {
            LOGGER.error("Bad JWT request (400); is the API key correct?")
            return null
        } else if (response.code != 200) {
            LOGGER.error("Error requesting JWT, returned {}: {}", response.code, response.reasonPhrase)
            return null
        }
        val json = EntityUtils.toString(response.entity, Charsets.UTF_8)
        val obj = GSON.fromJson(json, JsonObject::class.java)
        val token = obj.getAsJsonPrimitive("token").asString
        val expiresIn = obj.getAsJsonPrimitive("expiresIn").asLong
        return HangarAuthorizationToken.create(token, expiresIn)
    }
}
