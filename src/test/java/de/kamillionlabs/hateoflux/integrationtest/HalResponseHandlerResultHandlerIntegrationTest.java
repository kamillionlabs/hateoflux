package de.kamillionlabs.hateoflux.integrationtest;

import de.kamillionlabs.hateoflux.http.ReactiveResponseEntityConfig;
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
@Import({ReactiveResponseEntityConfig.class})
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
                .uri("/book/get-book")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.title").isEqualTo("Effective Java")
                .jsonPath("$.author").isEqualTo("Joshua Bloch")
                .jsonPath("$._links.self.href").isEqualTo("/book/effective-java");
    }


    @Test
    void givenBooksCollection_whenCallingGetBooksByAuthor_thenReturnHalListResponse() {
        webTestClient.get()
                .uri("/book/get-list-of-books")
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
    void givenResponseWithHttpHeaderAccepted_whenCallingGetBookAccepted_thenReturnAcceptedStatus() {
        webTestClient.get()
                .uri("/book/get-book-with-header")
                .exchange()
                .expectStatus().isAccepted()
                .expectBody()
                .jsonPath("$.title").isEqualTo("Refactoring: Improving the Design of Existing Code")
                .jsonPath("$._links.self.href").isEqualTo("/book/refactoring");
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

    @Test
    void givenControllerReturnsAPublisherOfReactiveResponseEntity_whenmethodCalled_thenThrowsException() {
        webTestClient.get()
                .uri("/book/wrapped-halresponse")
                .exchange()
                .expectStatus().is5xxServerError();
    }


}