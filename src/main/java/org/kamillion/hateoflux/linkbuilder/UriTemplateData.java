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
 * @since 23.08.2024
 */

package org.kamillion.hateoflux.linkbuilder;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import org.kamillion.hateoflux.utility.Pair;
import org.kamillion.hateoflux.utility.PairList;
import org.springframework.util.Assert;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;

/**
 * @author Younes El Ouarti
 */

@Data
public class UriTemplateData {

    private static final Pattern PATH_VARIABLE_PATTERN = Pattern.compile("(?<!\\?)\\{([^?}]+)}");

    private static final String QUERY_PARAMETER_PLACEHOLDER_REGEX =
            "\\{\\?([a-zA-Z0-9_-]+\\*?(?:,\\s*[a-zA-Z0-9_-]+\\*?)*)}$";

    private static final Pattern QUERY_VARIABLE_PATTERN = Pattern.compile(QUERY_PARAMETER_PLACEHOLDER_REGEX);

    private String originalUriTemplate = "";

    private String uriTemplateWithoutQueryParameters = "";

    private List<String> pathParameterNames = new ArrayList<>();

    @Getter(AccessLevel.PRIVATE)
    private PairList<String, Boolean> queryParameterNamesByExplodability = new PairList<>();

    public List<String> getQueryParameterNames() {
        return queryParameterNamesByExplodability.getLefts();
    }

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

    public boolean hasExplodedQueryParameters() {
        return queryParameterNamesByExplodability.getRights().stream()
                .anyMatch(p -> p); // is any boolean in the list true?
    }

    public boolean isExplodedQueryParameter(String parameterName) {
        return queryParameterNamesByExplodability.stream()
                .filter(Pair::right) // when right==true --> is explodable
                .anyMatch(p -> p.left().equals(parameterName));
    }

    public boolean hasOnlyQueryParameters() {
        return pathParameterNames.isEmpty() && !queryParameterNamesByExplodability.isEmpty();
    }

    public boolean doesNotIncludeAllPathParameters(Set<String> parameterNamesToTest) {
        return !parameterNamesToTest.containsAll(pathParameterNames);
    }

    public boolean includesUnknownParameters(Set<String> parameterNamesToTest) {
        Set<String> mergedParameters = new HashSet<>();
        mergedParameters.addAll(pathParameterNames);
        mergedParameters.addAll(getQueryParameterNames());

        return parameterNamesToTest.stream().anyMatch(p -> !mergedParameters.contains(p));
    }

    public int getTotalNumberOfParameters() {
        return pathParameterNames.size() + queryParameterNamesByExplodability.size();
    }


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

    private List<String> extractPathParameters(String uriTemplate) {
        Matcher matcher = PATH_VARIABLE_PATTERN.matcher(uriTemplate);
        ArrayList<String> pathVariables = new ArrayList<>();
        while (matcher.find()) {
            String parameter = matcher.group(1);
            pathVariables.add(parameter);
        }
        return pathVariables;
    }


    public boolean isTemplated() {
        return getTotalNumberOfParameters() != 0;
    }
}
