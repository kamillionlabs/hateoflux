package de.kamillionlabs.hateoflux.assembler;

import de.kamillionlabs.hateoflux.dummy.model.Book;
import de.kamillionlabs.hateoflux.model.hal.HalListWrapper;
import de.kamillionlabs.hateoflux.model.hal.HalPageInfo;
import de.kamillionlabs.hateoflux.model.hal.HalResourceWrapper;
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
        public Link buildSelfLinkForResourceList(ServerWebExchange exchange) {
            return Link.of("reactive/resource-list/self/link");
        }

        @Override
        public Link buildSelfLinkForResource(Book resourceToWrap, ServerWebExchange exchange) {
            return Link.of("reactive/resource/self/link");
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
                Mono.just(resource),
                null
        ).block();

        /*
         * THEN
         */
        assertThat(actualWrapper).isNotNull();
        //Resource
        Book actualResource = actualWrapper.getResource();
        assertThat(actualResource).isNotNull();
        assertThat(actualResource).isEqualTo(resource);
        assertThat(actualWrapper.getLinks()).hasSize(1);
        assertThat(actualWrapper.getRequiredLink(IanaRelation.SELF).getHref()).isEqualTo("reactive/resource/self/link");
    }


    @Test
    public void givenResources_wrapInListWrapper_thenAllFieldsAreFilled() {
        //GIVEN
        Book resource = new Book();

        //WHEN
        HalListWrapper<Book, Void> actualWrapper = assemblerUnderTest.wrapInListWrapper(
                Flux.fromIterable(List.of(resource,
                        resource)),
                null
        ).block();

        /*
         * THEN
         */
        //HalListWrapper
        assertThat(actualWrapper).isNotNull();
        assertThat(actualWrapper.getResourceList()).hasSize(2);
        assertThat(actualWrapper.getLinks()).hasSize(1);
        assertThat(actualWrapper.getRequiredLink(IanaRelation.SELF)
                .getHref()).isEqualTo("reactive/resource-list/self/link");

        //Resources
        List<HalResourceWrapper<Book, Void>> actualResourceList = actualWrapper.getResourceList();
        assertThat(actualResourceList).isNotNull();
        assertThat(actualResourceList).hasSize(2);
        assertThat(actualResourceList.get(0).getLinks()).hasSize(1);
        assertThat(actualResourceList.get(0).getRequiredLink(IanaRelation.SELF)
                .getHref()).isEqualTo("reactive/resource/self/link");
        assertThat(actualResourceList.get(1).getLinks()).hasSize(1);
        assertThat(actualResourceList.get(1).getRequiredLink(IanaRelation.SELF)
                .getHref()).isEqualTo("reactive/resource/self/link");
    }

    @Test
    public void givenResourcesAndADataForPageInfo_wrapInListWrapper_thenAllFieldsAreFilled() {
        //GIVEN
        Book resource = new Book();

        //WHEN
        HalListWrapper<Book, Void> actualWrapper = assemblerUnderTest.wrapInListWrapper(
                Flux.fromIterable(List.of(resource,
                        resource)),
                Mono.just(25L),
                5,
                10L,
                null,
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
        assertThat(page.size()).isEqualTo(5);
        assertThat(page.number()).isEqualTo(2);

        //Rudimentary testing (rest is tested elsewhere)
        assertThat(actualWrapper).isNotNull();
        List<HalResourceWrapper<Book, Void>> actualResourceList = actualWrapper.getResourceList();
        assertThat(actualResourceList).isNotNull();
    }
}