package org.kamillion.hateoflux.model.hal;

import org.junit.jupiter.api.Test;
import org.kamillion.hateoflux.dummy.model.Book;
import org.kamillion.hateoflux.dummy.model.EmptyRelationBook;
import org.kamillion.hateoflux.dummy.model.UnannotatedBook;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.kamillion.hateoflux.model.hal.HalWrapper.determineRelationNameForObject;

class HalWrapperTest {

    @Test
    void givenAnnotatedClassEntity_whenDetermineRelationNameForObject_thenAnnotatedEntityName() {
        Book book = new Book();
        assertThat(determineRelationNameForObject(book))
                .isEqualTo("customBook");
    }

    @Test
    void givenUnannotatedClassEntity_whenDetermineRelationNameForObject_thenDefaultEntityName() {
        UnannotatedBook unannotatedBook = new UnannotatedBook();
        assertThat(determineRelationNameForObject(unannotatedBook))
                .isEqualTo("unannotatedBook");
    }

    @Test
    void givenEmptyAnnotationClassEntity_whenDetermineRelationNameForObject_thenDefaultEntityName() {
        EmptyRelationBook emptyRelationBook = new EmptyRelationBook();
        assertThat(determineRelationNameForObject(emptyRelationBook))
                .isEqualTo("emptyRelationBook");
    }

    @Test
    void givenAnnotatedClassCollection_whenDetermineRelationNameForObject_thenAnnotatedCollectionName() {
        List<Book> books = List.of(new Book());
        assertThat(determineRelationNameForObject(books))
                .isEqualTo("customBooks");
    }

    @Test
    void givenUnannotatedClassCollection_whenDetermineRelationNameForObject_thenDefaultCollectionName() {
        Vector<UnannotatedBook> unannotatedBooks = new Vector<>();
        unannotatedBooks.add(new UnannotatedBook());
        assertThat(determineRelationNameForObject(unannotatedBooks))
                .isEqualTo("unannotatedBooks");
    }

    @Test
    void givenEmptyAnnotationClassCollection_whenDetermineRelationNameForObject_thenDefaultCollectionName() {
        Set<EmptyRelationBook> books = Set.of(new EmptyRelationBook());
        assertThat(determineRelationNameForObject(books))
                .isEqualTo("emptyRelationBooks");
    }

    @Test
    void givenObjectIsNull_whenDetermineRelationNameForObject_thenThrowsException() {
        assertThatThrownBy(() -> determineRelationNameForObject(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Object is not allowed to be when determining relation names");
    }

    @Test
    void givenEmptyCollection_whenDetermineRelationNameForObject_thenThrowsException() {
        assertThatThrownBy(() -> determineRelationNameForObject(new ArrayList<String>()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Iterable cannot be empty when determining relation names");
    }
}