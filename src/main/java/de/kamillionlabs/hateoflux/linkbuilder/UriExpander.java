/*
 * Copyright (c)  2024 kamillionlabs contributors
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

package de.kamillionlabs.hateoflux.linkbuilder;

import org.springframework.web.util.UriTemplate;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Utility class for expanding URI templates. Provides methods to expand URI templates using either ordered parameters
 * or named parameters from a map. The class handles templates indicated by placeholders enclosed in curly braces {}.
 *
 * @author Younes El Ouarti
 */
public class UriExpander {

    private static final Set<String> PAGING_QUERY_PARAMETER = Set.of("page", "sort", "size");

    private UriExpander() {
    }

    /**
     * Constructs a URL-encoded query string from a list of query parameters. This method supports both single values
     * and collections of values. Collections are encoded as multiple key-value pairs or as a single key with a
     * comma-separated list
     * of values, depending on the properties of each {@link QueryParameter} object.
     * <p>
     * <b>Example usages:</b><br>
     * <i>Query Parameters with Composite Rendering ({@code true})</i>
     * <blockquote><pre>
     * List<QueryParameter> params = List.of(
     *     QueryParameter.of("size", "medium"),
     *     QueryParameter.of("color", List.of("blue", "green"), true)
     * );                                                       ^^^^
     * String uriPart = constructExpandedQueryParameterUriPart(params);
     *
     * // Expected output: ?size=medium&color=blue&color=green
     * </pre></blockquote>
     * <p>
     * <i>Query Parameters with Non-Composite Rendering ({@code false})</i>
     * <blockquote><pre>
     * List<QueryParameter> params = List.of(
     *     QueryParameter.of("author", "johndoe"),
     *     QueryParameter.of("tag", List.of("fit", "gym"), false)
     * );                                                  ^^^^^
     * String uriPart = constructExpandedQueryParameterUriPart(params);
     *
     * // Expected output: ?author=johndoe&tag=fit,gym
     * </pre></blockquote>
     *
     * @param parameters
     *         a list of {@link QueryParameter} objects representing the query parameters to be included in the URI part
     * @return a string representing the URI part constructed from the query parameters
     */
    static String constructExpandedQueryParameterUriPart(List<QueryParameter> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return "";
        }

        StringJoiner joiner = new StringJoiner("&", "?", "");

