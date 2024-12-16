package de.kamillionlabs.hateoflux.http;

import de.kamillionlabs.hateoflux.dummy.TestDataGenerator;
import de.kamillionlabs.hateoflux.dummy.model.Author;
import de.kamillionlabs.hateoflux.dummy.model.Book;
import de.kamillionlabs.hateoflux.model.hal.HalEmbeddedWrapper;
import de.kamillionlabs.hateoflux.model.hal.HalResourceWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;

import static de.kamillionlabs.hateoflux.utility.ValidationMessageTemplates.valueNotAllowedToBeNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class HalResourceResponseTest {

    private Author author;
    private HalResourceWrapper<Book, Void> bookWrapper;

    @BeforeEach
    void setUp() {
        TestDataGenerator testData = new TestDataGenerator();
        Book book = testData.getBookByTitle(TestDataGenerator.BookTitle.EFFECTIVE_JAVA);
        author = testData.getAuthorByName(TestDataGenerator.AuthorName.JOSHUA_BLOCH);
        bookWrapper = HalResourceWrapper.wrap(book);
    }

    @Test
    void givenStatusOnly_whenCreatingResponse_thenStatusIsSet() {
        OldHalResourceResponse<Book, Void> response = new OldHalResourceResponse<>(HttpStatus.OK);

        assertThat(response.getHttpStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHalWrapper()).isNull();
        assertThat(response.getHeaders()).isNull();
    }

    @Test
    void givenHeadersAndStatus_whenCreatingResponse_thenBothAreSet() {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.add("Test-Header", "test-value");

        OldHalResourceResponse<Book, Void> response = new OldHalResourceResponse<>(headers, HttpStatus.OK);

        assertThat(response.getHttpStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders()).isEqualTo(headers);
        assertThat(response.getHalWrapper()).isNull();
    }

    @Test
    void givenWrapperAndStatus_whenCreatingResponse_thenBothAreSet() {
        OldHalResourceResponse<Book, Void> response = new OldHalResourceResponse<>(bookWrapper, HttpStatus.OK);

        assertThat(response.getHttpStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHalWrapper()).isEqualTo(bookWrapper);
        assertThat(response.getHalWrapper().getResource().getTitle()).isEqualTo("Effective Java");
        assertThat(response.getHeaders()).isNull();
    }

    @Test
    void givenNullStatus_whenCreatingResponse_thenThrowException() {
        assertThatThrownBy(() -> new OldHalResourceResponse<Book, Void>(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(valueNotAllowedToBeNull("HttpStatusCode"));
    }

    @Test
    void givenWrapper_whenCallingOk_thenCreateOkResponse() {
        OldHalResourceResponse<Book, Void> response = OldHalResourceResponse.ok(bookWrapper);

        assertThat(response.getHttpStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHalWrapper()).isEqualTo(bookWrapper);
        assertThat(response.getHalWrapper().getResource().getAuthor()).isEqualTo("Joshua Bloch");
        assertThat(response.getHeaders()).isNotNull();
    }

    @Test
    void givenWrapper_whenCallingCreated_thenCreateCreatedResponse() {
        OldHalResourceResponse<Book, Void> response = OldHalResourceResponse.created(bookWrapper);

        assertThat(response.getHttpStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHalWrapper()).isEqualTo(bookWrapper);
        assertThat(response.getHalWrapper().getResource().getIsbn()).isEqualTo("978-0134685991");
        assertThat(response.getHeaders()).isNotNull();
    }

    @Test
    void whenCallingNotFound_thenCreateNotFoundResponse() {
        OldHalResourceResponse<Book, Void> response = OldHalResourceResponse.notFound();

        assertThat(response.getHttpStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getHalWrapper()).isNull();
        assertThat(response.getHeaders()).isNotNull();
    }

    @Test
    void givenExistingResponse_whenAddingLocation_thenCreateNewResponseWithLocation() {
        URI location = URI.create("/books/effective-java");
        OldHalResourceResponse<Book, Void> response = OldHalResourceResponse.created(bookWrapper)
                .location(location);

        assertThat(response.getHeaders().getFirst(HttpHeaders.LOCATION))
                .isEqualTo(location.toString());
    }

    @Test
    void givenResponse_whenMapping_thenCreateNewResponseWithMappedContent() {
        OldHalResourceResponse<Book, Void> response = OldHalResourceResponse.ok(bookWrapper);

        OldHalResourceResponse<Book, Author> mappedResponse = response.map(wrapper ->
                wrapper.withEmbeddedResource(HalEmbeddedWrapper.wrap(author)));

        assertThat(mappedResponse.getHttpStatusCode()).isEqualTo(response.getHttpStatusCode());
        assertThat(mappedResponse.getHeaders()).isEqualTo(response.getHeaders());
        assertThat(mappedResponse.getHalWrapper().getEmbedded()).isPresent();
        assertThat(mappedResponse.getHalWrapper().getRequiredEmbedded().get(0).getEmbeddedResource().getName())
                .isEqualTo("Joshua Bloch");
    }
}