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

    public static String constructExpandedQueryParameterUriPart(Map<String, Object> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return "";
        }

        StringJoiner joiner = new StringJoiner("&", "?", "");

        for (var entry : parameters.entrySet()) {
            // Encode keys and values to ensure they are URL safe
            String key = encode(entry.getKey(), UTF_8);
            String value = encode(entry.getValue().toString(), UTF_8);
            joiner.add(key + "=" + value);
        }
        return joiner.toString();
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

        List<String> pathParameterNames = uriTemplateData.getPathParameters();
        List<String> queryParameterNames = uriTemplateData.getQueryParameters();

        //We expect the parameters to be in order, with path parameters provided first, followed by query parameters
        UriTemplate uriTemplate = new UriTemplate(uriTemplateData.getUriTemplateWithoutQueryParameters());
        URI expandedUriWithPathParametersOnly = uriTemplate.expand(parameters);

        String queryParameterUriPart = "";
        if (!queryParameterNames.isEmpty()) {
            Map<String, Object> queryParameterMap =
                    createQueryParameterMap(parameters, pathParameterNames, queryParameterNames);

            queryParameterUriPart = constructExpandedQueryParameterUriPart(queryParameterMap);
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


    public static String expand(String uriAsTemplate, Map<String, Object> parameters) {
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

        List<String> queryParameterNames = uriTemplateData.getQueryParameters();
        Map<String, Object> queryParameters = filterAccordingToWhitelist(queryParameterNames, parameters);
        String queryParameterUriPart = constructExpandedQueryParameterUriPart(queryParameters);

        return expandedUriWithPathParametersOnly + queryParameterUriPart;
    }

    private static void assertConsistentParameterArray(String uriAsTemplate, Object[] parameters,
                                                       UriTemplateData uriTemplateData) {
        String parameterValues = Arrays.stream(parameters).map(Object::toString).collect(Collectors.joining(","));
        if (parameters.length > uriTemplateData.getTotalNumberOfParameters()) {
            throw new IllegalArgumentException(format("Provided more parameters for URI template expansion than " +
                            "expected. Template was '%s', parameter values were [%s]",
                    uriAsTemplate, parameterValues));
        }

        if (parameters.length < uriTemplateData.getPathParameters().size()) {
            throw new IllegalArgumentException(format(
                    "Not enough mandatory path parameters provided for URI template expansion. " +
                            "Template was '%s', parameter values were [%s]",
                    uriAsTemplate, parameterValues));
        }
    }

    private static void assertConsistentParameterMap(UriTemplateData uriTemplateData, Map<String, Object> parameters) {
        Set<String> parameterNamesToTest = Optional.ofNullable(parameters)
                .map(p -> new HashSet<>(p.keySet()))
                .orElse(new HashSet<>());

        String originalUriTemplate = uriTemplateData.getOriginalUriTemplate();
        String parameterNames = String.join(",", parameterNamesToTest);

        if (uriTemplateData.doesNotIncludeAllPathParameters(parameterNamesToTest)) {
            String providedValues = parameterNames.isEmpty() ? "empty or null" : String.join(", ",
                    parameterNames);
            throw new IllegalArgumentException(format(
                    "Not enough mandatory path parameters provided for URI template expansion. " +
                            "Template was '%s', parameters were [%s]", originalUriTemplate, providedValues));
        }

        if (!parameterNamesToTest.isEmpty() && uriTemplateData.includesUnknownParameters(parameterNamesToTest)) {
            throw new IllegalArgumentException(format(
                    "Unknown parameters provided for URI template expansion. " +
                            "Template was '%s', parameters were [%s]", originalUriTemplate, parameterNames));

        }
    }

    private static Map<String, Object> filterAccordingToWhitelist(List<String> parameterWhiteList, Map<String,
            Object> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return parameters;
        }

        if (parameterWhiteList == null || parameterWhiteList.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, Object> result = new HashMap<>();
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
