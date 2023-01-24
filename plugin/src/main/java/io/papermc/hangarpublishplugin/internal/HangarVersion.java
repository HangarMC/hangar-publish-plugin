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

import io.papermc.hangarpublishplugin.HangarProjectNamespace;
import io.papermc.hangarpublishplugin.HangarPublication;
import io.papermc.hangarpublishplugin.HangarPublication.DependencyDetails;
import io.papermc.hangarpublishplugin.HangarPublication.PlatformDetails;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
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

    public static HangarVersion fromPublication(final HangarPublication publication) throws IOException {
        final Collection<PlatformDetails> platformDetails = publication.getPlatforms().getAsMap().values();
        final Map<String, List<PluginDependency>> pluginDependencies = platformDetails.stream().collect(Collectors.toMap(
            PlatformDetails::getPlatform,
            details -> details.getDependencies().stream().map(PluginDependency::fromDependencyDetails).collect(Collectors.toList())
        ));
        final Map<String, List<String>> platformDependencies = platformDetails.stream().collect(Collectors.toMap(
            PlatformDetails::getPlatform,
            details -> details.getPlatformVersions().get()
        ));
        return new HangarVersion(
            publication.getVersion().get(),
            pluginDependencies,
            platformDependencies,
            publication.getChangelog().getOrNull(),
            FileData.fromPlatformDetails(platformDetails),
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
            if (platforms.isEmpty()) {
                throw new IllegalArgumentException("At least one platform needs to be provided in FileData.");
            }

            this.platforms = platforms;
            this.externalUrl = externalUrl;
        }

        public List<String> platforms() {
            return this.platforms;
        }

        @Nullable
        public String externalUrl() {
            return this.externalUrl;
        }

        public String firstPlatform() {
            return this.platforms.get(0);
        }

        public boolean isFile() {
            return this.externalUrl == null;
        }

        public static List<FileData> fromPlatformDetails(final Collection<PlatformDetails> platformDetails) throws IOException {
            // Check for duplicate files and collect them as one
            final Map<File, List<String>> filePlatforms = new LinkedHashMap<>();
            final Map<String, List<String>> urlPlatforms = new LinkedHashMap<>();
            for (final PlatformDetails details : platformDetails) {
                final File platformFile = details.getJar().getAsFile().getOrNull();
                if (platformFile == null) {
                    addURLPlatform(details, urlPlatforms);
                    continue;
                }

                final List<String> platforms = filePlatforms.get(platformFile);
                if (platforms != null) {
                    platforms.add(details.getPlatform());
                    continue;
                }

                boolean newFile = true;
                for (final Map.Entry<File, List<String>> entry : filePlatforms.entrySet()) {
                    if (entry.getKey().getCanonicalPath().equals(platformFile.getCanonicalPath())) {
                        entry.getValue().add(details.getPlatform());
                        newFile = false;
                        break;
                    }
                }

                if (newFile) {
                    final List<String> newPlatforms = new ArrayList<>();
                    newPlatforms.add(details.getPlatform());
                    filePlatforms.put(platformFile, newPlatforms);
                }
            }

            final List<FileData> fileData = filePlatforms.values().stream().map(FileData::new).collect(Collectors.toList());
            for (final List<String> platforms : urlPlatforms.values()) {
                fileData.add(new FileData(platforms));
            }
            return fileData;
        }

        private static void addURLPlatform(final PlatformDetails details, final Map<String, List<String>> urlPlatforms) {
            final String url = details.getUrl().get();
            if (url == null) {
                throw new InvalidUserDataException("A platform download must provide a jar file or URL.");
            }

            urlPlatforms.compute(url, (key, value) -> {
                if (value == null) {
                    final List<String> platforms = new ArrayList<>();
                    platforms.add(details.getPlatform());
                    return platforms;
                }
                value.add(details.getPlatform());
                return value;
            });
        }
    }
}
