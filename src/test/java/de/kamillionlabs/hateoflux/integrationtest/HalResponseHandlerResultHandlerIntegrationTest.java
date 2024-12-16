package de.kamillionlabs.hateoflux.integrationtest;

import de.kamillionlabs.hateoflux.http.HalResponseConfig;
import org.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.skyscreamer.jsonassert.JSONCompareMode.NON_EXTENSIBLE;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = IntegrationTestConfiguration.class
)
@AutoConfigureWebTestClient
@Import({HalResponseConfig.class})
class HalResponseHandlerResultHandlerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void givenNoContent_whenCallingTeapotEndpoint_thenReturnTeapotStatus() {
        webTestClient.get()
                .uri("/book/teapot")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.I_AM_A_TEAPOT)
                .expectBody()
                .isEmpty();
    }

    @Test
    void givenExistingBook_whenCallingGetBookFound_thenReturnBookWithHalResponse() {
        webTestClient.get()
                .uri("/book/get-book-found")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.title").isEqualTo("Effective Java")
                .jsonPath("$.author").isEqualTo("Joshua Bloch")
                .jsonPath("$._links.self.href").isEqualTo("/book/effective-java");
    }

    @Test
    void givenNoBook_whenCallingGetBookNotFound_thenReturnNotFoundStatus() {
        webTestClient.get()
                .uri("/book/get-book-not-found")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .isEmpty();
    }

    @Test
    void givenBookWithAuthor_whenCallingGetBookWithAuthor_thenReturnBookAndEmbeddedAuthor() {
        webTestClient.get()
                .uri("/book/get-book-with-author")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.title").isEqualTo("Effective Java")
                .jsonPath("$._embedded.author.name").isEqualTo("Joshua Bloch")
                .jsonPath("$._links.self.href").isEqualTo("/book/effective-java");
    }

    @Test
    void givenNewBook_whenCallingGetBookCreated_thenReturnCreatedStatusAndLocation() {
        webTestClient.get()
                .uri("/book/get-book-created")
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().valueEquals("Location", "/book/clean-code")
                .expectBody()
                .jsonPath("$.title").isEqualTo("Clean Code")
                .jsonPath("$._links.self.href").isEqualTo("/book/clean-code");
    }

    @Test
    void givenBooksCollection_whenCallingGetBooksByAuthor_thenReturnHalListResponse() {
        webTestClient.get()
                .uri("/book/get-books-by-author")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$._embedded.customBooks.length()").isEqualTo(3)
                .jsonPath("$._embedded.customBooks[0].title")
                .isEqualTo("Design Patterns: Elements of Reusable Object-Oriented Software")
                .jsonPath("$._embedded.customBooks[0]._links.self.href").exists()
                .jsonPath("$._links.self.href").isEqualTo("/books/erich-gamma");
    }

    @Test
    void givenNoBooks_whenCallingGetEmptyBookList_thenReturnNoContent() {
        webTestClient.get()
                .uri("/book/get-empty-book-list")
                .exchange()
                .expectStatus().isNoContent()
                .expectBody()
                .isEmpty();
    }

    @Test
    void givenBooksWithAuthor_whenCallingGetBookListWithAuthors_thenReturnHalListWithEmbeddedAuthors() {
        webTestClient.get()
                .uri("/book/get-book-list-with-authors")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$._embedded.customBooks.length()").isEqualTo(2)
                .jsonPath("$._embedded.customBooks[0]._embedded.author.name").isEqualTo("Brian Goetz")
                .jsonPath("$._embedded.customBooks[0]._links.self.href").exists();
    }

    @Test
    void givenPendingBook_whenCallingGetBookAccepted_thenReturnAcceptedStatus() {
        webTestClient.get()
                .uri("/book/get-book-accepted")
                .exchange()
                .expectStatus().isAccepted()
                .expectBody()
                .jsonPath("$.title").isEqualTo("Refactoring: Improving the Design of Existing Code")
                .jsonPath("$._links.self.href").isEqualTo("/book/refactoring");
    }

    @Test
    void givenInvalidRequest_whenCallingGetBookBadRequest_thenReturnBadRequestStatus() {
        webTestClient.get()
                .uri("/book/get-book-bad-request")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .isEmpty();
    }

    @Test
    void givenUnauthorizedAccess_whenCallingGetBookForbidden_thenReturnForbiddenStatus() {
        webTestClient.get()
                .uri("/book/get-book-forbidden")
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .isEmpty();
    }

    @Test
    void givenUnauthenticatedAccess_whenCallingGetBookUnauthorized_thenReturnUnauthorizedStatus() {
        webTestClient.get()
                .uri("/book/get-book-unauthorized")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .isEmpty();
    }

    @Test
    void givenBookWithMetadata_whenCallingGetBookWithETag_thenReturnBookWithETagAndContentType() {
        webTestClient.get()
                .uri("/book/get-book-with-etag")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("ETag", "\"1234\"")
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.title").isEqualTo("Clean Code")
                .jsonPath("$._links.self.href").isEqualTo("/book/clean-code");
    }

    @Test
    void givenNonReactiveEndpoint_whenCallingGetBookNonReactive_thenReturnBookWithHalResponse() {
        webTestClient.get()
                .uri("/book/get-book-non-reactive")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.title").isEqualTo("Effective Java")
                .jsonPath("$.author").isEqualTo("Joshua Bloch")
                .jsonPath("$.isbn").isEqualTo("978-0134685991")
                .jsonPath("$._links.self.href").isEqualTo("/book/effective-java");
    }

    @Test
    void givenFluxEndpoint_whenCallingGetBooksFlux_thenReturnBooksStream() {
        webTestClient.get()
                .uri("/book/get-books-flux")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(String.class)
                .value(responseBody -> {
                    try {
                        JSONArray jsonArray = new JSONArray(responseBody);
                        // Assert the first JSON object
                        JSONAssert.assertEquals("""  
                                {
                                    "title":"Effective Java",
                                    "author":"Joshua Bloch",
                                    "isbn":"978-0134685991",
                                    "publishedDate":"2018-01-06",
                                    "_links":{"self":{"href":"/book/effective-java"}}
                                }
                                """, jsonArray.get(0).toString(), NON_EXTENSIBLE);
                        // Assert the second JSON object
                        JSONAssert.assertEquals("""
                                {
                                    "title":"Clean Code",
                                    "author":"Robert C. Martin",
                                    "isbn":"978-0132350884",
                                    "publishedDate":"2008-08-01",
                                    "_links":{"self":{"href":"/book/clean-code"}}
                                }
                                """, jsonArray.get(1).toString(), NON_EXTENSIBLE);
                    } catch (Exception e) {
                        throw new AssertionError("JSON comparison failed", e);
                    }
                });
    }
}