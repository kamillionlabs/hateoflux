package org.kamillion.hateoflux.assembler;

import org.junit.jupiter.api.Test;
import org.kamillion.hateoflux.dummy.model.Book;
import org.kamillion.hateoflux.model.hal.HalEntityWrapper;
import org.kamillion.hateoflux.model.hal.HalListWrapper;
import org.kamillion.hateoflux.model.hal.HalPageInfo;
import org.kamillion.hateoflux.model.link.IanaRelation;
import org.kamillion.hateoflux.model.link.Link;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FlatHalWrapperAssemblerTest {

    // Implementation for testing purposes -----------------------------------------------------------------------------
    static class AssemblerUnderTest implements FlatHalWrapperAssembler<Book> {

        @Override
        public Link buildSelfLinkForEntityList(ServerWebExchange exchange) {
            return Link.linkAsSelfOf("entity-list/self/link");
        }

        @Override
        public Link buildSelfLinkForEntity(Book entityToWrap, ServerWebExchange exchange) {
            return Link.linkAsSelfOf("entity/self/link");
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
                entity,
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
    }


    @Test
    public void givenEntities_wrapInListWrapper_thenAllFieldsAreFilled() {
        //GIVEN
        Book entity = new Book();

        //WHEN
        HalListWrapper<Book, Void> actualWrapper = assemblerUnderTest.wrapInListWrapper(
                List.of(entity,
                        entity),
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
        List<HalEntityWrapper<Book, Void>> actualEntityList = actualWrapper.getEntityList();
        assertThat(actualEntityList).isNotNull();
        assertThat(actualEntityList).hasSize(2);
        assertThat(actualEntityList.get(0).getLinks()).hasSize(1);
        assertThat(actualEntityList.get(0).getRequiredLink(IanaRelation.SELF).getHref()).isEqualTo("entity/self/link");
        assertThat(actualEntityList.get(1).getLinks()).hasSize(1);
        assertThat(actualEntityList.get(1).getRequiredLink(IanaRelation.SELF).getHref()).isEqualTo("entity/self/link");
    }

    @Test
    public void givenEntitiesAndADataForPageInfo_wrapInListWrapper_thenAllFieldsAreFilled() {
        //GIVEN
        Book entity = new Book();

        //WHEN
        HalListWrapper<Book, Void> actualWrapper = assemblerUnderTest.wrapInListWrapper(
                List.of(entity,
                        entity),
                25L, 5, 10L,
                null
        );

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

    @Test
    public void givenEmptyListWrapperWithStringName_toEmptyListWrapper_thenEmptyListWithSelfLink() {
        //GIVEN
        String nameOfList = "nameOfList";

        //WHEN
        HalListWrapper<Book, Void> actualWrapper = assemblerUnderTest.createEmptyListWrapper(nameOfList, null);

        //THEN
        assertThat(actualWrapper.getEntityList()).isEmpty();
        assertThat(actualWrapper.getNameOfEntityList()).isEqualTo(nameOfList);
        assertThat(actualWrapper.getLinks()).hasSize(1);
        assertThat(actualWrapper.getRequiredLink(IanaRelation.SELF)).isEqualTo(Link.linkAsSelfOf(
                "entity-list/self/link"));
    }

    @Test
    public void givenEmptyListWrapperWithClass_toEmptyListWrapper_thenNameOfListIsTakenFromClass() {
        //GIVEN
        Class<?> clazz = Book.class;

        //WHEN
        HalListWrapper<Book, Void> actualWrapper = assemblerUnderTest.createEmptyListWrapper(clazz, null);

        //THEN
        assertThat(actualWrapper.getNameOfEntityList()).isEqualTo("customBooks");
    }

}