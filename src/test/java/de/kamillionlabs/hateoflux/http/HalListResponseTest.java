package de.kamillionlabs.hateoflux.http;

import de.kamillionlabs.hateoflux.dummy.TestDataGenerator;
import de.kamillionlabs.hateoflux.dummy.model.Author;
import de.kamillionlabs.hateoflux.dummy.model.Book;
import de.kamillionlabs.hateoflux.model.hal.HalEmbeddedWrapper;
import de.kamillionlabs.hateoflux.model.hal.HalListWrapper;
import de.kamillionlabs.hateoflux.model.hal.HalResourceWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.util.List;

import static de.kamillionlabs.hateoflux.utility.ValidationMessageTemplates.valueNotAllowedToBeNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Younes El Ouarti
 */
class HalListResponseTest {

    private Author author;
    private List<HalResourceWrapper<Book, Author>> bookWithAuthorWrappers;
    private HalListWrapper<Book, Void> listWrapper;

    @BeforeEach
    void setUp() {
        TestDataGenerator testData = new TestDataGenerator();
        author = testData.getAuthorByName(TestDataGenerator.AuthorName.ERICH_GAMMA);
        List<Book> books = testData.getAllBooksByAuthorName(TestDataGenerator.AuthorName.ERICH_GAMMA);

        List<HalResourceWrapper<Book, Void>> bookWrappers = books.stream()
                .map(HalResourceWrapper::wrap)
                .toList();

        bookWithAuthorWrappers = books.stream()
                .map(book -> HalResourceWrapper.wrap(book)
                        .withEmbeddedResource(HalEmbeddedWrapper.wrap(author)))
                .toList();

        listWrapper = HalListWrapper.wrap(bookWrappers);
    }

    @Test
    void givenStatusOnly_whenCreatingResponse_thenStatusIsSet() {
        OldHalListResponse<Book, Void> response = new OldHalListResponse<>(HttpStatus.OK);

        assertThat(response.getHttpStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHalWrapper()).isNull();
        assertThat(response.getHeaders()).isNull();
    }

    @Test
    void givenWrapper_whenCallingOk_thenCreateOkResponse() {
        OldHalListResponse<Book, Void> response = OldHalListResponse.ok(listWrapper);

        assertThat(response.getHttpStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHalWrapper()).isEqualTo(listWrapper);
        assertThat(response.getHalWrapper().getResourceList()).hasSize(3);
        assertThat(response.getHalWrapper().getResourceList().get(0).getResource().getAuthor())
                .isEqualTo("Erich Gamma");
        assertThat(response.getHeaders()).isNotNull();
    }

    @Test
    void whenCallingNoContent_thenCreateNoContentResponse() {
        OldHalListResponse<Book, Void> response = OldHalListResponse.noContent();

        assertThat(response.getHttpStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getHalWrapper()).isNull();
        assertThat(response.getHeaders()).isNotNull();
    }

    @Test
    void givenExistingResponse_whenAddingLocation_thenCreateNewResponseWithLocation() {
        URI location = URI.create("/authors/erich-gamma/books");
        OldHalListResponse<Book, Void> response = OldHalListResponse.ok(listWrapper)
                .location(location);

        assertThat(response.getHeaders().getFirst(HttpHeaders.LOCATION))
                .isEqualTo(location.toString());
    }

    @Test
    void givenResponse_whenMapping_thenCreateNewResponseWithMappedContent() {
        OldHalListResponse<Book, Void> response = OldHalListResponse.ok(listWrapper);

        OldHalListResponse<Book, Author> mappedResponse = response.map(wrapper ->
                HalListWrapper.wrap(bookWithAuthorWrappers));

        assertThat(mappedResponse.getHttpStatusCode()).isEqualTo(response.getHttpStatusCode());
        assertThat(mappedResponse.getHeaders()).isEqualTo(response.getHeaders());
        assertThat(mappedResponse.getHalWrapper().getResourceList()).hasSize(3);
        assertThat(mappedResponse.getHalWrapper().getResourceList().get(0)
                .getRequiredEmbedded().get(0).getEmbeddedResource().getMainGenre())
                .isEqualTo("Software Architecture");
    }

    @Test
    void givenNullMapper_whenMapping_thenThrowException() {
        OldHalListResponse<Book, Void> response = OldHalListResponse.ok(listWrapper);

        assertThatThrownBy(() -> response.map(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining(valueNotAllowedToBeNull("Mapper function"));
    }
}
