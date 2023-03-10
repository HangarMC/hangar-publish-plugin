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
package io.papermc.hangarpublishplugin.internal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.papermc.hangarpublishplugin.HangarAuthService;
import io.papermc.hangarpublishplugin.model.HangarPublication;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.entity.mime.StringBody;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

public final class HangarVersionPublisher {

    private static final Logger LOGGER = Logging.getLogger(HangarVersionPublisher.class);
    private static final Gson GSON = new GsonBuilder().create();

    private final HangarAuthService auth;

    public HangarVersionPublisher(final HangarAuthService auth) {
        this.auth = auth;
    }

    public void uploadVersion(final HangarPublication publication) throws IOException {
        final HangarVersion version = HangarVersion.fromPublication(publication);
        final List<File> files = version.files().stream()
            .filter(HangarVersion.FileData::isFile)
            .map(fileData -> publication.getPlatforms().getByName(fileData.firstPlatform()).getJar().getAsFile().get())
            .collect(Collectors.toList());
        try (final CloseableHttpClient client = HttpClients.createDefault()) {
            final MultipartEntityBuilder builder = MultipartEntityBuilder.create();

            // Attach version upload json data
            final String json = GSON.toJson(version);
            builder.addPart("versionUpload", new StringBody(json, ContentType.APPLICATION_JSON));

            // Attach files (one file for each platform where no external url is defined in the version upload data)
            for (final File file : files) {
                builder.addPart("files", new FileBody(file, ContentType.DEFAULT_BINARY));
            }

            // Finalize the request
            final String namespace = publication.getOwner().get() + "/" + publication.getSlug().get();
            final HttpPost post = new HttpPost(publication.getApiEndpoint().get() + "projects/" + namespace + "/upload");
            post.setEntity(builder.build());

            // Authorize
            final HangarAuthorizationToken jwt = this.auth.jwt(client, publication.getApiEndpoint().get(), publication.getApiKey().get());
            post.addHeader("Authorization", jwt.getJwt());

            final boolean success = client.execute(post, response -> {
                if (response.getCode() != 200) {
                    LOGGER.error("Error uploading version, returned {}: {}", response.getCode(), ErrorResponseParser.parseErrorMessage(response));
                    return false;
                }
                return true;
            });

            if (!success) {
                throw new RuntimeException("Error uploading version");
            }
        }
    }
}
