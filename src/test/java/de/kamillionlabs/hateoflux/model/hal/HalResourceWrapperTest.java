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

import de.kamillionlabs.hateoflux.dummy.model.Author;
import de.kamillionlabs.hateoflux.dummy.model.Book;
import de.kamillionlabs.hateoflux.dummy.model.EmptyRelationBook;
import de.kamillionlabs.hateoflux.dummy.model.UnannotatedBook;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class HalResourceWrapperTest {


    @Test
    void givenContentIsAnResource_whenWrapping_thenNoExceptionIsThrown() {
        // WHEN
        HalResourceWrapper.wrap(new Book());
        //THEN no exception is thrown
    }

    @Test
    void givenContentIsAnIterable_whenWrapping_thenExceptionIsThrown() {
        assertThatThrownBy(() -> HalResourceWrapper.wrap(List.of(new Book())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Resource is not allowed to be of type collection/iterable. Use HalListWrapper instead");
    }

    @Test
    void givenContentIsNull_whenWrapping_thenExceptionIsThrown() {
        assertThatThrownBy(() -> HalResourceWrapper.wrap(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Resource is not allowed to be null");
    }

    @Test
    void givenEmbeddedIsNotNull_whenWithEmbeddedResource_thenEmbeddedIsAccessible() {
        //GIVEN
        var embedded = HalEmbeddedWrapper.wrap(new UnannotatedBook());

        //WHEN
        var resource = HalResourceWrapper.wrap(new Book())
                .withEmbeddedResource(embedded);

        //THEN
        assertThat(resource.hasEmbedded()).isEqualTo(true);
        assertThat(resource.getRequiredEmbedded()).isEqualTo(List.of(embedded));
        assertThat(resource.getRequiredNameOfEmbedded()).isEqualTo("unannotatedBook");
    }

    @Test
    void givenNull_whenWithNonEmptyEmbeddedList_thenExceptionIsThrown() {
        //GIVEN
        assertThatThrownBy(() -> HalResourceWrapper.wrap(new Book())
                //WHEN
                .withNonEmptyEmbeddedList(null))
                //THEN
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("List to embed is not allowed to be null");
    }

    @Test
    void givenEmptyCollection_whenWithNonEmptyEmbeddedList_thenExceptionIsThrown() {
        //GIVEN
        assertThatThrownBy(() -> HalResourceWrapper.wrap(new Book())
                //WHEN
                .withNonEmptyEmbeddedList(new ArrayList<>()))
                //THEN
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("List to embed is not allowed to be empty");
    }

    @Test
    void givenNonEmptyCollection_whenWithNonEmptyEmbeddedCollection_thenEmbeddedIsAccessible() {
        //GIVEN
        var embedded = List.of(HalEmbeddedWrapper.wrap(new UnannotatedBook()));

        //WHEN
        var resource = HalResourceWrapper.wrap(new Book())
                .withNonEmptyEmbeddedList(embedded);

        //THEN
        assertThat(resource.hasEmbedded()).isEqualTo(true);
        assertThat(resource.getRequiredEmbedded()).isEqualTo(embedded);
        assertThat(resource.getRequiredNameOfEmbedded()).isEqualTo("unannotatedBooks");
    }

    @Test
    void givenNonEmptyCollectionWithCustomName_whenWithEmbeddedCollection_thenEmbeddedIsAccessibleViaCustomName() {
        //GIVEN
        var embedded = List.of(HalEmbeddedWrapper.wrap(new UnannotatedBook()));
        String customName = "customName";

        //WHEN
        var resource = HalResourceWrapper.wrap(new Book())
                .withEmbeddedList(customName, embedded);

        //THEN
        assertThat(resource.getEmbedded().get()).isEqualTo(embedded);
        assertThat(resource.getNameOfEmbedded().get()).isEqualTo(customName);
    }

    @Test
    void givenEmptyCollectionWithCustomName_whenWithEmbeddedCollection_thenEmbeddedIsAccessibleViaCustomName() {
        //GIVEN
        String customName = "customName";
        var resource = HalResourceWrapper.wrap(new UnannotatedBook())
                //WHEN
                .withEmbeddedList(customName, new ArrayList<>());

        //THEN
        assertThat(resource.getEmbedded().get()).isEqualTo(new ArrayList<>());
        assertThat(resource.getNameOfEmbedded().get()).isEqualTo(customName);
    }

    @Test
    void givenEmptyWhiteSpaceAsCustomName_whenWithEmbeddedCollection_thenEmbeddedIsAccessibleViaCustomName() {
        //GIVEN
        var embedded = List.of(HalEmbeddedWrapper.wrap(new UnannotatedBook()));
        assertThatThrownBy(() -> HalResourceWrapper.wrap(new Book())
                //WHEN
                .withEmbeddedList(" ", embedded))
                //THEN
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Name for embedded is not allowed to be empty");
    }

    @Test
    void givenEmptyCollectionWithClass_whenWithEmbeddedCollection_thenEmbeddedIsAccessibleViaCustomName() {
        //GIVEN
        var resource = HalResourceWrapper.wrap(new EmptyRelationBook())
                //WHEN
                .withEmbeddedList(Book.class, new ArrayList<>());

        //THEN
        assertThat(resource.getEmbedded().get()).isEqualTo(new ArrayList<>());
        assertThat(resource.getNameOfEmbedded().get()).isEqualTo("customBooks");
    }

    @Test
    void givenNullAsEmbedded_whenWrapping_thenNoExceptionIsThrown() {
        // WHEN
        HalResourceWrapper<Book, Void> wrapper = HalResourceWrapper.wrap(new Book());
        //THEN no exception is thrown
        HalResourceWrapper<Book, Author> wrapperWithEmbedded = wrapper.withEmbeddedResource(null);
    }
}