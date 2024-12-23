/*
 * Copyright (c)  2024 kamillion labs contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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