/*
 * Copyright (c)  2024 kamillion labs contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.kamillionlabs.hateoflux.model.hal;

import de.kamillionlabs.hateoflux.dummy.model.Book;
import de.kamillionlabs.hateoflux.dummy.model.BookWithItemRelationValue;
import de.kamillionlabs.hateoflux.dummy.model.EmptyRelationBook;
import de.kamillionlabs.hateoflux.dummy.model.UnannotatedBook;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class HalWrapperTest {

    @Test
    void givenAnnotatedClassResourceWithItemRelation_whenDetermineRelationNameForObject_thenAnnotatedResourceName() {
        BookWithItemRelationValue book = new BookWithItemRelationValue();
        AssertionsForClassTypes.assertThat(HalWrapper.determineRelationNameForObject(book))
                .isEqualTo("itemRBook");
    }

    @Test
    void givenAnnotatedClassResource_whenDetermineRelationNameForObject_thenAnnotatedResourceName() {
        Book book = new Book();
        AssertionsForClassTypes.assertThat(HalWrapper.determineRelationNameForObject(book))
                .isEqualTo("customBook");
    }

    @Test
    void givenUnannotatedClassResource_whenDetermineRelationNameForObject_thenDefaultResourceName() {
        UnannotatedBook unannotatedBook = new UnannotatedBook();
        AssertionsForClassTypes.assertThat(HalWrapper.determineRelationNameForObject(unannotatedBook))
                .isEqualTo("unannotatedBook");
    }

    @Test
    void givenEmptyAnnotationClassResource_whenDetermineRelationNameForObject_thenDefaultResourceName() {
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

    @Test
    void givenString_whenWrapping_thenThrowsException() {
        assertThatThrownBy(() -> HalResourceWrapper.wrap("hello"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Resource is not allowed to be of type scalar (e.g. String, int, etc.)");
    }

    @Test
    void givenCharacter_whenWrapping_thenThrowsException() {
        assertThatThrownBy(() -> HalResourceWrapper.wrap('h'))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Resource is not allowed to be of type scalar (e.g. String, int, etc.)");
    }

    @Test
    void givenPrimitiveInt_whenWrapping_thenThrowsException() {
        assertThatThrownBy(() -> HalResourceWrapper.wrap(3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Resource is not allowed to be of type scalar (e.g. String, int, etc.)");
    }

    @Test
    void givenBigDecimal_whenWrapping_thenThrowsException() {
        assertThatThrownBy(() -> HalResourceWrapper.wrap(new BigDecimal("4536433.58")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Resource is not allowed to be of type scalar (e.g. String, int, etc.)");
    }

    @Test
    void givenBoolean_whenWrapping_thenThrowsException() {
        assertThatThrownBy(() -> HalResourceWrapper.wrap(false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Resource is not allowed to be of type scalar (e.g. String, int, etc.)");
    }
}