package de.kamillionlabs.hateoflux.model;

import de.kamillionlabs.hateoflux.model.hal.HalPageInfo;
import de.kamillionlabs.hateoflux.model.link.Link;
import de.kamillionlabs.hateoflux.utility.SortCriteria;
import de.kamillionlabs.hateoflux.utility.SortDirection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class LinkTest {

    @Test
    void givenMiddlePageWithSorting_whenDeriveNavigationLinks_thenHrefValuesIncludeSorting() {
        // GIVEN
        HalPageInfo pageInfo = new HalPageInfo(10, 50L, 5, 2); // Middle page (number = 2)
        Link link = Link.of("http://example.com/resource");
        SortCriteria sortCriteria1 = new SortCriteria("name", SortDirection.ASCENDING);
        SortCriteria sortCriteria2 = new SortCriteria("age", SortDirection.DESCENDING);

        // WHEN
        List<Link> navigationLinks = link.deriveNavigationLinks(pageInfo, sortCriteria1, sortCriteria2);

        // THEN
        Link selfLink = findLinkByRel(navigationLinks, "self");
        Link firstLink = findLinkByRel(navigationLinks, "first");
        Link prevLink = findLinkByRel(navigationLinks, "prev");
        Link nextLink = findLinkByRel(navigationLinks, "next");
        Link lastLink = findLinkByRel(navigationLinks, "last");

        // Expected base URL
        String baseUrl = "http://example.com/resource";

        // Expected sorting parameters
        String sortParam1 = "sort=name,asc";
        String sortParam2 = "sort=age,desc";

        // Assertions
        assertThat(selfLink.getHref())
                .isEqualTo(baseUrl + "?page=2&size=10&" + sortParam1 + "&" + sortParam2);
        assertThat(firstLink.getHref())
                .isEqualTo(baseUrl + "?page=0&size=10&" + sortParam1 + "&" + sortParam2);
        assertThat(prevLink.getHref())
                .isEqualTo(baseUrl + "?page=1&size=10&" + sortParam1 + "&" + sortParam2);
        assertThat(nextLink.getHref())
                .isEqualTo(baseUrl + "?page=3&size=10&" + sortParam1 + "&" + sortParam2);
        assertThat(lastLink.getHref())
                .isEqualTo(baseUrl + "?page=4&size=10&" + sortParam1 + "&" + sortParam2);
    }

    @Test
    void givenMiddlePageWithSorting_whenDeriveNavigationLinksWithList_thenAllLinksAreCreated() {
        // GIVEN
        HalPageInfo pageInfo = new HalPageInfo(10, 50L, 5, 2); // Middle page (number = 2)
        Link link = Link.of("http://example.com/resource");
        SortCriteria sortCriteria1 = new SortCriteria("name", SortDirection.ASCENDING);
        SortCriteria sortCriteria2 = new SortCriteria("age", SortDirection.DESCENDING);

        // WHEN
        List<Link> navigationLinks = link.deriveNavigationLinks(pageInfo, sortCriteria1, sortCriteria2);

        // THEN
        assertThat(findLinkByRel(navigationLinks, "self")).isNotNull();
        assertThat(findLinkByRel(navigationLinks, "self")).isNotNull();
        assertThat(findLinkByRel(navigationLinks, "first")).isNotNull();
        assertThat(findLinkByRel(navigationLinks, "prev")).isNotNull();
        assertThat(findLinkByRel(navigationLinks, "next")).isNotNull();
        assertThat(findLinkByRel(navigationLinks, "last")).isNotNull();
    }


    // Helper method to find a link by its rel
    private Link findLinkByRel(List<Link> links, String rel) {
        return links.stream()
                .filter(l -> rel.equals(l.getLinkRelation().getRelation()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Expected link with rel '" + rel + "' not found"));
    }

    @Test
    void givenUrlWithPagingInfoAlready_whenDeriveNavigationLinks_thenHrefDoNotIncludeDoubleQueryParams() {
        // GIVEN
        HalPageInfo pageInfo = new HalPageInfo(2, 2L, 1, 0); // Single page
        // already contains paging though it is different
        Link link = Link.of("https://example.com/resource?page=1&size=10&sort=age,desc");
        SortCriteria sortCriteria1 = new SortCriteria("name", SortDirection.ASCENDING);

        // WHEN
        List<Link> navigationLinks = link.deriveNavigationLinks(pageInfo, sortCriteria1);

        // THEN
        Link selfLink = findLinkByRel(navigationLinks, "self");

        // Assertions
        assertThat(selfLink.getHref())
                .isEqualTo("https://example.com/resource?page=0&size=2&sort=name,asc");
    }

    @ParameterizedTest
    @CsvSource(delimiter = ';', value = {
            // size, totalElements, totalPages, number, expectedRels
            "10;  0; 0; 0; self",                        // empty page
            "10;  5; 1; 0; self",                        // single page
            "10; 50; 5; 0; self,next,last",              // first page of several
            "10; 50; 5; 4; self,first,prev",             // last page of several
            "10; 50; 5; 2; self,first,prev,next,last"    // middle page of several
    })
    void givenVariousPageInfo_whenDeriveNavigationLinks_thenCorrectLinksAreReturned(
            int size, long totalElements, int totalPages, int number, String expectedRels) {

        // GIVEN
        HalPageInfo pageInfo = new HalPageInfo(size, totalElements, totalPages, number);
        Link link = Link.of("http://example.com/resource");
        String[] expectedRelArray = expectedRels.split(",");

        // WHEN
        List<Link> navigationLinks = link.deriveNavigationLinks(pageInfo);

        // THEN
        assertThat(navigationLinks)
                .extracting(l -> l.getLinkRelation().getRelation())
                .containsExactlyInAnyOrder(expectedRelArray);
    }


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

    @Test
    public void givenMultiValueMap_whenExpand_thenPlaceholdersCorrectlySet() {
        // GIVEN
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("someId", "54");

        // WHEN
        Link link = Link.of("http://example.com{?someId}")
                .expand(queryParams);

        //THEN
        assertThat(link.getHref()).isEqualTo("http://example.com?someId=54");
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