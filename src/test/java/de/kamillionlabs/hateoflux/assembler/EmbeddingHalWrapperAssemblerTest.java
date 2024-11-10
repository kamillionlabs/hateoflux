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
import de.kamillionlabs.hateoflux.utility.PairList;
import de.kamillionlabs.hateoflux.utility.SortCriteria;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static de.kamillionlabs.hateoflux.utility.SortDirection.ASCENDING;
import static org.assertj.core.api.Assertions.assertThat;

class EmbeddingHalWrapperAssemblerTest {

    // Implementation for testing purposes -----------------------------------------------------------------------------
    static class AssemblerUnderTest implements EmbeddingHalWrapperAssembler<Book, Author> {

        @Override
        public Class<Book> getResourceTClass() {
            return Book.class;
        }

        @Override
        public Link buildSelfLinkForResourceList(ServerWebExchange exchange) {
            return Link.of("resource-list/self/link");
        }

        @Override
        public Link buildSelfLinkForResource(Book resourceToWrap, ServerWebExchange exchange) {
            return Link.of("resource/self/link");
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
    public void givenResourceWithEmbedded_whenWrapInResourceWrapper_thenAllFieldsAreFilled() {
        //GIVEN
        Book resource = new Book();
        Author embedded = new Author();

        //WHEN
        HalResourceWrapper<Book, Author> actualWrapper = assemblerUnderTest.wrapInResourceWrapper(
                resource,
                embedded,
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

        //Embedded
        assertThat(actualWrapper.getEmbedded().isPresent()).isTrue();
        assertThat(actualWrapper.getRequiredEmbedded()).hasSize(1);
        HalEmbeddedWrapper<Author> actualEmbedded = actualWrapper.getRequiredEmbedded().get(0);
        assertThat(actualEmbedded.getEmbeddedResource()).isEqualTo(embedded);
        assertThat(actualEmbedded.getLinks().size()).isEqualTo(2);
        assertThat(actualEmbedded.getRequiredLink(IanaRelation.SELF).getHref()).isEqualTo("embedded/self/link");
        assertThat(actualEmbedded.getRequiredLink("other").getHref()).isEqualTo("embedded/other/");
    }

    @Test
    public void givenResourceWithEmbedded_whenWrapInResourceWrapperReactive_thenAllFieldsAreFilled() {
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
        assertThat(actualWrapper.getRequiredLink(IanaRelation.SELF).getHref()).isEqualTo("resource/self/link");

        //Embedded
        assertThat(actualWrapper.getEmbedded().isPresent()).isTrue();
        assertThat(actualWrapper.getRequiredEmbedded()).hasSize(1);
        HalEmbeddedWrapper<Author> actualEmbedded = actualWrapper.getRequiredEmbedded().get(0);
        assertThat(actualEmbedded.getEmbeddedResource()).isEqualTo(embedded);
        assertThat(actualEmbedded.getLinks().size()).isEqualTo(2);
        assertThat(actualEmbedded.getRequiredLink(IanaRelation.SELF)
                .getHref()).isEqualTo("embedded/self/link");
        assertThat(actualEmbedded.getRequiredLink("other").getHref()).isEqualTo("embedded/other/");
    }

    @Test
    public void givenResourceWithEmbeddedList_whenWrapInResourceWrapper_thenEmbeddedHas2Resources() {
        //GIVEN
        Book resource = new Book();
        Author embedded = new Author();

        //WHEN
        HalResourceWrapper<Book, Author> actualWrapper = assemblerUnderTest.wrapInResourceWrapper(
                resource,
                List.of(embedded, embedded),
                null
        );

        // THEN
        assertThat(actualWrapper).isNotNull();
        assertThat(actualWrapper.getEmbedded().isPresent()).isTrue();
        assertThat(actualWrapper.getRequiredEmbedded()).hasSize(2);
    }

    @Test
    public void givenResourceWithEmbeddedList_whenWrapInResourceWrapperReactive_thenEmbeddedHas2Resources() {
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
                resource,
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
    public void givenResourceWithEmbeddedEmptyListAndStringName_whenWrapInResourceWrapperReactive_thenEmbeddedHasGivenName() {
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
                resource,
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
    public void givenResourceWithEmbeddedEmptyListAndClassAsName_whenWrapInResourceWrapperReactive_thenEmbeddedHasGivenClassName() {
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
                PairList.of(resource, embedded,
                        resource, embedded),
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
        List<HalResourceWrapper<Book, Author>> actualResourceList = actualWrapper.getResourceList();
        assertThat(actualResourceList).isNotNull();
        assertThat(actualResourceList).hasSize(2);
        assertThat(actualResourceList.get(0).getLinks()).hasSize(1);
        assertThat(actualResourceList.get(0).getRequiredLink(IanaRelation.SELF)
                .getHref()).isEqualTo("resource/self/link");
        assertThat(actualResourceList.get(1).getLinks()).hasSize(1);
        assertThat(actualResourceList.get(1).getRequiredLink(IanaRelation.SELF)
                .getHref()).isEqualTo("resource/self/link");

        //Embedded
        HalEmbeddedWrapper<Author> actualEmbedded = actualResourceList.get(0).getRequiredEmbedded().get(0);
        assertThat(actualEmbedded).isNotNull();
        assertThat(actualEmbedded.getLinks()).hasSize(2);
    }

    @Test
    public void givenResourcesEachWithEmbedded_whenWrapInListWrapperReactive_thenAllFieldsAreFilled() {
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
                .getHref()).isEqualTo("resource-list/self/link");

        //Resources
        List<HalResourceWrapper<Book, Author>> actualResourceList = actualWrapper.getResourceList();
        assertThat(actualResourceList).isNotNull();
        assertThat(actualResourceList).hasSize(2);
        assertThat(actualResourceList.get(0).getLinks()).hasSize(1);
        assertThat(actualResourceList.get(0).getRequiredLink(IanaRelation.SELF)
                .getHref()).isEqualTo("resource/self/link");
        assertThat(actualResourceList.get(1).getLinks()).hasSize(1);
        assertThat(actualResourceList.get(1).getRequiredLink(IanaRelation.SELF)
                .getHref()).isEqualTo("resource/self/link");

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
                PairList.of(resource, embedded,
                        resource, embedded),
                100L,
                2,
                null,
                List.of(SortCriteria.by("title", ASCENDING)),
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
        List<HalResourceWrapper<Book, Author>> actualResourceList = actualWrapper.getResourceList();
        assertThat(actualResourceList).isNotNull();
        HalEmbeddedWrapper<Author> actualEmbedded = actualResourceList.get(0).getRequiredEmbedded().get(0);
        assertThat(actualEmbedded).isNotNull();
    }

    @Test
    public void givenResourcesAndADataForPageInfo_wrapInListWrapperReactive_thenAllFieldsAreFilled() {
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

    @Test
    public void givenResourcesAndAPageInfo_wrapInListWrapper_thenAllFieldsAreFilled() {
        //GIVEN
        Book resource = new Book();
        Author embedded = new Author();

        //WHEN
        HalListWrapper<Book, Author> actualWrapper = assemblerUnderTest.wrapInListWrapper(
                PairList.of(resource, embedded,
                        resource, embedded),
                HalPageInfo.assembleWithOffset(10, 1000L, 20L),
                null,
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
        assertThat(page.size()).isEqualTo(10);
        assertThat(page.number()).isEqualTo(2);

        //Rudimentary testing (rest is tested elsewhere)
        assertThat(actualWrapper).isNotNull();
        assertThat(actualWrapper.isEmpty()).isEqualTo(false);
        List<HalResourceWrapper<Book, Author>> actualResourceList = actualWrapper.getResourceList();
        assertThat(actualResourceList).isNotNull();
        HalEmbeddedWrapper<Author> actualEmbedded = actualResourceList.get(0).getRequiredEmbedded().get(0);
        assertThat(actualEmbedded).isNotNull();
    }

    @Test
    public void givenEmptyListWrapperWithStringName_whenToEmptyListWrapper_thenEmptyListWithSelfLink() {
        //GIVEN
        String nameOfList = "nameOfList";

        //WHEN
        HalListWrapper<Book, Author> actualWrapper = assemblerUnderTest.createEmptyListWrapper(nameOfList, null);

        //THEN
        assertThat(actualWrapper.getResourceList()).isEmpty();
        assertThat(actualWrapper.getNameOfResourceList()).isEqualTo(nameOfList);
        assertThat(actualWrapper.getLinks()).hasSize(1);
        assertThat(actualWrapper.getRequiredLink(IanaRelation.SELF)).isEqualTo(Link.linkAsSelfOf("resource-list/self" +
                "/link"));
    }

    @Test
    public void givenEmptyListWrapperWithClass_whenToEmptyListWrapper_thenNameOfListIsTakenFromClass() {
        //GIVEN
        Class<?> clazz = Book.class;

        //WHEN
        HalListWrapper<Book, Author> actualWrapper = assemblerUnderTest.createEmptyListWrapper(clazz, null);

        //THEN
        assertThat(actualWrapper.getNameOfResourceList()).isEqualTo("customBooks");
    }

    @Test
    public void givenEmptyPairs_whenWrapInListWrapper_thenNoException() {
        //GIVEN
        HalListWrapper<Book, Author> emptyWrapper = assemblerUnderTest.wrapInListWrapper(new PairList<>(), null);

        //THEN
        assertThat(emptyWrapper).isNotNull();
        assertThat(emptyWrapper.isEmpty()).isEqualTo(true);
        assertThat(emptyWrapper.getNameOfResourceList()).isEqualTo("customBooks");
        assertThat(emptyWrapper.getLinks()).hasSize(1);
        assertThat(emptyWrapper.getLinks().get(0).getHref()).isEqualTo("resource-list/self/link");
    }

}