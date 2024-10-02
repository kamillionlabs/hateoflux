package de.kamillionlabs.hateoflux.assembler;

import de.kamillionlabs.hateoflux.dummy.model.Book;
import de.kamillionlabs.hateoflux.model.hal.HalEntityWrapper;
import de.kamillionlabs.hateoflux.model.hal.HalListWrapper;
import de.kamillionlabs.hateoflux.model.hal.HalPageInfo;
import de.kamillionlabs.hateoflux.model.link.IanaRelation;
import de.kamillionlabs.hateoflux.model.link.Link;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReactiveFlatHalWrapperAssemblerTest {

    // Implementation for testing purposes -----------------------------------------------------------------------------
    static class AssemblerUnderTest implements ReactiveFlatHalWrapperAssembler<Book> {

        @Override
        public Link buildSelfLinkForEntityList(ServerWebExchange exchange) {
            return Link.linkAsSelfOf("reactive/entity-list/self/link");
        }

        @Override
        public Link buildSelfLinkForEntity(Book entityToWrap, ServerWebExchange exchange) {
            return Link.linkAsSelfOf("reactive/entity/self/link");
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final AssemblerUnderTest assemblerUnderTest = new AssemblerUnderTest();

    @Test
    public void givenEntityWithEmbedded_wrapInEntityWrapper_thenAllFieldsAreFilled() {
        //GIVEN
        Book entity = new Book();

        //WHEN
        HalEntityWrapper<Book, Void> actualWrapper = assemblerUnderTest.wrapInEntityWrapper(
                Mono.just(entity),
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
    }


    @Test
    public void givenEntities_wrapInListWrapper_thenAllFieldsAreFilled() {
        //GIVEN
        Book entity = new Book();

        //WHEN
        HalListWrapper<Book, Void> actualWrapper = assemblerUnderTest.WrapInListWrapper(
                Flux.fromIterable(List.of(entity,
                        entity)),
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
        List<HalEntityWrapper<Book, Void>> actualEntityList = actualWrapper.getEntityList();
        assertThat(actualEntityList).isNotNull();
        assertThat(actualEntityList).hasSize(2);
        assertThat(actualEntityList.get(0).getLinks()).hasSize(1);
        assertThat(actualEntityList.get(0).getRequiredLink(IanaRelation.SELF)
                .getHref()).isEqualTo("reactive/entity/self/link");
        assertThat(actualEntityList.get(1).getLinks()).hasSize(1);
        assertThat(actualEntityList.get(1).getRequiredLink(IanaRelation.SELF)
                .getHref()).isEqualTo("reactive/entity/self/link");
    }

    @Test
    public void givenEntitiesAndADataForPageInfo_wrapInListWrapper_thenAllFieldsAreFilled() {
        //GIVEN
        Book entity = new Book();

        //WHEN
        HalListWrapper<Book, Void> actualWrapper = assemblerUnderTest.wrapInListWrapper(
                Flux.fromIterable(List.of(entity,
                        entity)),
                Mono.just(25L), 5, 10L,
                null
        ).block();

        /*
         * THEN
         */
        //Paged Info
        HalPageInfo page = actualWrapper.getPage();
        assertThat(page).isNotNull();
        assertThat(page.totalPages()).isEqualTo(5);
        assertThat(page.totalElements()).isEqualTo(25);
        assertThat(page.size()).isEqualTo(2);
        assertThat(page.number()).isEqualTo(2);

        //Rudimentary testing (rest is tested elsewhere)
        assertThat(actualWrapper).isNotNull();
        List<HalEntityWrapper<Book, Void>> actualEntityList = actualWrapper.getEntityList();
        assertThat(actualEntityList).isNotNull();
    }
}