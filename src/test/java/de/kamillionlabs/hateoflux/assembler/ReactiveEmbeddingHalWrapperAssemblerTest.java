package de.kamillionlabs.hateoflux.assembler;

import de.kamillionlabs.hateoflux.dummy.model.Author;
import de.kamillionlabs.hateoflux.dummy.model.Book;
import de.kamillionlabs.hateoflux.model.hal.HalEmbeddedWrapper;
import de.kamillionlabs.hateoflux.model.hal.HalListWrapper;
import de.kamillionlabs.hateoflux.model.hal.HalPageInfo;
import de.kamillionlabs.hateoflux.model.hal.HalResourceWrapper;
import de.kamillionlabs.hateoflux.model.link.IanaRelation;
import de.kamillionlabs.hateoflux.model.link.Link;
import de.kamillionlabs.hateoflux.utility.Pair;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReactiveEmbeddingHalWrapperAssemblerTest {

    // Implementation for testing purposes -----------------------------------------------------------------------------
    static class AssemblerUnderTest implements ReactiveEmbeddingHalWrapperAssembler<Book, Author> {

        @Override
        public Link buildSelfLinkForResourceList(ServerWebExchange exchange) {
            return Link.of("reactive/resource-list/self/link");
        }

        @Override
        public Link buildSelfLinkForResource(Book resourceToWrap, ServerWebExchange exchange) {
            return Link.of("reactive/resource/self/link");
        }

        @Override
        public Link buildSelfLinkForEmbedded(Author embedded, ServerWebExchange exchange) {
            return Link.of("reactive/embedded/self/link");
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
    public void givenResourceWithEmbedded_whenWrapInResourceWrapper_thenAllFieldsAreFilled() {
        //GIVEN
        Book resource = new Book();
        Author embedded = new Author();

        //WHEN
        HalResourceWrapper<Book, Author> actualWrapper = assemblerUnderTest.wrapInResourceWrapper(
                Mono.just(resource),
                Mono.just(embedded),
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

        //Embedded
        assertThat(actualWrapper.getEmbedded().isPresent()).isTrue();
        assertThat(actualWrapper.getRequiredEmbedded()).hasSize(1);
        HalEmbeddedWrapper<Author> actualEmbedded = actualWrapper.getRequiredEmbedded().get(0);
        assertThat(actualEmbedded.getEmbeddedResource()).isEqualTo(embedded);
        assertThat(actualEmbedded.getLinks().size()).isEqualTo(2);
        assertThat(actualEmbedded.getRequiredLink(IanaRelation.SELF)
                .getHref()).isEqualTo("reactive/embedded/self/link");
        assertThat(actualEmbedded.getRequiredLink("other").getHref()).isEqualTo("reactive/embedded/other/");
    }

    @Test
    public void givenResourceWithEmbeddedList_whenWrapInResourceWrapper_thenEmbeddedHas2Resources() {
        //GIVEN
        Book resource = new Book();
        Author embedded = new Author();

        //WHEN
        HalResourceWrapper<Book, Author> actualWrapper = assemblerUnderTest.wrapInResourceWrapper(
                Mono.just(resource),
                Flux.fromIterable(List.of(embedded, embedded)),
                null
        ).block();

        // THEN
        assertThat(actualWrapper).isNotNull();
        assertThat(actualWrapper.getEmbedded().isPresent()).isTrue();
        assertThat(actualWrapper.getRequiredEmbedded()).hasSize(2);
    }

    @Test
    public void givenResourceWithEmbeddedEmptyListAndStringName_whenWrapInResourceWrapper_thenEmbeddedHasGivenName() {
        //GIVEN
        Book resource = new Book();
        String embeddedListName = "testEmbeddedListName";

        //WHEN
        HalResourceWrapper<Book, Author> actualWrapper = assemblerUnderTest.wrapInResourceWrapper(
                Mono.just(resource),
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
    public void givenResourceWithEmbeddedEmptyListAndClassAsName_whenWrapInResourceWrapper_thenEmbeddedHasGivenClassName() {
        //GIVEN
        Book resource = new Book();
        Class<?> clazz = Book.class;

        //WHEN
        HalResourceWrapper<Book, Author> actualWrapper = assemblerUnderTest.wrapInResourceWrapper(
                Mono.just(resource),
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
    public void givenResourcesEachWithEmbedded_whenWrapInListWrapper_thenAllFieldsAreFilled() {
        //GIVEN
        Book resource = new Book();
        Author embedded = new Author();

        //WHEN
        HalListWrapper<Book, Author> actualWrapper = assemblerUnderTest.wrapInListWrapper(
                Flux.fromIterable(
                        List.of(
                                Pair.of(resource, embedded),
                                Pair.of(resource, embedded)
                        )
                ),
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
        List<HalResourceWrapper<Book, Author>> actualResourceList = actualWrapper.getResourceList();
        assertThat(actualResourceList).isNotNull();
        assertThat(actualResourceList).hasSize(2);
        assertThat(actualResourceList.get(0).getLinks()).hasSize(1);
        assertThat(actualResourceList.get(0).getRequiredLink(IanaRelation.SELF)
                .getHref()).isEqualTo("reactive/resource/self/link");
        assertThat(actualResourceList.get(1).getLinks()).hasSize(1);
        assertThat(actualResourceList.get(1).getRequiredLink(IanaRelation.SELF)
                .getHref()).isEqualTo("reactive/resource/self/link");

        //Embedded
        HalEmbeddedWrapper<Author> actualEmbedded = actualResourceList.get(0).getRequiredEmbedded().get(0);
        assertThat(actualEmbedded).isNotNull();
        assertThat(actualEmbedded.getLinks()).hasSize(2);
    }

    @Test
    public void givenResourcesAndADataForPageInfo_wrapInListWrapper_thenAllFieldsAreFilled() {
        //GIVEN
        Book resource = new Book();
        Author embedded = new Author();

        //WHEN
        HalListWrapper<Book, Author> actualWrapper = assemblerUnderTest.wrapInListWrapper(
                Flux.fromIterable(
                        List.of(
                                Pair.of(resource, embedded),
                                Pair.of(resource, embedded)
                        )
                ),
                Mono.just(100L),
                2,
                null,
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
        List<HalResourceWrapper<Book, Author>> actualResourceList = actualWrapper.getResourceList();
        assertThat(actualResourceList).isNotNull();
        HalEmbeddedWrapper<Author> actualEmbedded = actualResourceList.get(0).getRequiredEmbedded().get(0);
        assertThat(actualEmbedded).isNotNull();
    }

}