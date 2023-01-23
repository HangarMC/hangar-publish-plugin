package io.papermc.hangarpublishplugin;

import io.papermc.hangarpublishplugin.HangarPublication.DependencyDetails;
import io.papermc.hangarpublishplugin.HangarPublication.Platform;
import io.papermc.hangarpublishplugin.HangarPublication.PlatformDetails;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.jetbrains.annotations.Nullable;

final class HangarVersion {
    private final String version;
    private final Map<Platform, List<PluginDependency>> pluginDependencies;
    private final Map<Platform, List<String>> platformDependencies;
    private final String description;
    private final List<FileData> files;
    private final String channel;

    private HangarVersion(
        final String version,
        final Map<Platform, List<PluginDependency>> pluginDependencies,
        final Map<Platform, List<String>> platformDependencies,
        final String description,
        final List<FileData> files,
        final String channel
    ) {
        this.version = version;
        this.pluginDependencies = pluginDependencies;
        this.platformDependencies = platformDependencies;
        this.description = description;
        this.files = files;
        this.channel = channel;
    }

    public static HangarVersion fromPublication(final HangarPublication publication) {
        final Collection<PlatformDetails> platforms = publication.getPlatforms().getAsMap().values();
        final Map<Platform, List<PluginDependency>> pluginDependencies = platforms.stream().collect(Collectors.toMap(
            platform -> platform.getPlatform().get(),
            platform -> platform.getDependencies().getAsMap().values().stream().map(PluginDependency::fromDependencyDetails).collect(Collectors.toList())
        ));
        final Map<Platform, List<String>> platformDependencies = platforms.stream().collect(Collectors.toMap(
            details -> details.getPlatform().get(),
            details -> details.getPlatformVersions().get()
        ));
        // TODO: Check if the file for different platforms is the same and collect them in the same FileData
        final List<FileData> fileData = platforms.stream().map(platform -> new FileData(Arrays.asList(platform.getPlatform().get()))).collect(Collectors.toList());
        return new HangarVersion(
            publication.getVersion().get(),
            pluginDependencies,
            platformDependencies,
            publication.getChangelog().getOrNull(),
            fileData,
            publication.getChannel().get()
        );
    }

    public String version() {
        return version;
    }

    public Map<Platform, List<PluginDependency>> pluginDependencies() {
        return pluginDependencies;
    }

    public Map<Platform, List<String>> platformDependencies() {
        return platformDependencies;
    }

    public String description() {
        return description;
    }

    public List<FileData> files() {
        return files;
    }

    public String channel() {
        return channel;
    }

    static final class PluginDependency {
        private final String name;
        private final boolean required;
        private final HangarProjectNamespaceSerializable namespace;
        private final String externalUrl;

        private PluginDependency(final String name, final boolean required, @Nullable final HangarProjectNamespaceSerializable namespace, @Nullable final String externalUrl) {
            if (namespace == null && externalUrl == null) {
                throw new IllegalArgumentException("Either a Hangar namespace or an external url needs to be defined");
            }
            this.name = name;
            this.required = required;
            this.namespace = namespace;
            this.externalUrl = externalUrl;
        }

        public static PluginDependency fromDependencyDetails(final DependencyDetails dependencyDetails) {
            if (dependencyDetails.getHangarNamespace().isPresent()) {
                final HangarProjectNamespace ns = dependencyDetails.getHangarNamespace().get();
                final HangarProjectNamespaceSerializable namespace = new HangarProjectNamespaceSerializable(ns.getOwner().get(), ns.getSlug().get());
                return PluginDependency.createWithHangarNamespace(dependencyDetails.getName(), dependencyDetails.getRequired().get(), namespace);
            }
            return PluginDependency.createWithUrl(dependencyDetails.getName(), dependencyDetails.getRequired().get(), dependencyDetails.getUrl().get());
        }

        public static PluginDependency createWithHangarNamespace(final String name, final boolean required, final HangarProjectNamespaceSerializable namespace) {
            return new PluginDependency(name, required, namespace, null);
        }

        public static PluginDependency createWithUrl(final String name, final boolean required, final String externalUrl) {
            return new PluginDependency(name, required, null, externalUrl);
        }

        public String name() {
            return name;
        }

        public boolean required() {
            return required;
        }

        @Nullable
        public HangarProjectNamespaceSerializable namespace() {
            return namespace;
        }

        @Nullable
        public String externalUrl() {
            return externalUrl;
        }
    }

    static final class FileData {
        private final List<Platform> platforms;
        private final String externalUrl;

        FileData(final List<Platform> platforms) {
            this(platforms, null);
        }

        FileData(final List<Platform> platforms, @Nullable final String externalUrl) {
            this.platforms = platforms;
            this.externalUrl = externalUrl;
        }

        public List<Platform> platforms() {
            return platforms;
        }

        @Nullable
        public String externalUrl() {
            return externalUrl;
        }
    }
}
