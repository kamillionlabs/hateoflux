package org.kamillion.hateoflux.model.hal;

import org.junit.jupiter.api.Test;
import org.kamillion.hateoflux.dummy.model.Book;
import org.kamillion.hateoflux.dummy.model.EmptyRelationBook;
import org.kamillion.hateoflux.dummy.model.UnannotatedBook;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class HalEntityWrapperTest {


    @Test
    void givenContentIsAnEntity_whenWrapping_thenNoExceptionIsThrown() {
        // WHEN
        HalEntityWrapper.wrap(new Book());
        //THEN no exception is thrown
    }

    @Test
    void givenContentIsAnIterable_whenWrapping_thenExceptionIsThrown() {
        assertThatThrownBy(() -> HalEntityWrapper.wrap(List.of(new Book())))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Content is not allowed to be a collection/iterable. Use HalCollectionWrapper instead");
    }

    @Test
    void givenContentIsNull_whenWrapping_thenExceptionIsThrown() {
        assertThatThrownBy(() -> HalEntityWrapper.wrap(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Content is not allowed to be null");
    }

    @Test
    void givenEmbeddedIsNull_whenWithEmbeddedEntity_thenExceptionIsThrown() {
        //GIVEN
        assertThatThrownBy(() -> HalEntityWrapper.wrap(new Book())
                //WHEN
                .withEmbeddedEntity(null))
                //THEN
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Embedded null is not allowed");
    }

    @Test
    void givenEmbeddedIsNotNull_whenWithEmbeddedEntity_thenEmbeddedIsAccessible() {
        //GIVEN
        var embedded = HalEntityWrapper.wrap(new UnannotatedBook());

        //WHEN
        var entity = HalEntityWrapper.wrap(new Book())
                .withEmbeddedEntity(embedded);

        //THEN
        assertThat(entity.hasEmbedded()).isEqualTo(true);
        assertThat(entity.getRequiredEmbedded()).isEqualTo(embedded);
        assertThat(entity.getRequiredNameOfEmbedded()).isEqualTo("unannotatedBook");
    }

    @Test
    void givenNull_whenWithNonEmptyEmbeddedCollection_thenExceptionIsThrown() {
        //GIVEN
        assertThatThrownBy(() -> HalEntityWrapper.wrap(new Book())
                //WHEN
                .withNonEmptyEmbeddedCollection(null))
                //THEN
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Collection to embed is not allowed to be null");
    }

    @Test
    void givenEmptyCollection_whenWithNonEmptyEmbeddedCollection_thenExceptionIsThrown() {
        //GIVEN
        assertThatThrownBy(() -> HalEntityWrapper.wrap(new Book())
                //WHEN
                .withNonEmptyEmbeddedCollection(new ArrayList<>()))
                //THEN
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Collection to embed is not allowed to be empty");
    }

    @Test
    void givenNonEmptyCollection_whenWithNonEmptyEmbeddedCollection_thenEmbeddedIsAccessible() {
        //GIVEN
        var embedded = List.of(HalEntityWrapper.wrap(new UnannotatedBook()));

        //WHEN
        var entity = HalEntityWrapper.wrap(new Book())
                .withNonEmptyEmbeddedCollection(embedded);

        //THEN
        assertThat(entity.hasEmbedded()).isEqualTo(true);
        assertThat(entity.getRequiredEmbedded()).isEqualTo(embedded);
        assertThat(entity.getRequiredNameOfEmbedded()).isEqualTo("unannotatedBooks");
    }

    @Test
    void givenNonEmptyCollectionWithCustomName_whenWithEmbeddedCollection_thenEmbeddedIsAccessibleViaCustomName() {
        //GIVEN
        var embedded = List.of(HalEntityWrapper.wrap(new UnannotatedBook()));
        String customName = "customName";

        //WHEN
        var entity = HalEntityWrapper.wrap(new Book())
                .withEmbeddedCollection(customName, embedded);

        //THEN
        assertThat(entity.getEmbedded().get()).isEqualTo(embedded);
        assertThat(entity.getNameOfEmbedded().get()).isEqualTo(customName);
    }

    @Test
    void givenEmptyCollectionWithCustomName_whenWithEmbeddedCollection_thenEmbeddedIsAccessibleViaCustomName() {
        //GIVEN
        String customName = "customName";
        var entity = HalEntityWrapper.wrap(new UnannotatedBook())
                //WHEN
                .withEmbeddedCollection(customName, new ArrayList<>());

        //THEN
        assertThat(entity.getEmbedded().get()).isEqualTo(new ArrayList<>());
        assertThat(entity.getNameOfEmbedded().get()).isEqualTo(customName);
    }

    @Test
    void givenEmptyWhiteSpaceAsCustomName_whenWithEmbeddedCollection_thenEmbeddedIsAccessibleViaCustomName() {
        //GIVEN
        var embedded = List.of(HalEntityWrapper.wrap(new UnannotatedBook()));
        assertThatThrownBy(() -> HalEntityWrapper.wrap(new Book())
                //WHEN
                .withEmbeddedCollection(" ", embedded))
                //THEN
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Name for embedded must not be empty or contain only whitespace");
    }

    @Test
    void givenEmptyCollectionWithClass_whenWithEmbeddedCollection_thenEmbeddedIsAccessibleViaCustomName() {
        //GIVEN
        var entity = HalEntityWrapper.wrap(new EmptyRelationBook())
                //WHEN
                .withEmbeddedCollection(Book.class, new ArrayList<>());

        //THEN
        assertThat(entity.getEmbedded().get()).isEqualTo(new ArrayList<>());
        assertThat(entity.getNameOfEmbedded().get()).isEqualTo("customBook");
    }
}