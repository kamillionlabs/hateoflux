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

import org.springframework.web.util.UriTemplate;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Utility class for expanding URI templates. Provides methods to expand URI templates using either ordered parameters
 * or named parameters from a map. The class handles templates indicated by placeholders enclosed in curly braces {}. If
 * a provided URI string does not contain placeholders, it is returned as is.
 *
 * @author Younes El Ouarti
 */
public class UriExpander {

    public static String constructExpandedQueryParameterUriPart(List<QueryParameter> parameters) {
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


    public static String constructExpandedQueryParameterUriPart(Map<String, ?> parameters,
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

            //collectionRenderedAsComposite is set as false but is not used anyway because in this expand method it is
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
     * Expands the URI template using a map of named path or query parameters. If the template contains no placeholders,
     * the original string is returned. Placeholders for query parameters follow the structure suggested by RFC6570.
     * Given {@code var} is a templated variable this means:
     * <ol>
     *     <li>{@code {var}} is a mandatory variable</li>
     *     <li>{@code {?var}} is an optional variable used specifically as query parameter</li>
     *     <li>{@code {?var1,var2}} are 2 optional query parameters</li>
     * </ol>
     * <p>
     * <b>Example usages:</b><br>
     * <i>Path and Query Parameters</i>
     * <blockquote><pre>
     * Map<String, Object> map = Map.of("id", 15, "limit", 50, "page", 2);
     * String expanded = UriExpander.expand("/users/{id}/activity{?limit,page}", map);
     *
     * // Outputs: /users/15/activity?limit=50&page=2
     * </pre></blockquote>
     * <p>
     * <i>Non Existing Query Parameters in Map</i>
     * <blockquote><pre>
     * Map<String, Object> map = Map.of("id", 15);
     * String expanded = UriExpander.expand("/users/{id}/activity{?limit,page}", map);
     *
     * // Outputs: /users/15/activity
     * </pre></blockquote>
     * <p>
     * <i>Query Parameters in Template with None Provided</i>
     * <blockquote><pre>
     * String expanded = UriExpander.expand("/users/15/activity{?limit,page}", Map.of());
     *
     * // Outputs: /users/15/activity
     * </pre></blockquote>
     * <p>
     * TODO explodedParam
     *
     * @param uriAsTemplate
     *         the URI template containing placeholders
     * @param parameters
     *         a map containing key-value pairs where keys match the placeholders' names
     * @param collectionRenderedAsComposite
     *         TODO
     * @return the expanded or original URI
     *
     * @throws IllegalArgumentException
     *         if any placeholders are unmatched or if there are mismatches in the number of
     *         arguments
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
     * TODO
     *
     * @param uriAsTemplate
     * @param parameters
     * @return
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

        if (!parameterNamesToTest.isEmpty() && uriTemplateData.includesUnknownParameters(parameterNamesToTest)) {
            throw new IllegalArgumentException(format(
                    "Unknown parameters provided for URI template expansion. " +
                            "Template was '%s', parameters were %s", originalUriTemplate, parameters));

        }

        for (var parameterName : parameterNamesToTest) {
            Object parameterValue = parameters.get(parameterName);
            if (parameterValue instanceof Collection<?>) {
                if (!uriTemplateData.isExplodedQueryParameter(parameterName)) {
                    throw new IllegalArgumentException(format(
                            "Detected a collection as value for a parameter, but parameter was not exploded in " +
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

}
