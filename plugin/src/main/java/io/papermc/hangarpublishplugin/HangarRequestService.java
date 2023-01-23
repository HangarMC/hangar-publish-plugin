package io.papermc.hangarpublishplugin;

import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.entity.mime.StringBody;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HangarRequestService {

    private static final Logger LOGGER = LoggerFactory.getLogger(HangarRequestService.class);
    private static final Gson GSON = new Gson();

    private final HangarAuthService auth;

    public HangarRequestService(final HangarAuthService auth) {
        this.auth = auth;
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
            final HttpPost post = new HttpPost(publication.getApiEndpoint().get() + "projects/" + namespace + "/upload");
            post.setEntity(builder.build());
            this.addAuthorizationHeader(publication.getApiEndpoint().get(), publication.getApiKey().get(), client, post);

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

    private void addAuthorizationHeader(
        final String apiEndpoint,
        final String apiKey,
        final HttpClient client,
        final HttpMessage message
    ) throws IOException {
        final HangarAuthorizationToken jwt = this.auth.fetchJwt(client, apiEndpoint, apiKey);
        message.addHeader("Authorization", jwt.jwt());
    }
}
