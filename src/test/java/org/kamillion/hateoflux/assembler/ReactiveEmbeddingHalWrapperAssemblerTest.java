package org.kamillion.hateoflux.assembler;

import org.junit.jupiter.api.Test;
import org.kamillion.hateoflux.dummy.model.Author;
import org.kamillion.hateoflux.dummy.model.Book;
import org.kamillion.hateoflux.model.hal.HalEmbeddedWrapper;
import org.kamillion.hateoflux.model.hal.HalEntityWrapper;
import org.kamillion.hateoflux.model.hal.HalListWrapper;
import org.kamillion.hateoflux.model.hal.HalPageInfo;
import org.kamillion.hateoflux.model.link.IanaRelation;
import org.kamillion.hateoflux.model.link.Link;
import org.kamillion.hateoflux.utility.Pair;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReactiveEmbeddingHalWrapperAssemblerTest {

    // Implementation for testing purposes -----------------------------------------------------------------------------
    static class AssemblerUnderTest implements ReactiveEmbeddingHalWrapperAssembler<Book, Author> {

        @Override
        public Link buildSelfLinkForEntityList(ServerWebExchange exchange) {
            return Link.linkAsSelfOf("reactive/entity-list/self/link");
        }

        @Override
        public Link buildSelfLinkForEntity(Book entityToWrap, ServerWebExchange exchange) {
            return Link.linkAsSelfOf("reactive/entity/self/link");
        }

        @Override
        public Link buildSelfLinkForEmbedded(Author embedded, ServerWebExchange exchange) {
            return Link.linkAsSelfOf("reactive/embedded/self/link");
        }

        @Override
        public List<Link> buildOtherLinksForEmbedded(Author embedded, ServerWebExchange exchange) {
            return List.of(Link.of("reactive/embedded/other/")
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
                Mono.just(entity),
                Mono.just(embedded),
                null
        ).block();

        /*
         * THEN
         */
        assertThat(actualWrapper).isNotNull();
        //Entity
        Book actualEntity = actualWrapper.getEntity();
        assertThat(actualEntity).isNotNull();
        assertThat(actualEntity).isEqualTo(entity);
        assertThat(actualWrapper.getLinks()).hasSize(1);
        assertThat(actualWrapper.getRequiredLink(IanaRelation.SELF).getHref()).isEqualTo("reactive/entity/self/link");

        //Embedded
        assertThat(actualWrapper.getEmbedded().isPresent()).isTrue();
        assertThat(actualWrapper.getRequiredEmbedded()).hasSize(1);
        HalEmbeddedWrapper<Author> actualEmbedded = actualWrapper.getRequiredEmbedded().get(0);
        assertThat(actualEmbedded.getEmbeddedEntity()).isEqualTo(embedded);
        assertThat(actualEmbedded.getLinks().size()).isEqualTo(2);
        assertThat(actualEmbedded.getRequiredLink(IanaRelation.SELF)
                .getHref()).isEqualTo("reactive/embedded/self/link");
        assertThat(actualEmbedded.getRequiredLink("other").getHref()).isEqualTo("reactive/embedded/other/");
    }

    @Test
    public void givenEntityWithEmbeddedList_whenToEntityWrapper_thenEmbeddedHas2Entities() {
        //GIVEN
        Book entity = new Book();
        Author embedded = new Author();

        //WHEN
        HalEntityWrapper<Book, Author> actualWrapper = assemblerUnderTest.toEntityWrapper(
                Mono.just(entity),
                Flux.fromIterable(List.of(embedded, embedded)),
                null
        ).block();

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
                Mono.just(entity),
                embeddedListName,
                Flux.empty(),
                null
        ).block();

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
                Mono.just(entity),
                clazz,
                Flux.empty(),
                null
        ).block();

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
                Flux.fromIterable(
                        List.of(
                                Pair.of(entity, embedded),
                                Pair.of(entity, embedded)
                        )
                ),
                null
        ).block();

        /*
         * THEN
         */
        //HalListWrapper
        assertThat(actualWrapper).isNotNull();
        assertThat(actualWrapper.getEntityList()).hasSize(2);
        assertThat(actualWrapper.getLinks()).hasSize(1);
        assertThat(actualWrapper.getRequiredLink(IanaRelation.SELF)
                .getHref()).isEqualTo("reactive/entity-list/self/link");

        //Entities
        List<HalEntityWrapper<Book, Author>> actualEntityList = actualWrapper.getEntityList();
        assertThat(actualEntityList).isNotNull();
        assertThat(actualEntityList).hasSize(2);
        assertThat(actualEntityList.get(0).getLinks()).hasSize(1);
        assertThat(actualEntityList.get(0).getRequiredLink(IanaRelation.SELF)
                .getHref()).isEqualTo("reactive/entity/self/link");
        assertThat(actualEntityList.get(1).getLinks()).hasSize(1);
        assertThat(actualEntityList.get(1).getRequiredLink(IanaRelation.SELF)
                .getHref()).isEqualTo("reactive/entity/self/link");

        //Embedded
        HalEmbeddedWrapper<Author> actualEmbedded = actualEntityList.get(0).getRequiredEmbedded().get(0);
        assertThat(actualEmbedded).isNotNull();
        assertThat(actualEmbedded.getLinks()).hasSize(2);
    }

    @Test
    public void givenEntitiesAndADataForPageInfo_toPagedListWrapper_thenAllFieldsAreFilled() {
        //GIVEN
        Book entity = new Book();
        Author embedded = new Author();

        //WHEN
        HalListWrapper<Book, Author> actualWrapper = assemblerUnderTest.toPagedListWrapper(
                Flux.fromIterable(
                        List.of(
                                Pair.of(entity, embedded),
                                Pair.of(entity, embedded)
                        )
                ),
                Mono.just(100L),
                2,
                null,
                null
        ).block();

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

}