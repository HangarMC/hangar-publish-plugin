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

import com.google.gson.JsonElement
import io.papermc.hangarpublishplugin.HangarAuthService
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.ClassicHttpRequest
import org.apache.hc.core5.http.HttpHeaders
import org.apache.hc.core5.http.io.entity.StringEntity
import org.gradle.api.logging.Logging
import java.nio.charset.StandardCharsets

private val logger = Logging.getLogger("HangarRequest")

fun <T : ClassicHttpRequest> send(
    auth: HangarAuthService,
    apiEndpoint: String,
    methodEndpoint: String,
    apiKey: String,
    entitySupplier: (String) -> T,
    entityConsumer: (T) -> Unit
) {
    HttpClients.createDefault().use { client ->
        val entity = entitySupplier.invoke(apiEndpoint + methodEndpoint)
        val jwt = auth.jwt(client, apiEndpoint, apiKey)
        entity.addHeader(HttpHeaders.AUTHORIZATION, jwt)

        entityConsumer.invoke(entity)
        client.execute(entity) { response ->
            if (response.code != 200) {
                logger.error("Error using endpoint '{}', returned {}: {}", apiEndpoint, response.code, ErrorResponseParser.parse(response))
            }
        }
    }
}

fun addBody(entity: ClassicHttpRequest, body: JsonElement) {
    entity.entity = StringEntity(HangarAuthService.GSON.toJson(body), StandardCharsets.UTF_8)
}
