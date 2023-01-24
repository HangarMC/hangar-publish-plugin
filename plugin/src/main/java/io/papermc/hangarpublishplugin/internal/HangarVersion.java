package io.papermc.hangarpublishplugin.internal;

import io.papermc.hangarpublishplugin.HangarProjectNamespace;
import io.papermc.hangarpublishplugin.HangarPublication;
import io.papermc.hangarpublishplugin.HangarPublication.DependencyDetails;
import io.papermc.hangarpublishplugin.HangarPublication.PlatformDetails;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.gradle.api.InvalidUserDataException;
import org.jetbrains.annotations.Nullable;

final class HangarVersion {
    private final String version;
    private final Map<String, List<PluginDependency>> pluginDependencies;
    private final Map<String, List<String>> platformDependencies;
    private final String description;
    private final List<FileData> files;
    private final String channel;

    private HangarVersion(
        final String version,
        final Map<String, List<PluginDependency>> pluginDependencies,
        final Map<String, List<String>> platformDependencies,
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
        final Map<String, List<PluginDependency>> pluginDependencies = platforms.stream().collect(Collectors.toMap(
            PlatformDetails::getPlatform,
            platform -> platform.getDependencies().getAsMap().values().stream().map(PluginDependency::fromDependencyDetails).collect(Collectors.toList())
        ));
        final Map<String, List<String>> platformDependencies = platforms.stream().collect(Collectors.toMap(
            PlatformDetails::getPlatform,
            details -> details.getPlatformVersions().get()
        ));
        // TODO: Check if the file for different platforms is the same and collect them in the same FileData
        final List<FileData> fileData = platforms.stream().map(platform -> new FileData(Arrays.asList(platform.getPlatform()))).collect(Collectors.toList());
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

    public Map<String, List<PluginDependency>> pluginDependencies() {
        return pluginDependencies;
    }

    public Map<String, List<String>> platformDependencies() {
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
            if (dependencyDetails.getHangarNamespace().isPresent() && !dependencyDetails.getUrl().isPresent()) {
                final HangarProjectNamespace ns = dependencyDetails.getHangarNamespace().get();
                final HangarProjectNamespaceSerializable namespace = new HangarProjectNamespaceSerializable(ns.getOwner().get(), ns.getSlug().get());
                return PluginDependency.createWithHangarNamespace(dependencyDetails.getName(), dependencyDetails.getRequired().get(), namespace);
            } else if (!dependencyDetails.getHangarNamespace().isPresent() && dependencyDetails.getUrl().isPresent()) {
                return PluginDependency.createWithUrl(dependencyDetails.getName(), dependencyDetails.getRequired().get(), dependencyDetails.getUrl().get());
            }
            throw new InvalidUserDataException("A plugin dependency must provide a URL or a Hangar namespace, but not both.");
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
        private final List<String> platforms;
        private final String externalUrl;

        FileData(final List<String> platforms) {
            this(platforms, null);
        }

        FileData(final List<String> platforms, @Nullable final String externalUrl) {
            this.platforms = platforms;
            this.externalUrl = externalUrl;
        }

        public List<String> platforms() {
            return platforms;
        }

        @Nullable
        public String externalUrl() {
            return externalUrl;
        }
    }
}
