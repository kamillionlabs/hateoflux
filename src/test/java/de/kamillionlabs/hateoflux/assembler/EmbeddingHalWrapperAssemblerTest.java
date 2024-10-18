package de.kamillionlabs.hateoflux.assembler;

import de.kamillionlabs.hateoflux.dummy.model.Author;
import de.kamillionlabs.hateoflux.dummy.model.Book;
import de.kamillionlabs.hateoflux.model.hal.HalEmbeddedWrapper;
import de.kamillionlabs.hateoflux.model.hal.HalEntityWrapper;
import de.kamillionlabs.hateoflux.model.hal.HalListWrapper;
import de.kamillionlabs.hateoflux.model.hal.HalPageInfo;
import de.kamillionlabs.hateoflux.model.link.IanaRelation;
import de.kamillionlabs.hateoflux.model.link.Link;
import de.kamillionlabs.hateoflux.utility.PairList;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EmbeddingHalWrapperAssemblerTest {

    // Implementation for testing purposes -----------------------------------------------------------------------------
    static class AssemblerUnderTest implements EmbeddingHalWrapperAssembler<Book, Author> {

        @Override
        public Link buildSelfLinkForEntityList(ServerWebExchange exchange) {
            return Link.of("entity-list/self/link");
        }

        @Override
        public Link buildSelfLinkForEntity(Book entityToWrap, ServerWebExchange exchange) {
            return Link.of("entity/self/link");
        }

        @Override
        public Link buildSelfLinkForEmbedded(Author embedded, ServerWebExchange exchange) {
            return Link.of("embedded/self/link");
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
    public void givenEntityWithEmbedded_whenWrapInEntityWrapper_thenAllFieldsAreFilled() {
        //GIVEN
        Book entity = new Book();
        Author embedded = new Author();

        //WHEN
        HalEntityWrapper<Book, Author> actualWrapper = assemblerUnderTest.wrapInEntityWrapper(
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
    public void givenEntityWithEmbeddedList_whenWrapInEntityWrapper_thenEmbeddedHas2Entities() {
        //GIVEN
        Book entity = new Book();
        Author embedded = new Author();

        //WHEN
        HalEntityWrapper<Book, Author> actualWrapper = assemblerUnderTest.wrapInEntityWrapper(
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
    public void givenEntityWithEmbeddedEmptyListAndStringName_whenWrapInEntityWrapper_thenEmbeddedHasGivenName() {
        //GIVEN
        Book entity = new Book();
        String embeddedListName = "testEmbeddedListName";

        //WHEN
        HalEntityWrapper<Book, Author> actualWrapper = assemblerUnderTest.wrapInEntityWrapper(
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
    public void givenEntityWithEmbeddedEmptyListAndClassAsName_whenWrapInEntityWrapper_thenEmbeddedHasGivenClassName() {
        //GIVEN
        Book entity = new Book();
        Class<?> clazz = Book.class;

        //WHEN
        HalEntityWrapper<Book, Author> actualWrapper = assemblerUnderTest.wrapInEntityWrapper(
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
    public void givenEntitiesEachWithEmbedded_whenWrapInListWrapper_thenAllFieldsAreFilled() {
        //GIVEN
        Book entity = new Book();
        Author embedded = new Author();

        //WHEN
        HalListWrapper<Book, Author> actualWrapper = assemblerUnderTest.wrapInListWrapper(
                PairList.of(entity, embedded,
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
        assertThat(actualEntityList.get(0).getRequiredLink(IanaRelation.SELF).getHref()).isEqualTo("entity/self/link");
        assertThat(actualEntityList.get(1).getLinks()).hasSize(1);
        assertThat(actualEntityList.get(1).getRequiredLink(IanaRelation.SELF).getHref()).isEqualTo("entity/self/link");

        //Embedded
        HalEmbeddedWrapper<Author> actualEmbedded = actualEntityList.get(0).getRequiredEmbedded().get(0);
        assertThat(actualEmbedded).isNotNull();
        assertThat(actualEmbedded.getLinks()).hasSize(2);
    }

    @Test
    public void givenEntitiesAndADataForPageInfo_wrapInListWrapper_thenAllFieldsAreFilled() {
        //GIVEN
        Book entity = new Book();
        Author embedded = new Author();

        //WHEN
        HalListWrapper<Book, Author> actualWrapper = assemblerUnderTest.wrapInListWrapper(
                PairList.of(entity, embedded,
                        entity, embedded),
                100L,
                2,
                null,
                null
        );

        /*
         * THEN
         */
        //Paged Info
        HalPageInfo page = actualWrapper.getPage();
        assertThat(page).isNotNull();
        assertThat(page.totalPages()).isEqualTo(50);
        assertThat(page.totalElements()).isEqualTo(100);
        assertThat(page.size()).isEqualTo(2);
        assertThat(page.number()).isEqualTo(0);

        //Rudimentary testing (rest is tested elsewhere)
        assertThat(actualWrapper).isNotNull();
        List<HalEntityWrapper<Book, Author>> actualEntityList = actualWrapper.getEntityList();
        assertThat(actualEntityList).isNotNull();
        HalEmbeddedWrapper<Author> actualEmbedded = actualEntityList.get(0).getRequiredEmbedded().get(0);
        assertThat(actualEmbedded).isNotNull();
    }

    @Test
    public void givenEntitiesAndAPageInfo_wrapInListWrapper_thenAllFieldsAreFilled() {
        //GIVEN
        Book entity = new Book();
        Author embedded = new Author();

        //WHEN
        HalListWrapper<Book, Author> actualWrapper = assemblerUnderTest.wrapInListWrapper(
                PairList.of(entity, embedded,
                        entity, embedded),
                HalPageInfo.assemble(30, 1000L, 10, 20L),
                null
        );

        /*
         * THEN
         */
        //Paged Info
        HalPageInfo page = actualWrapper.getPage();
        assertThat(page).isNotNull();
        assertThat(page.totalPages()).isEqualTo(100);
        assertThat(page.totalElements()).isEqualTo(1000);
        assertThat(page.size()).isEqualTo(30);
        assertThat(page.number()).isEqualTo(2);

        //Rudimentary testing (rest is tested elsewhere)
        assertThat(actualWrapper).isNotNull();
        List<HalEntityWrapper<Book, Author>> actualEntityList = actualWrapper.getEntityList();
        assertThat(actualEntityList).isNotNull();
        HalEmbeddedWrapper<Author> actualEmbedded = actualEntityList.get(0).getRequiredEmbedded().get(0);
        assertThat(actualEmbedded).isNotNull();
    }

    @Test
    public void givenEmptyListWrapperWithStringName_whenToEmptyListWrapper_thenEmptyListWithSelfLink() {
        //GIVEN
        String nameOfList = "nameOfList";

        //WHEN
        HalListWrapper<Book, Author> actualWrapper = assemblerUnderTest.createEmptyListWrapper(nameOfList, null);

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
        HalListWrapper<Book, Author> actualWrapper = assemblerUnderTest.createEmptyListWrapper(clazz, null);

        //THEN
        assertThat(actualWrapper.getNameOfEntityList()).isEqualTo("customBooks");
    }
}