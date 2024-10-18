package de.kamillionlabs.hateoflux.model;

import de.kamillionlabs.hateoflux.model.link.Link;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class LinkTest {


    @ParameterizedTest
    @CsvSource({
            "http://localhost:8080, current/href, http://localhost:8080/current/href",
            "http://example.com, /, http://example.com/",
            "http://localhost:8080, /slash/beginning-and-end/, http://localhost:8080/slash/beginning-and-end/",
    })
    void givenServerHttpRequest_whenPrependBaseUrl_thenBaseUrlPrependedToHref(String baseUrl, String currentLink,
                                                                              String expectedHref) {
        //GIVEN
        Link link = Link.of(currentLink);
        MockServerHttpRequest request = MockServerHttpRequest
                .get(baseUrl)
                .build();

        //WHEN
        Link result = link.prependBaseUrl(request);

        //THEN
        assertThat(result).isNotNull();
        assertThat(result.getHref()).isEqualTo(expectedHref);
    }

    @ParameterizedTest
    @CsvSource({
            "http://localhost:8080, current/href, http://localhost:8080/current/href",
            "http://example.com, /, http://example.com/",
            "http://localhost:8080, /slash/beginning-and-end/, http://localhost:8080/slash/beginning-and-end/",
    })
    void givenServerWebExchange_whenPrependBaseUrl_thenBaseUrlPrependedToHref(String baseUrl, String currentLink,
                                                                              String expectedHref) {
        //GIVEN
        Link link = Link.of(currentLink);
        MockServerHttpRequest request = MockServerHttpRequest
                .get(baseUrl)
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        //WHEN
        Link result = link.prependBaseUrl(exchange);

        //THEN
        assertThat(result).isNotNull();
        assertThat(result.getHref()).isEqualTo(expectedHref);
    }

    @ParameterizedTest
    @CsvSource({
            "http://localhost:8080, current/href, http://localhost:8080/current/href",
            "http://example.com, /, http://example.com/",
            "http://localhost:8080, /slash/beginning-and-end/, http://localhost:8080/slash/beginning-and-end/",
    })
    void givenBaseUrl_whenPrependBaseUrl_thenBaseUrlPrependedToHref(String baseUrl, String currentLink,
                                                                    String expectedHref) {
        //GIVEN
        Link link = Link.of(currentLink);

        //WHEN
        Link result = link.prependBaseUrl(baseUrl);

        //THEN
        assertThat(result).isNotNull();
        assertThat(result.getHref()).isEqualTo(expectedHref);
    }

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

    @ParameterizedTest
    @CsvSource(delimiter = ';', value = {
            "true; http://example.com?keyWords=blue&keyWords=red",
            "false; http://example.com?keyWords=blue,red"
    })
    public void givenMapToExpandWithExplodedParameter_whenExpand_thenPlaceholdersCorrectlySet(
            boolean collectionRenderedAsComposite, String expectedUri) {
        //GIVEN
        Link link = Link.of("http://example.com{?keyWords*}");
        Map<String, Object> parameters = Map.of("keyWords", List.of("blue", "red"));

        //WHEN
        Link actual = link.expand(parameters, collectionRenderedAsComposite);

        //THEN
        assertThat(actual.getHref()).isEqualTo(expectedUri);
    }

}