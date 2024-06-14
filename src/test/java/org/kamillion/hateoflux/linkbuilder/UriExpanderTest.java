package org.kamillion.hateoflux.linkbuilder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class UriExpanderTest {

    @ParameterizedTest
    @CsvSource({
            "/users/{userId}, 37, /users/37",
            "/users/{userId}/posts/{postId}, 37|2605, /users/37/posts/2605"
    })
    void givenValidInputs_whenExpandWithVars_thenExpectedUri(String template, String vars, String expected) {
        Object[] variables = vars == null ? new Object[]{} : vars.split("\\|");
        assertThat(UriExpander.expand(template, variables)).isEqualTo(expected);
    }


    @Test
    void givenTooFewArguments_whenExpandWithVars_thenThrowException() {
        assertThatThrownBy(() -> UriExpander.expand("/users/{userId}/posts/{postId}", 15))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Not enough arguments provided to expand the URI template. Template was: " +
                        "'/users/{userId}/posts/{postId}', path variables were: [15]");
    }

    @Test
    void givenTooManyArguments_whenExpandWithVars_thenThrowException() {
        assertThatThrownBy(() -> UriExpander.expand("/users/{userId}", 15, 1015))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Too many arguments provided for the URI template. Template was: '/users/{userId}', path " +
                        "variables were: [15, 1015]");
    }

    @Test
    void givenEmptyTemplate_whenExpandWithVars_thenThrowException() {
        assertThatThrownBy(() -> UriExpander.expand("", 15))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Provided string is not a template. Was ''");
    }

    @Test
    void givenNonTemplate_whenExpandWithVars_thenThrowException() {
        assertThatThrownBy(() -> UriExpander.expand("/no/placeholders", 15))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Provided string is not a template. Was '/no/placeholders'");
    }

    @Test
    void givenValidMapInputs_whenExpandWithMap_thenCorrectUri() {
        Map<String, Object> map = Map.of("userId", 15, "postId", 1015);
        assertThat(UriExpander.expand("/users/{userId}/posts/{postId}", map)).isEqualTo("/users/15/posts/1015");
    }

    @Test
    void givenMissingKeysInMap_whenExpandWithMap_thenThrowException() {
        assertThatThrownBy(() -> UriExpander.expand("/users/{userId}/posts/{postId}", Map.of("userId", 15)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Expanding URL failed; No matching variable found for 'postId' in provided keys.");
    }

    @Test
    void givenExtraKeysInMap_whenExpandWithMap_thenThrowException() {
        assertThatThrownBy(() -> UriExpander.expand("/users/{userId}", Map.of("userId", 15, "postId", 1015)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Expanding URL '/users/{userId}' ended without using all provided keys. The following " +
                        "stayed unused: postId");
    }
}