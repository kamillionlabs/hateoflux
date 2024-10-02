package de.kamillionlabs.hateoflux.linkbuilder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class UriExpanderTest {

    @ParameterizedTest
    @CsvSource(delimiter = ';', value = {
            "true; ?val1=123&val2=456&val2=789&val3=hello",
            "false; ?val1=123&val2=456,789&val3=hello",
    })
    void givenRenderingTypeAndMapWithCollections_whenConstructExpandedQueryParameterUriPart_thenCorrectUri(
            boolean isComposite, String expectedUriPart) {
        //GIVEN
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("val1", "123");
        map.put("val2", List.of(456, 789));
        map.put("val3", Set.of("hello"));

        //WHEN
        String actual = UriExpander.constructExpandedQueryParameterUriPart(map, isComposite);

        //THEN
        assertThat(actual).isEqualTo(expectedUriPart);
    }

    @ParameterizedTest
    @CsvSource(delimiter = ';', value = {
            //Without parameters
            "/users; ; /users",

            //With path parameters at the end
            "/users/{userId}; 37; /users/37",

            //With path parameters in the middle
            "/users/{userId}/posts/; 37; /users/37/posts/",

            //With path parameters at the front
            "{baseUrl}/users/; http://localhost:80; http://localhost:80/users/",

            // With path and query parameters
            "/users/{userId}/activity{?limit}; 3|10; /users/3/activity?limit=10",

            //With multiple query parameters
            "/users/{userId}/activity{?limit,page}; 3|10|2; /users/3/activity?limit=10&page=2",

            //With multiple query parameters but we provide just one
            "/users/3/activity{?limit,page}; 3; /users/3/activity?limit=3",

            //With multiple query parameters but we provide only path parameter
            "/users/{userId}/activity{?limit,page}; 3; /users/3/activity",

            //With multiple query parameters but we provide none
            "/users/3/activity{?limit,page}; ; /users/3/activity",

            //With path parameters that contains spaces
            "/users/names/{name}; this has spaces; /users/names/this%20has%20spaces",

            //With query parameters that contains spaces
            //(apparently query parameters encode it typically with '+' instead of '%20')
            "/users/names{?name}; this has spaces; /users/names?name=this+has+spaces",

            //With path parameter that contains reserved character
            "/users/names/{name}; name_with{_brace; /users/names/name_with%7B_brace",

            //With query parameter that contains reserved character
            "/users/names{?name}; name_with{_brace; /users/names?name=name_with%7B_brace",})
    void givenValidInputs_whenExpandWithVars_thenExpectedUri(String template, String vars, String expected) {
        Object[] variables = convertToArray(vars);
        assertThat(UriExpander.expand(template, variables)).isEqualTo(expected);

    }

    @ParameterizedTest
    @CsvSource(delimiter = ';', value = {
            //Not enough parameters
            "/users/{userId}/posts/{postId}; 15; " +
                    "Not enough mandatory path parameters provided for URI template expansion. " +
                    "Template was '/users/{userId}/posts/{postId}', parameter values were [15]",

            //Path has path parameters but none were provided
            "/users/{userId}/posts/{postId}; ; " +
                    "No parameters provided for URI expansion, but mandatory path parameters were detected. " +
                    "Template was '/users/{userId}/posts/{postId}'",

            //Too many parameters
            "/users/{userId}; 15|1015; " +
                    "Provided more parameters for URI template expansion than expected. " +
                    "Template was '/users/{userId}', parameter values were [15,1015]",

            //URI is not a template but parameters were provided
            "/no/placeholders ; 15; " +
                    "Provided more parameters for URI template expansion than expected. " +
                    "Template was '/no/placeholders', parameter values were [15]",

            //Template has parameters with explode modifier
            "/users{?keyWords*}; 15|1015; " +
                    "Exploded query parameters cannot be expanded using only values. " +
                    "Use expansion method that assigns values to dedicated parameters. " +
                    "Template was '/users{?keyWords*}'",

    })
    void givenInvalidInputs_whenExpandWithVars_thenThrowException(String template, String parameters,
                                                                  String expectedExceptionMessage) {
        Object[] parameteresAsArray = convertToArray(parameters);
        assertThatThrownBy(() -> UriExpander.expand(template, parameteresAsArray)).isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedExceptionMessage);
    }

    private static String[] convertToArray(String parameters) {
        return parameters == null ? new String[]{} : parameters.split("\\|");
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void givenEmptyOrNullPath_whenExpandWithVars_thenThrowException(String template) {
        assertThatThrownBy(() -> UriExpander.expand(template, "value1", "value2"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Provided more parameters for URI template expansion than expected.");
    }


    @ParameterizedTest
    @CsvSource(delimiter = ';', value = {
            //Without parameters
            "/users; ; /users",

            //With path parameters at the end
            "/users/{userId}; userId=37; /users/37",

            //With path parameters in the middle
            "/users/{userId}/posts/; userId=37; /users/37/posts/",

            //With path parameters at the front
            "{baseUrl}/users/; baseUrl=http://localhost:80; http://localhost:80/users/",

            // With path and query parameters
            "/users/{userId}/activity{?limit}; userId=3|limit=10; /users/3/activity?limit=10",

            //With multiple query parameters
            "/users/{userId}/activity{?limit,page}; userId=3|limit=10|page=2; /users/3/activity?limit=10&page=2",

            //With multiple query parameters but we provide just one
            "/users/3/activity{?limit,page}; limit=3; /users/3/activity?limit=3",

            //With multiple query parameters but we provide none
            "/users/3/activity{?limit,page}; ; /users/3/activity",

            //With path parameters that contains spaces
            "/users/names/{name}; name=this has spaces; /users/names/this%20has%20spaces",

            //With query parameters that contains spaces
            //(apparently query parameters encode it typically with '+' instead of '%20')
            "/users/names{?name}; name=this has spaces; /users/names?name=this+has+spaces",

            //With path parameter that contains reserved character
            "/users/names/{name}; name=name_with{_brace; /users/names/name_with%7B_brace",

            //With query parameter that contains reserved character
            "/users/names{?name}; name=name_with{_brace; /users/names?name=name_with%7B_brace",
    })
    void givenValidMapInputs_whenExpandWithMap_thenCorrectUri(String template, String keyValues, String expected) {
        Map<String, Object> map = convertToMap(keyValues);
        assertThat(UriExpander.expand(template, map)).isEqualTo(expected);
    }

    @Test
    void givenExplodedQueryParameter_whenExpandWithMap_thenCorrectUri() {
        //GIVEN
        String template = "/users{?keyWords*,limit}";
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("keyWords", List.of("active", "blue", "positive"));
        map.put("limit", 10);

        //WHEN
        String actual = UriExpander.expand(template, map);

        //THEN
        assertThat(actual).isEqualTo("/users?keyWords=active,blue,positive&limit=10");
    }

    @Test
    void givenNotExplodedQueryParameter_whenExpandWithMap_thenThrowException() {
        //GIVEN
        String template = "/users{?keyWords}";
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("keyWords", List.of("active", "blue", "positive"));
        assertThatThrownBy(() -> UriExpander.expand(template, map)).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Detected a collection as value for a parameter, but parameter was not exploded in " +
                        "template (asterisk after parameter name e.g. {?var*}). " +
                        "Template was '/users{?keyWords}', parameters were {keyWords=[active, blue, positive]}");
    }

    @ParameterizedTest
    @CsvSource(delimiter = ';', value = {
            //Missing path parameter
            "/users/{userId}/posts/{postId}; userId=15; " +
                    "Not enough mandatory path parameters provided for URI template expansion. " +
                    "Template was '/users/{userId}/posts/{postId}', parameters were {userId=15}",

            //Path has path parameters but none were provided
            "/users/{userId}/posts/{postId}; ; " +
                    "No parameters provided for URI expansion, but mandatory path parameters were detected. " +
                    "Template was '/users/{userId}/posts/{postId}'",

            //Added unknown parameter
            "/users/{userId}; userId=15|somethingElse=1015; " +
                    "Unknown parameters provided for URI template expansion. " +
                    "Template was '/users/{userId}', parameters were {userId=15, somethingElse=1015}",

            //URI is not a template but parameter were provided
            "/no/placeholders ; something=15; " +
                    "Unknown parameters provided for URI template expansion. " +
                    "Template was '/no/placeholders', parameters were {something=15}"
    })
    void givenInvalidInputs_whenExpandWithMaps_thenThrowException(String template, String parameters,
                                                                  String expectedExceptionMessage) {
        Map<String, Object> parametersAsArray = convertToMap(parameters);
        assertThatThrownBy(() -> UriExpander.expand(template, parametersAsArray)).isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedExceptionMessage);
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void givenEmptyOrNullPath_whenExpandWithMap_thenThrowException(String template) {
        assertThatThrownBy(() -> UriExpander.expand(template, Map.of("key1", "value1", "key2", "value2")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown parameters provided for URI template expansion.");
    }


    private static Map<String, Object> convertToMap(String keyValues) {
        String[] keyValuesAsArray = convertToArray(keyValues);
        Map<String, Object> map = new LinkedHashMap<>();
        for (String keyValue : keyValuesAsArray) {
            if (keyValue != null && !keyValue.trim().isEmpty()) {
                String[] split = keyValue.split("=");
                map.put(split[0], split[1]);
            }
        }
        return map;
    }
}