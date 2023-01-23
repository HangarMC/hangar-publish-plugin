package io.papermc.hangarpublishplugin;

public final class HangarProjectNamespace {
    private final String owner;
    private final String slug;

    public HangarProjectNamespace(final String owner, final String slug) {
        this.owner = owner;
        this.slug = slug;
    }

    @Override
    public String toString() {
        return this.owner + "/" + this.slug;
    }

    public String owner() {
        return owner;
    }

    public String slug() {
        return slug;
    }
}
