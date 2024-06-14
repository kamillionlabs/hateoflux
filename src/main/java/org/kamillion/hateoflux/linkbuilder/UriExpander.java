/*
 * Copyright (c)  2024 kamillion-suite contributors
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
 *
 * @since 13.06.2024
 */

package org.kamillion.hateoflux.linkbuilder;

import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for expanding URI templates. Provides methods to expand URI templates using either ordered parameters
 * or named parameters from a map. The class handles templates indicated by placeholders enclosed in curly braces {}. If
 * a provided URI string does not contain placeholders, it is returned as is.
 *
 * @author Younes El Ouarti
 */
public class UriExpander {
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{([^}]+)\\}");

    /**
     * Expands the URI template using the given ordered path variables. If the template contains no placeholders, the
     * original string is returned.
     * <p>
     * Example usage:
     * <pre>
     * String template = "/users/{userId}/posts/{postId}";
     * String expanded = UriExpander.expand(template, 15, 1015);  // Outputs: /users/15/posts/1015
     * </pre>
     *
     * @param uriTemplate
     *         the URI template containing placeholders.
     * @param pathVariables
     *         the variables to replace the placeholders in order.
     * @return the expanded or original URI.
     *
     * @throws IllegalArgumentException
     *         if there are too many or too few variables provided.
     */
    public static String expand(String uriTemplate, Object... pathVariables) {
        assertStringIsATemplate(uriTemplate);

        Matcher matcher = VARIABLE_PATTERN.matcher(uriTemplate);
        StringBuffer sb = new StringBuffer();
        int i = 0;
        while (matcher.find()) {
            if (i >= pathVariables.length) {
                throw new IllegalArgumentException(
                        "Not enough arguments provided to expand the URI template. Template was: '" + uriTemplate +
                                "', path variables were: " + Arrays.toString(pathVariables));
            }
            matcher.appendReplacement(sb, pathVariables[i++].toString());
        }
        matcher.appendTail(sb);

        if (i < pathVariables.length) {
            throw new IllegalArgumentException(
                    "Too many arguments provided for the URI template. Template was: '" + uriTemplate + //
                            "', path variables were: " + Arrays.toString(pathVariables));
        }

        return sb.toString();
    }

    /**
     * Expands the URI template using a map of named path variables. If the template contains no placeholders, the
     * original string is returned.
     * <p>
     * Example usage:
     * <pre>
     * Map<String, Object> map = Map.of("userId", 15, "postId", 1057);
     * String expanded = UriExpander.expand("/users/{userId}/posts/{postId}", map);  // Outputs: /users/15/posts/1057
     * </pre>
     *
     * @param uriTemplate
     *         the URI template containing placeholders.
     * @param pathVariables
     *         a map containing key-value pairs where keys match the placeholders' names. Keys are
     *         case-insensitive
     * @return the expanded or original URI.
     *
     * @throws IllegalArgumentException
     *         if any placeholders are unmatched or if there are mismatches in the number of
     *         arguments.
     */
    public static String expand(String uriTemplate, Map<String, Object> pathVariables) {
        assertStringIsATemplate(uriTemplate);
        Matcher matcher = VARIABLE_PATTERN.matcher(uriTemplate);
        StringBuffer sb = new StringBuffer();
        Set<String> usedKeys = new HashSet<>();

        while (matcher.find()) {
            String key = matcher.group(1);
            if (!pathVariables.containsKey(key)) {
                throw new IllegalArgumentException(
                        "Expanding URL failed; No matching variable found for '" + matcher.group(
                                1) + "' in provided keys.");
            }
            matcher.appendReplacement(sb, pathVariables.get(key).toString());
            usedKeys.add(key);
        }
        matcher.appendTail(sb);

        // Check for unused keys in the provided map
        final List<String> unusedKeys = pathVariables.keySet().stream()//
                .filter(k -> !usedKeys.contains(k)) //
                .toList();
        if (!unusedKeys.isEmpty()) {
            throw new IllegalArgumentException("Expanding URL '" + uriTemplate //
                    + "' ended without using all provided keys. " //
                    + "The following stayed unused: " //
                    + String.join(",", unusedKeys));
        }
        return sb.toString();
    }

    private static void assertStringIsATemplate(final String uriTemplate) {
        if (!StringUtils.hasText(uriTemplate) || !uriTemplate.contains("{")) {
            throw new IllegalArgumentException("Provided string is not a template. Was '" + uriTemplate + "'");
        }
    }
}