        for (QueryParameter parameter : parameters) {
            // Encode keys and values to ensure they are URL safe
            String key = encode(parameter.getName(), UTF_8);
            if (parameter.isCollection()) {
                List<String> listOfValues = parameter.getListOfValues();
                if (parameter.isRenderedAsComposite()) {
                    for (String value : listOfValues) {
                        joiner.add(key + "=" + value);
                    }
                } else {
                    String aggregateValues = listOfValues.stream()
                            .filter(Objects::nonNull)
                            .map(v -> encode(v, UTF_8))
                            .collect(Collectors.joining(","));
                    joiner.add(key + "=" + aggregateValues);
                }
            } else {
                joiner.add(key + "=" + encode(parameter.getValue(), UTF_8));
            }
        }
        return joiner.toString();
    }

    /**
     * Constructs a URL-encoded query string from a map of parameters. It supports both single values and collections
     * of values, with the output depending on the {@code collectionRenderedAsComposite} flag, which dictates whether
     * collections are rendered in a composite or non-composite way.
     * <p>
     * <b>Example usages:</b><br>
     * <i>Query Parameters with Composite Rendering ({@code true})</i>
     * <blockquote><pre>
     * Map<String, Object> params = Map.of(
     *     "size", "medium",
     *     "color", List.of("blue", "green")
     * );
     * var o = constructExpandedQueryParameterUriPart(params, true);
     *                                                        ^^^^
     * // Expected output: ?size=medium&color=blue&color=green
     * </pre></blockquote>
     * <p>
     * <i>Query Parameters with Non-Composite Rendering ({@code false})</i>
     * <blockquote><pre>
     * Map<String, Object> params = Map.of(
     *     "author", "johndoe",
     *     "tag", List.of("fit", "gym")
     * );
     * var o =constructExpandedQueryParameterUriPart(params, false);
     *                                                       ^^^^^
     * // Expected output: ?author=johndoe&tag=fit,gym
     * </pre></blockquote>
     *
     * @param parameters
     *         a map where each entry consists of a parameter name and its associated value(s), either as a single value
     *         or a collection
     * @param collectionRenderedAsComposite
     *         a boolean flag that determines whether collections of values are rendered in a composite (true) or
     *         non-composite (false) way
     * @return a string representing the URI part constructed from the query parameters
     */

    static String constructExpandedQueryParameterUriPart(Map<String, ?> parameters,
                                                         boolean collectionRenderedAsComposite) {
        if (parameters == null || parameters.isEmpty()) {
            return "";
        }

        //convert to list of query parameters
        List<QueryParameter> queryParameterList = new ArrayList<>();
        for (var entry : parameters.entrySet()) {
            var builder = QueryParameter.builder();
            builder.name(entry.getKey());
            Object value = entry.getValue();
            if (value instanceof Collection<?> valueAsCollection) {
                builder.listOfValues(valueAsCollection, collectionRenderedAsComposite);
            } else {
                builder.value(value);
            }
            queryParameterList.add(builder.build());
        }
        return constructExpandedQueryParameterUriPart(queryParameterList);
    }


    /**
     * Expands the URI template using a list of anonymous parameters provided in the order they appear within the
     * template. Placeholders for parameters follow the structure suggested by RFC6570. Given that {@code var} is a
     * placeholder, i.e., a templated variable, the following applies:
     * <ol>
     *     <li>{@code {var}} is a mandatory variable.</li>
     *     <li>{@code {?var}} is an optional variable used specifically as a query parameter.</li>
     *     <li>{@code {?var1,var2}} are two optional query parameters.</li>
     * </ol>
     * This method does not support exploded query parameters (if required use this {@link #expand(String, Map)
     * expand()} instead).<br>
     * <br>
     * <p>
     * <b>Example usage:</b>
     * <blockquote><pre>
     * String template = "/users/{userId}/posts{?limit,page}"
     * String expanded = expand(template, 42, 10, 2);
     *
     * // Outputs: /users/42/posts?limit=10&amp;page=2
     * </pre></blockquote>
     *
     * @param uriAsTemplate
     *         URI template containing placeholders
     * @param parameters
     *         a sequence of objects that correspond in order to the placeholders in the URI template
     * @return the expanded or original URI if expansion is not applicable
     *
     * @throws IllegalArgumentException
     *         if template and parameters are incompatible
     */
    public static String expand(String uriAsTemplate, Object... parameters) {
        UriTemplateData uriTemplateData = UriTemplateData.of(uriAsTemplate);
        if (parameters == null || parameters.length == 0) {
            if (!uriTemplateData.isTemplated()) {
                return uriAsTemplate;
            }

            if (uriTemplateData.hasOnlyQueryParameters()) {
                return uriTemplateData.getUriTemplateWithoutQueryParameters();
            }

            throw new IllegalArgumentException(format(
                    "No parameters provided for URI expansion, but mandatory path parameters were detected. " +
                            "Template was '%s'", uriAsTemplate));
        }


        assertConsistentParameterArray(uriAsTemplate, parameters, uriTemplateData);

        List<String> pathParameterNames = uriTemplateData.getPathParameterNames();
        List<String> queryParameterNames = uriTemplateData.getQueryParameterNames();

        //We expect the parameters to be in order, with path parameters provided first, followed by query parameters
        UriTemplate uriTemplate = new UriTemplate(uriTemplateData.getUriTemplateWithoutQueryParameters());
        URI expandedUriWithPathParametersOnly = uriTemplate.expand(parameters);

        String queryParameterUriPart = "";
        if (!queryParameterNames.isEmpty()) {
            Map<String, Object> queryParameterMap =
                    createQueryParameterMap(parameters, pathParameterNames, queryParameterNames);

            //The flag is set to false but is not used anyway because in this expand method it is
            //not possible to have exploded parameters
            queryParameterUriPart = constructExpandedQueryParameterUriPart(queryParameterMap, false);
        }

        return expandedUriWithPathParametersOnly + queryParameterUriPart;
    }

    private static Map<String, Object> createQueryParameterMap(Object[] parameters, List<String> pathParameterNames,
                                                               List<String> queryParameterNames) {
        Map<String, Object> queryParameterMap = new LinkedHashMap<>();
        int queryParameterIndex = 0;

        //skip path parameters that are at the front
        for (int parametersIndex = pathParameterNames.size(); parametersIndex < parameters.length; parametersIndex++) {
            String parameterName = queryParameterNames.get(queryParameterIndex++);
            Object parameterValue = parameters[parametersIndex];
            queryParameterMap.put(parameterName, parameterValue);
        }
        return queryParameterMap;
    }

    /**
     * Expands the URI template using a map of named path or query parameters. The full documentation can be found at
     * {@link #expand(String, Map)}. This variation of {@code expand()} adds the ability to influence how exploded
     * parameters are rendered when a collection is provided.
     * <p>
     * <b>Example usages:</b><br>
     * <p>
     * <i>Exploded Query Parameter with Non-Composite Rendering ({@code false})</i>
     * <blockquote><pre>
     * var map = Map.of("keyWords", List.of("blue","active"));
     * String expanded = expand("/users{?keyWords*}", map, false);
     *                                                     ^^^^^
     * // Outputs: /users?keyWords=blue,active
     * </pre></blockquote>
     *
     * <p>
     * <i>Exploded Query Parameter with Composite Rendering ({@code true})</i>
     * <blockquote><pre>
     * var map = Map.of("keyWords", List.of("blue","active"));
     * String expanded = expand("/users{?keyWords*}", map, true);
     *                                                     ^^^^
     * // Outputs: /users?keyWords=blue&amp;keyWords=active
     * </pre></blockquote>
     *
     * @param uriAsTemplate
     *         URI template containing placeholders
     * @param parameters
     *         a map containing key-value pairs where keys match the placeholders' names. Values for exploded parameters
     *         can be a {@link Collection}.
     * @param collectionRenderedAsComposite
     *         specifies whether the collection should be rendered as composite (true) or non-composite (false)
     * @return the expanded or original URI if expansion is not applicable
     *
     * @throws IllegalArgumentException
     *         if template and parameters are incompatible
     */
    public static String expand(String uriAsTemplate, Map<String, ?> parameters,
                                boolean collectionRenderedAsComposite) {
        UriTemplateData uriTemplateData = UriTemplateData.of(uriAsTemplate);
        if ((parameters == null || parameters.isEmpty())) {
            if (!uriTemplateData.isTemplated()) {
                return uriAsTemplate;
            }

            if (uriTemplateData.hasOnlyQueryParameters()) {
                return uriTemplateData.getUriTemplateWithoutQueryParameters();
            }

            throw new IllegalArgumentException(format(
                    "No parameters provided for URI expansion, but mandatory path parameters were detected. " +
                            "Template was '%s'", uriAsTemplate));

        }

        assertConsistentParameterMap(uriTemplateData, parameters);

        UriTemplate uriTemplate = new UriTemplate(uriTemplateData.getUriTemplateWithoutQueryParameters());
        URI expandedUriWithPathParametersOnly = uriTemplate.expand(parameters);

        List<String> queryParameterNames = uriTemplateData.getQueryParameterNames();
        Map<String, ?> queryParameters = filterAccordingToWhitelist(queryParameterNames, parameters);
        String queryParameterUriPart = constructExpandedQueryParameterUriPart(
                queryParameters, collectionRenderedAsComposite);

        return expandedUriWithPathParametersOnly + queryParameterUriPart;
    }


    /**
     * Expands the URI template using a map of named path or query parameters. If the template contains no
     * placeholders, the original string is returned. Placeholders for parameters follow the structure suggested
     * by RFC6570. Given that {@code var} is a placeholder, i.e., a templated variable, the following applies:
     * <ol>
     *     <li>{@code {var}} is a mandatory variable.</li>
     *     <li>{@code {?var}} is an optional variable used specifically as a query parameter.</li>
     *     <li>{@code {?var1,var2}} are two optional query parameters.</li>
     *     <li>{@code {?var*}} is an exploded query parameter, i.e., it can represent a list.</li>
     * </ol>
     * <b>Hints on the explode modifier ('*'):</b>
     * <br>
     * <ul>
     *     <li>A collection of values is only allowed for query parameters.</li>
     *     <li>To accept a collection as a value, a query parameter must be marked with the
     *     explode modifier ('*' i.e., asterisk).</li>
     *     <li>The expansion of exploded parameters is configurable via {@link #expand(String, Map, boolean)}. It can
     *     be expanded in a composite or non-composite way.</li>
     *     <li>This method expands parameters in a non-composite way by default
     *     (e.g., ?var=1,2 as opposed to ?var=1&amp;var=2).</li>
     * </ul>
     *
     * <p>
     * <b>Example usages:</b><br>
     * <i>Path and Query Parameters</i>
     * <blockquote><pre>
     * var map = Map.of("id", 15,
     *                  "limit", 50,
     *                  "page", 2);
     * String expanded = expand("/users/{id}/activity{?limit,page}", map);
     *
     * // Outputs: /users/15/activity?limit=50&amp;page=2
     * </pre></blockquote>
     *
     * <p>
     * <i>Unused Query Parameters in Template</i>
     * <blockquote><pre>
     * var map = Map.of("id", 15);
     * String expanded = expand("/users/{id}/activity{?limit,page}", map);
     *
     * // Outputs: /users/15/activity
     * </pre></blockquote>
     *
     * <p>
     * <i>Exploded Query Parameter</i>
     * <blockquote><pre>
     * var map = Map.of("keyWords", List.of("blue","active")
     *                                      "page", 3));
     * String expanded = expand("/users{?keyWords*,page}", map);
     *
     * // Outputs: /users?keyWords=blue,active&amp;page=3
     * </pre></blockquote>
     *
     * @param uriAsTemplate
     *         URI template containing placeholders
     * @param parameters
     *         a map containing key-value pairs, where keys match the placeholders' names. Values for exploded
     *         parameters are allowed to be a {@link Collection}
     * @return the expanded or original URI if expansion is not applicable
     *
     * @throws IllegalArgumentException
     *         if template and parameters are incompatible
     */
    public static String expand(String uriAsTemplate, Map<String, ?> parameters) {
        return expand(uriAsTemplate, parameters, false);
    }

    private static void assertConsistentParameterArray(String uriAsTemplate, Object[] parameters,
                                                       UriTemplateData uriTemplateData) {
        if (uriTemplateData.hasExplodedQueryParameters()) {
            throw new IllegalArgumentException(format("Exploded query parameters cannot be expanded using only " +
                    "values. Use expansion method that assigns values to dedicated parameters. " +
                    "Template was '%s'", uriAsTemplate));
        }

        String parameterValues = Arrays.stream(parameters).map(Object::toString).collect(Collectors.joining(","));

        if (parameters.length > uriTemplateData.getTotalNumberOfParameters()) {
            throw new IllegalArgumentException(format("Provided more parameters for URI template expansion than " +
                            "expected. Template was '%s', parameter values were [%s]",
                    uriAsTemplate, parameterValues));
        }

        if (parameters.length < uriTemplateData.getPathParameterNames().size()) {
            throw new IllegalArgumentException(format(
                    "Not enough mandatory path parameters provided for URI template expansion. " +
                            "Template was '%s', parameter values were [%s]",
                    uriAsTemplate, parameterValues));
        }
    }

    private static void assertConsistentParameterMap(UriTemplateData uriTemplateData, Map<String, ?> parameters) {
        Set<String> parameterNamesToTest = Optional.ofNullable(parameters)
                .map(p -> new HashSet<>(p.keySet()))
                .orElse(new HashSet<>());

        String originalUriTemplate = uriTemplateData.getOriginalUriTemplate();

        if (uriTemplateData.doesNotIncludeAllPathParameters(parameterNamesToTest)) {
            throw new IllegalArgumentException(format(
                    "Not enough mandatory path parameters provided for URI template expansion. " +
                            "Template was '%s', parameters were %s", originalUriTemplate, parameters));
        }

        // paging parameters are allowed to be "unknown" because they can be generated and are generally handled
        // automatically
        Set<String> parameterNamesExcludingPagingParameters = new HashSet<>(parameterNamesToTest);
        parameterNamesExcludingPagingParameters.removeAll(PAGING_QUERY_PARAMETER);

        if (!parameterNamesToTest.isEmpty()
                && uriTemplateData.includesUnknownParameters(parameterNamesExcludingPagingParameters)) {
            throw new IllegalArgumentException(format(
                    "Unknown parameters provided for URI template expansion. " +
                            "Template was '%s', parameters were %s", originalUriTemplate, parameters));

        }

        for (var parameterName : parameterNamesToTest) {
            Object parameterValue = parameters.get(parameterName);
            if (parameterValue instanceof Collection<?> parameterValueAsCollection) {
                if (!uriTemplateData.isExplodedQueryParameter(parameterName) && parameterValueAsCollection.size() > 1) {
                    throw new IllegalArgumentException(format(
                            "Detected a collection of values for a parameter, but parameter was not exploded in " +
                                    "template (asterisk after parameter name e.g. {?var*}). " +
                                    "Template was '%s', parameters were %s", originalUriTemplate, parameters
                    ));
                }
            }
        }
    }

    private static Map<String, ?> filterAccordingToWhitelist(List<String> parameterWhiteList, Map<String,
            ?> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return parameters;
        }

        if (parameterWhiteList == null || parameterWhiteList.isEmpty()) {
            return new LinkedHashMap<>();
        }

        Map<String, Object> result = new LinkedHashMap<>();
        for (var whiteListedParameter : parameterWhiteList) {
            for (var key : parameters.keySet()) {
                if (whiteListedParameter.equals(key)) {
                    result.put(key, parameters.get(key));
                }
            }
        }
        return result;
    }

    /**
     * Removes existing paging query parameters {@code page}, {@code size}, and {@code sort}. Any other query parameter
     * is ignored. If as a result the URI has no query paramters anymore, the '?' is removed.
     *
     * @param inputUrl
     *         input URL to remove paging parameters from
     * @return sanitized URL
     */
    public static String removePagingParameters(String inputUrl) {
        // Split the inputUrl into base URL and query string
        if (inputUrl == null || inputUrl.isEmpty()) {
            return inputUrl;
        }

        int idx = inputUrl.indexOf('?');
        if (idx == -1) {
            // No query parameters
            return inputUrl;
        }
        String baseUrl = inputUrl.substring(0, idx);
        String query = inputUrl.substring(idx + 1);

        // Regex pattern to match 'page', 'size', and 'sort' parameters
        String pattern = "(^|&)(page|size|sort)(=[^&]*)?(?=&|$)";
        query = query.replaceAll(pattern, "");

        // Remove any leading or trailing '&' characters
        query = query.replaceAll("^&+", "").replaceAll("&+$", "");

        // Build the output URL
        if (query.isEmpty()) {
            return baseUrl;
        } else {
            return baseUrl + "?" + query;
        }
    }

}
