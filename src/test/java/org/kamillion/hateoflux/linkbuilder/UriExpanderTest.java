package org.kamillion.hateoflux.linkbuilder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class UriExpanderTest {


    //TODO Testcase where characters should be escaped  //what is the difference between + escaping and percent
    // escaping???

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

            //With path parameters that contains reserved character
            "/users/names/{name}; this has spaces; /users/names/this%20has%20spaces",

//            //With query parameters that contains reserved character                TODO this is pluses
//            "/users/names{?name}; this has spaces; /users/names?name=this%20has%20spaces",
    })
    void givenValidInputs_whenExpandWithVars_thenExpectedUri(String template, String vars, String expected) {
        Object[] variables = vars == null ? new Object[]{} : vars.split("\\|");
        assertThat(UriExpander.expand(template, variables)).isEqualTo(expected);
    }


    @Test
    void givenTooFewArguments_whenExpandWithVars_thenThrowException() {
        assertThatThrownBy(() -> UriExpander.expand("/users/{userId}/posts/{postId}", 15))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Not enough mandatory path parameters provided for URI template expansion. " +
                        "Template was '/users/{userId}/posts/{postId}', parameter values were [15]");
    }

    @Test
    void givenTooManyArguments_whenExpandWithVars_thenThrowException() {
        assertThatThrownBy(() -> UriExpander.expand("/users/{userId}", 15, 1015))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Provided more parameters for URI template expansion than expected. Template was " +
                        "'/users/{userId}', parameter values were [15,1015]");
    }

    @Test
    void givenEmptyTemplate_whenExpandWithVars_thenThrowException() {
        assertThatThrownBy(() -> UriExpander.expand("", 15))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Provided more parameters for URI template expansion than expected. " +
                        "Template was '', parameter values were [15]");
    }

    @Test
    void givenNonTemplate_whenExpandWithVars_thenThrowException() {
        assertThatThrownBy(() -> UriExpander.expand("/no/placeholders", 15))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Provided more parameters for URI template expansion than expected. " +
                        "Template was '/no/placeholders', parameter values were [15]");
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
            "/users/3/activity{?limit,page}; ; /users/3/activity"
    })
    void givenValidMapInputs_whenExpandWithMap_thenCorrectUri(String template, String keyValues, String expected) {
        String[] keyValuesAsArray = keyValues == null ? new String[]{} : keyValues.split("\\|");
        Map<String, Object> map = new HashMap<>();
        for (String keyValue : keyValuesAsArray) {
            if (keyValue != null && !keyValue.trim().isEmpty()) {
                String[] split = keyValue.split("=");
                map.put(split[0], split[1]);
            }
        }
        assertThat(UriExpander.expand(template, map)).isEqualTo(expected);
    }

    @Test
    void givenMissingKeysInMap_whenExpandWithMap_thenThrowException() {
        assertThatThrownBy(() -> UriExpander.expand("/users/{userId}/posts/{postId}", Map.of("userId", 15)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Not enough mandatory path parameters provided for URI template expansion. Template was " +
                        "'/users/{userId}/posts/{postId}', parameters were [userId]");
    }

    @Test
    void givenExtraKeysInMap_whenExpandWithMap_thenThrowException() {
        assertThatThrownBy(() -> UriExpander.expand("/users/{userId}", Map.of("userId", 15, "postId", 1015)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unknown parameters provided for URI template expansion. " +
                        "Template was '/users/{userId}', parameters were [postId,userId]");
    }
}