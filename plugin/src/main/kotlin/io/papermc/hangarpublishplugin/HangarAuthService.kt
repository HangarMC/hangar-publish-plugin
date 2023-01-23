package io.papermc.hangarpublishplugin

import com.google.gson.Gson
import com.google.gson.JsonObject
import org.apache.hc.client5.http.classic.HttpClient
import org.apache.hc.client5.http.classic.methods.HttpPost
import org.apache.hc.core5.http.ClassicHttpResponse
import org.apache.hc.core5.http.io.HttpClientResponseHandler
import org.apache.hc.core5.http.io.entity.EntityUtils
import org.gradle.api.GradleException
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.concurrent.ConcurrentHashMap

abstract class HangarAuthService : BuildService<BuildServiceParameters.None> {
    companion object {
        val GSON: Gson = Gson();
        val LOGGER: Logger = Logging.getLogger(HangarAuthService::class.java)
    }

    private val cache: MutableMap<String, HangarAuthorizationToken> = ConcurrentHashMap()

    @Throws(IOException::class)
    @Synchronized
    fun fetchJwt(client: HttpClient, apiEndpoint: String, apiKey: String): HangarAuthorizationToken {
        val key = "$apiEndpoint:$apiKey"
        val get = cache[key]
        if (get != null && !get.shouldRenew()) {
            return get
        }
        val fetch = fetchJwt0(client, apiEndpoint, apiKey) ?: throw GradleException("Error getting JWT")
        cache[key] = fetch
        return fetch
    }

    @Throws(IOException::class)
    private fun fetchJwt0(client: HttpClient, apiEndpoint: String, apiKey: String): HangarAuthorizationToken? {
        return client.execute(HttpPost(apiEndpoint + "authenticate?apiKey=" + apiKey), HttpClientResponseHandler { response: ClassicHttpResponse ->
            println(response.code)
            if (response.code == 400) {
                LOGGER.error("Bad JWT request; is the API key correct?")
                return@HttpClientResponseHandler null
            } else if (response.code != 200) {
                LOGGER.error("Error requesting JWT {}: {}", response.code, response.reasonPhrase)
                return@HttpClientResponseHandler null
            }
            val json = EntityUtils.toString(response.entity, StandardCharsets.UTF_8)
            val obj = GSON.fromJson(json, JsonObject::class.java)
            val token = obj.getAsJsonPrimitive("token").asString
            val expiresIn = obj.getAsJsonPrimitive("expiresIn").asLong
            HangarAuthorizationToken(token, System.currentTimeMillis() + expiresIn)
        })
    }
}
