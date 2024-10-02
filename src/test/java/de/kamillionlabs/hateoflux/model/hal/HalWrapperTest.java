package de.kamillionlabs.hateoflux.model.hal;

import de.kamillionlabs.hateoflux.dummy.model.Book;
import de.kamillionlabs.hateoflux.dummy.model.EmptyRelationBook;
import de.kamillionlabs.hateoflux.dummy.model.UnannotatedBook;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class HalWrapperTest {

    @Test
    void givenAnnotatedClassEntity_whenDetermineRelationNameForObject_thenAnnotatedEntityName() {
        Book book = new Book();
        AssertionsForClassTypes.assertThat(HalWrapper.determineRelationNameForObject(book))
                .isEqualTo("customBook");
    }

    @Test
    void givenUnannotatedClassEntity_whenDetermineRelationNameForObject_thenDefaultEntityName() {
        UnannotatedBook unannotatedBook = new UnannotatedBook();
        AssertionsForClassTypes.assertThat(HalWrapper.determineRelationNameForObject(unannotatedBook))
                .isEqualTo("unannotatedBook");
    }

    @Test
    void givenEmptyAnnotationClassEntity_whenDetermineRelationNameForObject_thenDefaultEntityName() {
        EmptyRelationBook emptyRelationBook = new EmptyRelationBook();
        AssertionsForClassTypes.assertThat(HalWrapper.determineRelationNameForObject(emptyRelationBook))
                .isEqualTo("emptyRelationBook");
    }

    @Test
    void givenAnnotatedClassCollection_whenDetermineRelationNameForObject_thenAnnotatedCollectionName() {
        List<Book> books = List.of(new Book());
        AssertionsForClassTypes.assertThat(HalWrapper.determineRelationNameForObject(books))
                .isEqualTo("customBooks");
    }

    @Test
    void givenUnannotatedClassCollection_whenDetermineRelationNameForObject_thenDefaultCollectionName() {
        Vector<UnannotatedBook> unannotatedBooks = new Vector<>();
        unannotatedBooks.add(new UnannotatedBook());
        AssertionsForClassTypes.assertThat(HalWrapper.determineRelationNameForObject(unannotatedBooks))
                .isEqualTo("unannotatedBooks");
    }

    @Test
    void givenEmptyAnnotationClassCollection_whenDetermineRelationNameForObject_thenDefaultCollectionName() {
        Set<EmptyRelationBook> books = Set.of(new EmptyRelationBook());
        AssertionsForClassTypes.assertThat(HalWrapper.determineRelationNameForObject(books))
                .isEqualTo("emptyRelationBooks");
    }

    @Test
    void givenObjectIsNull_whenDetermineRelationNameForObject_thenThrowsException() {
        assertThatThrownBy(() -> HalWrapper.determineRelationNameForObject(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Object is not allowed to be null when determining relation names");
    }

    @Test
    void givenEmptyCollection_whenDetermineRelationNameForObject_thenThrowsException() {
        assertThatThrownBy(() -> HalWrapper.determineRelationNameForObject(new ArrayList<String>()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Iterable cannot be empty when determining relation names");
    }
}