/*
 * Configurate
 * Copyright (C) zml and Configurate contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.spongepowered.configurate.yaml;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.yaml.snakeyaml.reader.StreamReader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public interface YamlTest {

    default CommentedConfigurationNode parseString(final String input) {
        final YamlParser parser = new YamlParser(new ConfigurateScanner(new StreamReader(input)));
        final CommentedConfigurationNode result = CommentedConfigurationNode.root();
        try {
            parser.singleDocumentStream(result);
        } catch (final IOException ex) {
            fail(ex);
        }
        return result;
    }

    default CommentedConfigurationNode parseResource(final URL url) {
        assertNotNull(url, "Expected resource is missing");
        try {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
                final YamlParser parser = new YamlParser(new ConfigurateScanner(new StreamReader(reader)));
                final CommentedConfigurationNode result = CommentedConfigurationNode.root();
                parser.singleDocumentStream(result);
                return result;
            }
        } catch (final IOException ex) {
            fail(ex);
            throw new AssertionError();
        }
    }

    default String dump(final CommentedConfigurationNode input) {
        return dump(input, null);
    }

    default String dump(final CommentedConfigurationNode input, final @Nullable NodeStyle preferredStyle) {
        final StringWriter writer = new StringWriter();
        try {
            YamlConfigurationLoader.builder()
                    .setSink(() -> new BufferedWriter(writer))
                    .setNodeStyle(preferredStyle)
                    .build().save(input);
        } catch (IOException e) {
            fail(e);
        }
        return writer.toString();
    }


}
