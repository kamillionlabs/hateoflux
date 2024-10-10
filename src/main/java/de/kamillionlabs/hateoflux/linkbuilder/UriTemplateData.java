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
 * @since 23.08.2024
 */

package de.kamillionlabs.hateoflux.linkbuilder;

import de.kamillionlabs.hateoflux.utility.Pair;
import de.kamillionlabs.hateoflux.utility.PairList;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.Assert;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;

/**
 * Represents a parsed URI template and provides methods to retrieve information from it.
 *
 * @author Younes El Ouarti
 */
@Data
public class UriTemplateData {

    private static final Pattern PATH_VARIABLE_PATTERN = Pattern.compile("(?<!\\?)\\{([^?}]+)}");

    private static final String QUERY_PARAMETER_PLACEHOLDER_REGEX =
            "\\{\\?([a-zA-Z0-9_-]+\\*?(?:,\\s*[a-zA-Z0-9_-]+\\*?)*)}$";

    private static final Pattern QUERY_VARIABLE_PATTERN = Pattern.compile(QUERY_PARAMETER_PLACEHOLDER_REGEX);

    @Setter(AccessLevel.PRIVATE)
    private String originalUriTemplate = "";

    @Setter(AccessLevel.PRIVATE)
    private String uriTemplateWithoutQueryParameters = "";

    @Setter(AccessLevel.PRIVATE)
    private List<String> pathParameterNames = new ArrayList<>();

    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private PairList<String, Boolean> queryParameterNamesByExplodability = new PairList<>();

    /**
     * Returns the names of the query parameters in the URI template.
     *
     * @return a list of query parameter names
     */
    public List<String> getQueryParameterNames() {
        return queryParameterNamesByExplodability.getLefts();
    }

    /**
     * Creates a new {@code UriTemplateData} instance from the given URI template string.
     *
     * @param originalUriTemplate
     *         the URI template string; may be null or empty
     * @return a new instance with the parsed URI template data
     */
    public static UriTemplateData of(String originalUriTemplate) {
        return new UriTemplateData(originalUriTemplate);
    }

    private UriTemplateData(String uriTemplate) {
        if (uriTemplate != null && !uriTemplate.isEmpty()) {
            this.originalUriTemplate = uriTemplate;
            this.uriTemplateWithoutQueryParameters = uriTemplate.replaceAll(QUERY_PARAMETER_PLACEHOLDER_REGEX, "");
            this.pathParameterNames = extractPathParameters(uriTemplate);
            this.queryParameterNamesByExplodability = extractQueryParametersByExplodability(uriTemplate);
        }
    }

    /**
     * Checks if the URI template contains any exploded query parameters.
     *
     * @return {@code true} if there are exploded query parameters; {@code false} otherwise
     */
    public boolean hasExplodedQueryParameters() {
        return queryParameterNamesByExplodability.getRights().stream()
                .anyMatch(p -> p); // is any boolean in the list true?
    }

    /**
     * Checks if the specified query parameter is exploded in the URI template.
     *
     * @param parameterName
     *         the name of the query parameter
     * @return {@code true} if the parameter is exploded; {@code false} otherwise
     */
    public boolean isExplodedQueryParameter(String parameterName) {
        return queryParameterNamesByExplodability.stream()
                .filter(Pair::right) // when right==true --> is explodable
                .anyMatch(p -> p.left().equals(parameterName));
    }

    /**
     * Checks if the URI template contains only query parameters and no path parameters.
     *
     * @return {@code true} if the template has only query parameters; {@code false} otherwise
     */
    public boolean hasOnlyQueryParameters() {
        return pathParameterNames.isEmpty() && !queryParameterNamesByExplodability.isEmpty();
    }

    /**
     * Checks if the given set of parameter names does not include all path parameters from the URI template.
     *
     * @param parameterNamesToTest
     *         the set of parameter names to test
     * @return {@code true} if not all path parameters are included; {@code false} otherwise
     */
    public boolean doesNotIncludeAllPathParameters(Set<String> parameterNamesToTest) {
        return !parameterNamesToTest.containsAll(pathParameterNames);
    }

    /**
     * Checks if the given set of parameter names includes any parameters not present in the URI template.
     *
     * @param parameterNamesToTest
     *         the set of parameter names to test
     * @return {@code true} if unknown parameters are included; {@code false} otherwise
     */
    public boolean includesUnknownParameters(Set<String> parameterNamesToTest) {
        Set<String> mergedParameters = new HashSet<>();
        mergedParameters.addAll(pathParameterNames);
        mergedParameters.addAll(getQueryParameterNames());

        return parameterNamesToTest.stream().anyMatch(p -> !mergedParameters.contains(p));
    }


    /**
     * Returns the total number of parameters (path and query) in the URI template.
     *
     * @return the total number of parameters
     */
    public int getTotalNumberOfParameters() {
        return pathParameterNames.size() + queryParameterNamesByExplodability.size();
    }


    /**
     * Extracts query parameters from the URI template along with their explodability.
     *
     * @param uriTemplate
     *         the URI template string
     * @return a list of pairs containing parameter names and their explodability
     */
    private PairList<String, Boolean> extractQueryParametersByExplodability(String uriTemplate) {
        Matcher queryParameterMatcher = QUERY_VARIABLE_PATTERN.matcher(uriTemplate);
        if (queryParameterMatcher.find()) {
            String parameters = queryParameterMatcher.group(1);
            String[] parameterNames = parameters.split(",");
            List<Pair<String, Boolean>> listOfPairs = Arrays.stream(parameterNames)
                    .map(p -> {
                        boolean explodable = p.contains("*");
                        String name = p.replaceAll("\\*", "");
                        return Pair.of(name, explodable);
                    }).toList();
            PairList<String, Boolean> result = PairList.of(listOfPairs);
            boolean hasNoWhiteSpaceInVariableName = result.stream().allMatch(pair -> {
                String parameterName = pair.left();
                String trimmedParameter = parameterName.trim();
                return trimmedParameter.equals(parameterName);
            });
            Assert.isTrue(hasNoWhiteSpaceInVariableName,
                    format("Leading or trailing whitespace in any query parameter is not allowed (also before or " +
                            "after a comma). Template was '%s'", uriTemplate));
            return result;
        }

        return PairList.of();
    }

    /**
     * Extracts path parameters from the URI template.
     *
     * @param uriTemplate
     *         the URI template string
     * @return a list of path parameter names
     */
    private List<String> extractPathParameters(String uriTemplate) {
        Matcher matcher = PATH_VARIABLE_PATTERN.matcher(uriTemplate);
        ArrayList<String> pathVariables = new ArrayList<>();
        while (matcher.find()) {
            String parameter = matcher.group(1);
            pathVariables.add(parameter);
        }
        return pathVariables;
    }

    /**
     * Checks if the URI template contains any parameters.
     *
     * @return {@code true} if the template is parameterized; {@code false} otherwise
     */
    public boolean isTemplated() {
        return getTotalNumberOfParameters() != 0;
    }
}
