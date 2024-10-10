package de.kamillionlabs.hateoflux.model.hal;

import de.kamillionlabs.hateoflux.dummy.model.Book;
import de.kamillionlabs.hateoflux.dummy.model.EmptyRelationBook;
import de.kamillionlabs.hateoflux.dummy.model.UnannotatedBook;
import org.junit.jupiter.api.Test;

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
                .hasMessage("Entity is not allowed to be of type collection/iterable. Use HalListWrapper instead");
    }

    @Test
    void givenContentIsNull_whenWrapping_thenExceptionIsThrown() {
        assertThatThrownBy(() -> HalEntityWrapper.wrap(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Entity is not allowed to be null");
    }

    @Test
    void givenEmbeddedIsNull_whenWithEmbeddedEntity_thenExceptionIsThrown() {
        //GIVEN
        assertThatThrownBy(() -> HalEntityWrapper.wrap(new Book())
                //WHEN
                .withEmbeddedEntity(null))
                //THEN
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Embedded is not allowed to be null");
    }

    @Test
    void givenEmbeddedIsNotNull_whenWithEmbeddedEntity_thenEmbeddedIsAccessible() {
        //GIVEN
        var embedded = HalEmbeddedWrapper.wrap(new UnannotatedBook());

        //WHEN
        var entity = HalEntityWrapper.wrap(new Book())
                .withEmbeddedEntity(embedded);

        //THEN
        assertThat(entity.hasEmbedded()).isEqualTo(true);
        assertThat(entity.getRequiredEmbedded()).isEqualTo(List.of(embedded));
        assertThat(entity.getRequiredNameOfEmbedded()).isEqualTo("unannotatedBook");
    }

    @Test
    void givenNull_whenWithNonEmptyEmbeddedList_thenExceptionIsThrown() {
        //GIVEN
        assertThatThrownBy(() -> HalEntityWrapper.wrap(new Book())
                //WHEN
                .withNonEmptyEmbeddedList(null))
                //THEN
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("List to embed is not allowed to be null");
    }

    @Test
    void givenEmptyCollection_whenWithNonEmptyEmbeddedList_thenExceptionIsThrown() {
        //GIVEN
        assertThatThrownBy(() -> HalEntityWrapper.wrap(new Book())
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
        var entity = HalEntityWrapper.wrap(new Book())
                .withNonEmptyEmbeddedList(embedded);

        //THEN
        assertThat(entity.hasEmbedded()).isEqualTo(true);
        assertThat(entity.getRequiredEmbedded()).isEqualTo(embedded);
        assertThat(entity.getRequiredNameOfEmbedded()).isEqualTo("unannotatedBooks");
    }

    @Test
    void givenNonEmptyCollectionWithCustomName_whenWithEmbeddedCollection_thenEmbeddedIsAccessibleViaCustomName() {
        //GIVEN
        var embedded = List.of(HalEmbeddedWrapper.wrap(new UnannotatedBook()));
        String customName = "customName";

        //WHEN
        var entity = HalEntityWrapper.wrap(new Book())
                .withEmbeddedList(customName, embedded);

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
                .withEmbeddedList(customName, new ArrayList<>());

        //THEN
        assertThat(entity.getEmbedded().get()).isEqualTo(new ArrayList<>());
        assertThat(entity.getNameOfEmbedded().get()).isEqualTo(customName);
    }

    @Test
    void givenEmptyWhiteSpaceAsCustomName_whenWithEmbeddedCollection_thenEmbeddedIsAccessibleViaCustomName() {
        //GIVEN
        var embedded = List.of(HalEmbeddedWrapper.wrap(new UnannotatedBook()));
        assertThatThrownBy(() -> HalEntityWrapper.wrap(new Book())
                //WHEN
                .withEmbeddedList(" ", embedded))
                //THEN
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Name for embedded is not allowed to be empty");
    }

    @Test
    void givenEmptyCollectionWithClass_whenWithEmbeddedCollection_thenEmbeddedIsAccessibleViaCustomName() {
        //GIVEN
        var entity = HalEntityWrapper.wrap(new EmptyRelationBook())
                //WHEN
                .withEmbeddedList(Book.class, new ArrayList<>());

        //THEN
        assertThat(entity.getEmbedded().get()).isEqualTo(new ArrayList<>());
        assertThat(entity.getNameOfEmbedded().get()).isEqualTo("customBooks");
    }
}