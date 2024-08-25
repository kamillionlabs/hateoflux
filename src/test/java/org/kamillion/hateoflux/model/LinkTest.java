package org.kamillion.hateoflux.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.kamillion.hateoflux.model.link.Link;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class LinkTest {

    @ParameterizedTest
    @NullAndEmptySource
    public void givenEmptyOrNullPath_whenIsTemplated_thenCorrectlyEvaluateFalse(String uriPath) {
        Link link = Link.of(uriPath);
        assertThat(link.isTemplated()).isEqualTo(false);
    }

    @ParameterizedTest
    @CsvSource({ //
            "https://example.com, false", //
            "some-path/{var}, true", //
            "{var}/some-path/, true", //
            "{var1}/some-path/{var2}, true", //
            "{var1}/some-path/{?var2}, true", //
    })
    public void givenUriPath_whenIsTemplated_thenCorrectlyEvaluate(String uriPath, boolean expected) {
        Link link = Link.of(uriPath);
        assertThat(link.isTemplated()).isEqualTo(expected);
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void givenEmptyOrNullUriPart_whenSlash_thenHrefUnchanged(String uriPart) {
        Link link = Link.of("http://example.com");
        Link actual = link.slash(uriPart);
        assertThat(actual.getHref()).isEqualTo("http://example.com/");
    }

    @ParameterizedTest
    @CsvSource({ //
            "http://example.com, path/to/resource", //
            "http://example.com, /path/to/resource", //
            "http://example.com/, path/to/resource", //
            "http://example.com/, /path/to/resource" //
    })
    public void givenOptionalSlashes_whenSlash_thenHrefAppendedCorrectly(String basePath, String uriPart) {
        Link link = Link.of(basePath);
        Link actual = link.slash(uriPart);
        assertThat(actual.getHref()).isEqualTo("http://example.com/path/to/resource");
    }

    @Test
    public void givenVariablesToExpand_whenExpand_thenPlaceholdersCorrectlySet() {
        Link link = Link.of("http://example.com/{someId}");
        Link actual = link.expand(54);
        assertThat(actual.getHref()).isEqualTo("http://example.com/54");
    }

    @Test
    public void givenMapToExpand_whenExpand_thenPlaceholdersCorrectlySet() {
        Link link = Link.of("http://example.com/{someId}");
        Link actual = link.expand(Map.of("someId", 54));
        assertThat(actual.getHref()).isEqualTo("http://example.com/54");
    }

}