package io.papermc.hangarpublishplugin.internal

import java.util.concurrent.TimeUnit

data class HangarAuthorizationToken(val jwt: String, val expiresAt: Long) {
    companion object {
        fun create(jwt: String, expiresIn: Long): HangarAuthorizationToken =
            HangarAuthorizationToken(jwt, System.currentTimeMillis() + expiresIn)
    }

    fun shouldRenew(): Boolean {
        // Request a new one a little before the active one expires
        return System.currentTimeMillis() < expiresAt + TimeUnit.SECONDS.toMillis(3)
    }
}
