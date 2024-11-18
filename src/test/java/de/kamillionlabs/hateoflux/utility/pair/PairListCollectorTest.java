package de.kamillionlabs.hateoflux.utility.pair;

import de.kamillionlabs.hateoflux.dummy.TestDataGenerator;
import de.kamillionlabs.hateoflux.dummy.model.Author;
import de.kamillionlabs.hateoflux.dummy.model.Book;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static de.kamillionlabs.hateoflux.utility.pair.PairListCollector.toPairList;
import static org.assertj.core.api.Assertions.assertThat;

class PairListCollectorTest {


    @Test
    void givenEmptyFlux_whenCollectToPairList_thenReturnsEmptyPairList() {
        // GIVEN
        Flux<Pair<Book, Author>> emptyFlux = Flux.empty();

        // WHEN
        Mono<PairList<Book, Author>> pairListMono = emptyFlux.collect(toPairList());

        // THEN
        StepVerifier.create(pairListMono)
                .assertNext(pairList -> assertThat(pairList).isEmpty())
                .verifyComplete();
    }

    @Test
    void givenFluxWithMultiplePairs_whenCollectToPairList_thenReturnsPairListWithAllPairs() {
        // GIVEN
        TestDataGenerator testData = new TestDataGenerator();
        Book effectiveJavaBook = testData.getBookByTitle("Effective Java");
        Author effectiveJavaAuthor = testData.getAuthorByName(effectiveJavaBook.getAuthor());
        Book cleanCodeBook = testData.getBookByTitle("Clean Code");
        Author cleanCodeAuthor = testData.getAuthorByName(cleanCodeBook.getAuthor());

        PairList<Author, Book> bookAndHisAuthor = PairList.of(
                effectiveJavaAuthor, effectiveJavaBook,
                cleanCodeAuthor, cleanCodeBook
        );

        Flux<Pair<Author, Book>> fluxWithPairs = Flux.fromIterable(bookAndHisAuthor);


        // WHEN
        Mono<PairList<Author, Book>> pairListMono = fluxWithPairs.collect(toPairList());

        // THEN
        StepVerifier.create(pairListMono)
                .assertNext(pairList -> {
                    assertThat(pairList)
                            .hasSize(2)
                            .containsAll(
                                    bookAndHisAuthor
                            );
                })
                .verifyComplete();
    }
}