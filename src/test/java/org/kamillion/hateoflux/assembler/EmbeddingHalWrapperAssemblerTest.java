package org.kamillion.hateoflux.assembler;

import org.junit.jupiter.api.Test;
import org.kamillion.hateoflux.dummy.model.Author;
import org.kamillion.hateoflux.dummy.model.Book;
import org.kamillion.hateoflux.model.Pair;
import org.kamillion.hateoflux.model.Pairs;
import org.kamillion.hateoflux.model.hal.HalEmbeddedWrapper;
import org.kamillion.hateoflux.model.hal.HalEntityWrapper;
import org.kamillion.hateoflux.model.hal.HalListWrapper;
import org.kamillion.hateoflux.model.link.IanaRelation;
import org.kamillion.hateoflux.model.link.Link;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EmbeddingHalWrapperAssemblerTest {

    // Implementation for testing purposes -----------------------------------------------------------------------------
    static class AssemblerUnderTest implements EmbeddingHalWrapperAssembler<Book, Author> {

        @Override
        public Link buildSelfLinkForEntityList(List<Pair<Book, Author>> entitiesToWrap, ServerWebExchange exchange) {
            return Link.linkAsSelfOf("entity-list/self/link");
        }

        @Override
        public Link buildSelfLinkForEntity(Book entityToWrap, ServerWebExchange exchange) {
            return Link.linkAsSelfOf("entity/self/link");
        }

        @Override
        public Link buildSelfLinkForEmbedded(Author embedded, ServerWebExchange exchange) {
            return Link.linkAsSelfOf("embedded/self/link");
        }

        @Override
        public List<Link> buildOtherLinksForEmbedded(Author embedded, ServerWebExchange exchange) {
            return List.of(Link.of("embedded/other/")
                    .withRel("other"));
        }
    }
    // -----------------------------------------------------------------------------------------------------------------

    private final AssemblerUnderTest assemblerUnderTest = new AssemblerUnderTest();

    @Test
    public void givenEntityWithEmbedded_whenToEntityWrapper_thenAllFieldsAreFilled() {
        //GIVEN
        Book entity = new Book();
        Author embedded = new Author();

        //WHEN
        HalEntityWrapper<Book, Author> actualWrapper = assemblerUnderTest.toEntityWrapper(
                entity,
                embedded,
                null
        );

        /*
         * THEN
         */
        assertThat(actualWrapper).isNotNull();
        //Entity
        Book actualEntity = actualWrapper.getEntity();
        assertThat(actualEntity).isNotNull();
        assertThat(actualEntity).isEqualTo(entity);
        assertThat(actualWrapper.getLinks()).hasSize(1);
        assertThat(actualWrapper.getRequiredLink(IanaRelation.SELF).getHref()).isEqualTo("entity/self/link");

        //Embedded
        assertThat(actualWrapper.getEmbedded().isPresent()).isTrue();
        assertThat(actualWrapper.getRequiredEmbedded()).hasSize(1);
        HalEmbeddedWrapper<Author> actualEmbedded = actualWrapper.getRequiredEmbedded().get(0);
        assertThat(actualEmbedded.getEmbeddedEntity()).isEqualTo(embedded);
        assertThat(actualEmbedded.getLinks().size()).isEqualTo(2);
        assertThat(actualEmbedded.getRequiredLink(IanaRelation.SELF).getHref()).isEqualTo("embedded/self/link");
        assertThat(actualEmbedded.getRequiredLink("other").getHref()).isEqualTo("embedded/other/");
    }

    @Test
    public void givenEntityWithEmbeddedList_whenToEntityWrapper_thenEmbeddedHas2Entities() {
        //GIVEN
        Book entity = new Book();
        Author embedded = new Author();

        //WHEN
        HalEntityWrapper<Book, Author> actualWrapper = assemblerUnderTest.toEntityWrapper(
                entity,
                List.of(embedded, embedded),
                null
        );

        // THEN
        assertThat(actualWrapper).isNotNull();
        assertThat(actualWrapper.getEmbedded().isPresent()).isTrue();
        assertThat(actualWrapper.getRequiredEmbedded()).hasSize(2);
    }

    @Test
    public void givenEntityWithEmbeddedEmptyListAndStringName_whenToEntityWrapper_thenEmbeddedHasGivenName() {
        //GIVEN
        Book entity = new Book();
        String embeddedListName = "testEmbeddedListName";

        //WHEN
        HalEntityWrapper<Book, Author> actualWrapper = assemblerUnderTest.toEntityWrapper(
                entity,
                embeddedListName,
                List.of(),
                null
        );

        // THEN
        assertThat(actualWrapper).isNotNull();
        assertThat(actualWrapper.getEmbedded().isPresent()).isTrue();
        assertThat(actualWrapper.getRequiredEmbedded()).hasSize(0);
        assertThat(actualWrapper.getRequiredNameOfEmbedded()).isEqualTo(embeddedListName);
    }

    @Test
    public void givenEntityWithEmbeddedEmptyListAndClassAsName_whenToEntityWrapper_thenEmbeddedHasGivenClassName() {
        //GIVEN
        Book entity = new Book();
        Class<?> clazz = Book.class;

        //WHEN
        HalEntityWrapper<Book, Author> actualWrapper = assemblerUnderTest.toEntityWrapper(
                entity,
                clazz,
                List.of(),
                null
        );

        // THEN
        assertThat(actualWrapper).isNotNull();
        assertThat(actualWrapper.getEmbedded().isPresent()).isTrue();
        assertThat(actualWrapper.getRequiredEmbedded()).hasSize(0);
        assertThat(actualWrapper.getRequiredNameOfEmbedded()).isEqualTo("customBooks");
    }

    @Test
    public void givenEntitiesEachWithEmbedded_whenToListWrapper_thenAllFieldsAreFilled() {
        //GIVEN
        Book entity = new Book();
        Author embedded = new Author();

        //WHEN
        HalListWrapper<Book, Author> actualWrapper = assemblerUnderTest.toListWrapper(
                Pairs.of(entity, embedded,
                        entity, embedded),
                null
        );

        /*
         * THEN
         */
        //HalListWrapper
        assertThat(actualWrapper).isNotNull();
        assertThat(actualWrapper.getEntityList()).hasSize(2);
        assertThat(actualWrapper.getLinks()).hasSize(1);
        assertThat(actualWrapper.getRequiredLink(IanaRelation.SELF).getHref()).isEqualTo("entity-list/self/link");

        //Entities
        List<HalEntityWrapper<Book, Author>> actualEntityList = actualWrapper.getEntityList();
        assertThat(actualEntityList).isNotNull();
        assertThat(actualEntityList).hasSize(2);
        assertThat(actualEntityList.get(0).getLinks()).hasSize(1);

        //Embedded
        HalEmbeddedWrapper<Author> actualEmbedded = actualEntityList.get(0).getRequiredEmbedded().get(0);
        assertThat(actualEmbedded).isNotNull();
        assertThat(actualEmbedded.getLinks()).hasSize(2);
    }

    @Test
    public void givenEmptyListWrapperWithStringName_whenToEmptyListWrapper_thenEmptyListWithSelfLink() {
        //GIVEN
        String nameOfList = "nameOfList";

        //WHEN
        HalListWrapper<Book, Author> actualWrapper = assemblerUnderTest.toEmptyListWrapper(nameOfList, null);

        //THEN
        assertThat(actualWrapper.getEntityList()).isEmpty();
        assertThat(actualWrapper.getNameOfEntityList()).isEqualTo(nameOfList);
        assertThat(actualWrapper.getLinks()).hasSize(1);
        assertThat(actualWrapper.getRequiredLink(IanaRelation.SELF)).isEqualTo(Link.linkAsSelfOf("entity-list/self" +
                "/link"));
    }

    @Test
    public void givenEmptyListWrapperWithClass_whenToEmptyListWrapper_thenNameOfListIsTakenFromClass() {
        //GIVEN
        Class<?> clazz = Book.class;

        //WHEN
        HalListWrapper<Book, Author> actualWrapper = assemblerUnderTest.toEmptyListWrapper(clazz, null);

        //THEN
        assertThat(actualWrapper.getNameOfEntityList()).isEqualTo("customBooks");
    }


}