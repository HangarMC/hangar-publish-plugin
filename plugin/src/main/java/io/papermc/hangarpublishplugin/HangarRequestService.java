package io.papermc.hangarpublishplugin;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.entity.mime.StringBody;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpMessage;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

public final class HangarRequestService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HangarRequestService.class);
    private static final Gson GSON = new Gson();
    private final String apiEndpoint;
    private final String apiKey;
    private HangarAuthorizationToken activeJWT;

    public HangarRequestService(final String apiEndpoint, final String apiKey) {
        this.apiEndpoint = apiEndpoint;
        this.apiKey = apiKey;
    }

    public void uploadVersion(final HangarPublication publication) throws IOException {
        final HangarVersion version = HangarVersion.fromPublication(publication);
        final List<File> files = publication.getPlatforms().stream().map(platform -> platform.getJar().getAsFile().get()).collect(Collectors.toList()); // TODO: Possibly not right
        try (final CloseableHttpClient client = HttpClients.createDefault()) {
            final MultipartEntityBuilder builder = MultipartEntityBuilder.create();

            // Attach version upload json data
            builder.addPart("versionUpload", new StringBody(GSON.toJson(version), ContentType.APPLICATION_JSON));

            // Attach files (one file for each platform where no external url is defined in the version upload data)
            for (final File file : files) {
                builder.addPart("files", new FileBody(file, ContentType.DEFAULT_BINARY));
            }

            // Finalize the request
            final String namespace = publication.getAuthor().get() + "/" + publication.getSlug().get();
            final HttpPost post = new HttpPost(this.apiEndpoint + "projects/" + namespace + "/upload");
            post.setEntity(builder.build());
            this.addAuthorizationHeader(client, post);

            final boolean success = client.execute(post, response -> {
                if (response.getCode() != 200) {
                    LOGGER.error("Error uploading version {}: {}", response.getCode(), response.getReasonPhrase());
                    return false;
                }
                return true;
            });

            if (!success) {
                throw new RuntimeException("Error uploading version");
            }
        }
    }

    private synchronized void addAuthorizationHeader(final HttpClient client, final HttpMessage message) throws IOException {
        // Re-use active JWT if present
        if (this.activeJWT != null && !this.activeJWT.shouldRenew()) {
            message.addHeader("Authorization", this.activeJWT.jwt());
            return;
        }

        // Fetch new JWT
        final HangarAuthorizationToken jwt = client.execute(new HttpPost(this.apiEndpoint + "authenticate?apiKey=" + this.apiKey), response -> {
            System.out.println(response.getCode());
            if (response.getCode() == 400) {
                LOGGER.error("Bad JWT request; is the API key correct?");
                return null;
            } else if (response.getCode() != 200) {
                LOGGER.error("Error requesting JWT {}: {}", response.getCode(), response.getReasonPhrase());
                return null;
            }

            final String json = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            final JsonObject object = GSON.fromJson(json, JsonObject.class);
            final String token = object.getAsJsonPrimitive("token").getAsString();
            final long expiresIn = object.getAsJsonPrimitive("expiresIn").getAsLong();
            return new HangarAuthorizationToken(token, System.currentTimeMillis() + expiresIn);
        });

        if (jwt == null) {
            throw new RuntimeException("Error getting JWT");
        }

        this.activeJWT = jwt;
        message.addHeader("Authorization", jwt.jwt());
    }
}
