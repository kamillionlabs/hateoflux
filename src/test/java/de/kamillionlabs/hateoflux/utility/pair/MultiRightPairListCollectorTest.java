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