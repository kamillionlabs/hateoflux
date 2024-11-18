package de.kamillionlabs.hateoflux.utility.pair;

import de.kamillionlabs.hateoflux.dummy.TestDataGenerator;
import de.kamillionlabs.hateoflux.dummy.model.Author;
import de.kamillionlabs.hateoflux.dummy.model.Book;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static de.kamillionlabs.hateoflux.utility.pair.MultiRightPairListCollector.toMultiRightPairList;
import static org.assertj.core.api.Assertions.assertThat;

public class MultiRightPairListCollectorTest {

    @Test
    void givenEmptyFlux_whenCollectToMultiRightPairList_thenReturnsEmptyMultiRightPairList() {
        // GIVEN
        Flux<MultiRightPair<Book, Author>> emptyFlux = Flux.empty();

        // WHEN
        Mono<MultiRightPairList<Book, Author>> multiRightPairListMono = emptyFlux.collect(toMultiRightPairList());

        // THEN
        StepVerifier.create(multiRightPairListMono)
                .assertNext(multiRightPairList -> assertThat(multiRightPairList).isEmpty())
                .verifyComplete();
    }

    @Test
    void givenFluxWithMultipleMultiRightPairs_whenCollectToMultiRightPairList_thenReturnsMultiRightPairListWithAllPairs() {
        // GIVEN
        TestDataGenerator testData = new TestDataGenerator();
        Book effectiveJavaBook = testData.getBookByTitle("Effective Java");
        Author effectiveJavaAuthor = testData.getAuthorByName(effectiveJavaBook.getAuthor());
        Book cleanCodeBook = testData.getBookByTitle("Clean Code");
        Author cleanCodeAuthor = testData.getAuthorByName(cleanCodeBook.getAuthor());

        MultiRightPair<Author, Book> pair1 = MultiRightPair.of(effectiveJavaAuthor, effectiveJavaBook);
        MultiRightPair<Author, Book> pair2 = MultiRightPair.of(cleanCodeAuthor, cleanCodeBook);

        Flux<MultiRightPair<Author, Book>> fluxWithPairs = Flux.just(pair1, pair2);

        // WHEN
        Mono<MultiRightPairList<Author, Book>> multiRightPairListMono = fluxWithPairs.collect(toMultiRightPairList());

        // THEN
        StepVerifier.create(multiRightPairListMono)
                .assertNext(multiRightPairList -> {
                    assertThat(multiRightPairList)
                            .hasSize(2)
                            .containsExactly(pair1, pair2);
                })
                .verifyComplete();
    }
}