package de.kamillionlabs.hateoflux.assembler;

import de.kamillionlabs.hateoflux.dummy.model.Book;
import de.kamillionlabs.hateoflux.model.hal.HalListWrapper;
import de.kamillionlabs.hateoflux.model.hal.HalPageInfo;
import de.kamillionlabs.hateoflux.model.hal.HalResourceWrapper;
import de.kamillionlabs.hateoflux.model.link.IanaRelation;
import de.kamillionlabs.hateoflux.model.link.Link;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FlatHalWrapperAssemblerTest {

    // Implementation for testing purposes -----------------------------------------------------------------------------
    static class AssemblerUnderTest implements FlatHalWrapperAssembler<Book> {

        @Override
        public Link buildSelfLinkForResourceList(ServerWebExchange exchange) {
            return Link.of("resource-list/self/link");
        }

        @Override
        public Link buildSelfLinkForResource(Book resourceToWrap, ServerWebExchange exchange) {
            return Link.of("resource/self/link");
        }
    }

    // -----------------------------------------------------------------------------------------------------------------
    private final AssemblerUnderTest assemblerUnderTest = new AssemblerUnderTest();

    @Test
    public void givenResourceWithEmbedded_wrapInResourceWrapper_thenAllFieldsAreFilled() {
        //GIVEN
        Book resource = new Book();

        //WHEN
        HalResourceWrapper<Book, Void> actualWrapper = assemblerUnderTest.wrapInResourceWrapper(
                resource,
                null
        );

        /*
         * THEN
         */
        assertThat(actualWrapper).isNotNull();
        //Resource
        Book actualResource = actualWrapper.getResource();
        assertThat(actualResource).isNotNull();
        assertThat(actualResource).isEqualTo(resource);
        assertThat(actualWrapper.getLinks()).hasSize(1);
        assertThat(actualWrapper.getRequiredLink(IanaRelation.SELF).getHref()).isEqualTo("resource/self/link");
    }


    @Test
    public void givenResources_wrapInListWrapper_thenAllFieldsAreFilled() {
        //GIVEN
        Book resource = new Book();

        //WHEN
        HalListWrapper<Book, Void> actualWrapper = assemblerUnderTest.wrapInListWrapper(
                List.of(resource,
                        resource),
                null
        );

        /*
         * THEN
         */
        //HalListWrapper
        assertThat(actualWrapper).isNotNull();
        assertThat(actualWrapper.getResourceList()).hasSize(2);
        assertThat(actualWrapper.getLinks()).hasSize(1);
        assertThat(actualWrapper.getRequiredLink(IanaRelation.SELF).getHref()).isEqualTo("resource-list/self/link");

        //Resources
        List<HalResourceWrapper<Book, Void>> actualResourceList = actualWrapper.getResourceList();
        assertThat(actualResourceList).isNotNull();
        assertThat(actualResourceList).hasSize(2);
        assertThat(actualResourceList.get(0).getLinks()).hasSize(1);
        assertThat(actualResourceList.get(0).getRequiredLink(IanaRelation.SELF)
                .getHref()).isEqualTo("resource/self/link");
        assertThat(actualResourceList.get(1).getLinks()).hasSize(1);
        assertThat(actualResourceList.get(1).getRequiredLink(IanaRelation.SELF)
                .getHref()).isEqualTo("resource/self/link");
    }

    @Test
    public void givenResourcesAndADataForPageInfo_wrapInListWrapper_thenAllFieldsAreFilled() {
        //GIVEN
        Book resource = new Book();

        //WHEN
        HalListWrapper<Book, Void> actualWrapper = assemblerUnderTest.wrapInListWrapper(
                List.of(resource,
                        resource),
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
        List<HalResourceWrapper<Book, Void>> actualResourceList = actualWrapper.getResourceList();
        assertThat(actualResourceList).isNotNull();

    }

    @Test
    public void givenEmptyListWrapperWithStringName_toEmptyListWrapper_thenEmptyListWithSelfLink() {
        //GIVEN
        String nameOfList = "nameOfList";

        //WHEN
        HalListWrapper<Book, Void> actualWrapper = assemblerUnderTest.createEmptyListWrapper(nameOfList, null);

        //THEN
        assertThat(actualWrapper.getResourceList()).isEmpty();
        assertThat(actualWrapper.getNameOfResourceList()).isEqualTo(nameOfList);
        assertThat(actualWrapper.getLinks()).hasSize(1);
        assertThat(actualWrapper.getRequiredLink(IanaRelation.SELF)).isEqualTo(Link.linkAsSelfOf(
                "resource-list/self/link"));
    }

    @Test
    public void givenEmptyListWrapperWithClass_toEmptyListWrapper_thenNameOfListIsTakenFromClass() {
        //GIVEN
        Class<?> clazz = Book.class;

        //WHEN
        HalListWrapper<Book, Void> actualWrapper = assemblerUnderTest.createEmptyListWrapper(clazz, null);

        //THEN
        assertThat(actualWrapper.getNameOfResourceList()).isEqualTo("customBooks");
    }

}