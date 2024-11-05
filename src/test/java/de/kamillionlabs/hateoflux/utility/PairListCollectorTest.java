package de.kamillionlabs.hateoflux.utility;

import de.kamillionlabs.hateoflux.dummy.model.Author;
import de.kamillionlabs.hateoflux.dummy.model.Book;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static de.kamillionlabs.hateoflux.utility.PairListCollector.toPairList;
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
        Book book1 = Book.builder()
                .title("Effective Java")
                .author("Joshua Bloch")
                .isbn("978-0134685991")
                .publishedDate("2018-01-06")
                .build();

        Author author1 = Author.builder()
                .name("Joshua Bloch")
                .birthDate("1961-08-28")
                .mainGenre("Programming")
                .build();

        Book book2 = Book.builder()
                .title("Clean Code")
                .author("Robert C. Martin")
                .isbn("978-0132350884")
                .publishedDate("2008-08-01")
                .build();

        Author author2 = Author.builder()
                .name("Robert C. Martin")
                .birthDate("1952-12-05")
                .mainGenre("Software Engineering")
                .build();
        Flux<Pair<Book, Author>> fluxWithPairs = Flux.just(
                Pair.of(book1, author1),
                Pair.of(book2, author2)
        );

        // WHEN
        Mono<PairList<Book, Author>> pairListMono = fluxWithPairs.collect(toPairList());

        // THEN
        StepVerifier.create(pairListMono)
                .assertNext(pairList -> {
                    assertThat(pairList)
                            .hasSize(2)
                            .containsExactly(
                                    Pair.of(book1, author1),
                                    Pair.of(book2, author2)
                            );
                })
                .verifyComplete();
    }
}