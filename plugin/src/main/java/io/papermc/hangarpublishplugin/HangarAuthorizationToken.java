package io.papermc.hangarpublishplugin;

import java.util.concurrent.TimeUnit;

public final class HangarAuthorizationToken {
    private final String jwt;
    private final long expiresAt;

    public HangarAuthorizationToken(final String jwt, final long expiresAt) {
        this.jwt = jwt;
        this.expiresAt = expiresAt;
    }

    public boolean shouldRenew() {
        // Request a new one a little before the active one expires
        return System.currentTimeMillis() < this.expiresAt + TimeUnit.SECONDS.toMillis(3);
    }

    public String jwt() {
        return jwt;
    }

    public long expiresAt() {
        return expiresAt;
    }
}
