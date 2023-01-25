package io.papermc.hangarpublishplugin.internal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.nio.charset.StandardCharsets;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;

public final class ErrorResponseParser {

    private static final Gson GSON = new GsonBuilder().create();
    private static final String FORMAT = "(%d) %s ";

    public static String parseErrorMessage(final ClassicHttpResponse response) {
        final JsonObject object;
        try {
            object = GSON.fromJson(EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8), JsonObject.class);
        } catch (final Exception ignored) {
            return response.getReasonPhrase();
        }

        final JsonElement fieldErrors = object.get("fieldErrors");
        if (fieldErrors != null && !fieldErrors.getAsJsonArray().isEmpty()) {
            final JsonArray fieldErrorsArray = fieldErrors.getAsJsonArray();
            if (fieldErrorsArray.size() == 1) {
                final JsonObject fieldError = fieldErrorsArray.get(0).getAsJsonObject();
                return fieldError.getAsJsonPrimitive("errorMsg").getAsString();
            }

            final StringBuilder errorBuilder = new StringBuilder();
            int i = 0;
            for (final JsonElement fieldError : fieldErrorsArray) {
                final String errorMessage = fieldError.getAsJsonObject().getAsJsonPrimitive("errorMsg").getAsString();
                errorBuilder.append(String.format(FORMAT, ++i, errorMessage));
            }
            return errorBuilder.substring(0, errorBuilder.length() - 1);
        }

        final JsonPrimitive message = object.getAsJsonPrimitive("message");
        return message != null ? message.getAsString() : response.getReasonPhrase();
    }
}
