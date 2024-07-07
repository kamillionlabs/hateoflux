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