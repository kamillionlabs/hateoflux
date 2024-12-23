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


import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PairFluxTest {

    @Test
    void givenFluxOfLeftTAndMapper_whenUsingZipWith_thenPairsAreCreated() {
        // GIVEN
        Flux<String> leftFlux = Flux.just("A", "B", "C");

        // WHEN
        PairFlux<String, Integer> pairFlux = PairFlux.zipWith(leftFlux, str -> {
            if (str.equalsIgnoreCase("A")) {
                return Mono.empty();
            }
            return Mono.just(str.length());
        });

        // THEN
        StepVerifier.create(pairFlux.getFlux())
                .expectNext(Pair.of("A", null))
                .expectNext(Pair.of("B", 1))
                .expectNext(Pair.of("C", 1))
                .verifyComplete();
    }

    @Test
    void givenFluxOfLeftTAndMapper_whenUsingBuilder_thenPairsAreCreated() {
        // GIVEN
        Flux<String> leftFlux = Flux.just("Alpha", "Beta", "Gamma");

        // WHEN
        PairFlux<String, Integer> pairFlux = PairFlux.from(leftFlux)
                .with(str -> {
                    if (str.equalsIgnoreCase("Beta")) {
                        return Mono.empty();
                    }
                    return Mono.just(str.length());
                });

        // THEN
        StepVerifier.create(pairFlux.getFlux())
                .expectNext(Pair.of("Alpha", 5))
                .expectNext(Pair.of("Beta", null))
                .expectNext(Pair.of("Gamma", 5))
                .verifyComplete();
    }

    @Test
    void givenEmptyFlux_whenUsingZipWith_thenResultIsEmpty() {
        // GIVEN
        Flux<String> emptyFlux = Flux.empty();

        // WHEN
        PairFlux<String, Integer> pairFlux = PairFlux.zipWith(emptyFlux, str -> Mono.just(str.length()));

        // THEN
        StepVerifier.create(pairFlux.getFlux())
                .verifyComplete();
    }

    @Test
    void givenEmptyFlux_whenUsingBuilder_thenResultIsEmpty() {
        // GIVEN
        Flux<String> emptyFlux = Flux.empty();

        // WHEN
        PairFlux<String, Integer> pairFlux = PairFlux.from(emptyFlux)
                .with(str -> Mono.just(str.length()));

        // THEN
        StepVerifier.create(pairFlux.getFlux())
                .verifyComplete();
    }

    @Test
    void givenErrorInMapper_whenUsingZipWith_thenErrorIsPropagated() {
        // GIVEN
        Flux<String> leftFlux = Flux.just("A", "B", "C");

        // WHEN
        PairFlux<String, Integer> pairFlux = PairFlux.zipWith(leftFlux, str -> {
            if ("B".equals(str)) {
                return Mono.error(new RuntimeException("Simulated exception"));
            }
            return Mono.just(str.length());
        });

        // THEN
        StepVerifier.create(pairFlux.getFlux())
                .expectNext(Pair.of("A", 1))
                .expectErrorMatches(throwable -> throwable instanceof RuntimeException &&
                        throwable.getMessage().equals("Simulated exception"))
                .verify();
    }


    @Test
    void givenFromIterable_whenPairsAreCreated_thenFluxEmitsCorrectPairs() {
        // GIVEN
        List<Pair<String, Integer>> pairs = List.of(
                Pair.of("X", 10),
                Pair.of("Y", 20),
                Pair.of("Z", 30)
        );

        // WHEN
        PairFlux<String, Integer> pairFlux = PairFlux.fromIterable(pairs);

        // THEN
        StepVerifier.create(pairFlux.getFlux())
                .expectNext(Pair.of("X", 10))
                .expectNext(Pair.of("Y", 20))
                .expectNext(Pair.of("Z", 30))
                .verifyComplete();
    }


    @Test
    void givenFlux_whenOf_thenWrapsFluxCorrectly() {
        // GIVEN
        Flux<Pair<String, Integer>> flux = Flux.just(Pair.of("A", 1), Pair.of("B", 2));

        // WHEN
        PairFlux<String, Integer> pairFlux = PairFlux.of(flux);

        // THEN
        assertThat(pairFlux).isNotNull();
        StepVerifier.create(pairFlux.getFlux())
                .expectNext(Pair.of("A", 1))
                .expectNext(Pair.of("B", 2))
                .verifyComplete();
    }


    @Test
    void givenIterable_whenFromIterable_thenEmitsAllPairs() {
        //GIVEN
        PairList<Integer, String> pairList = PairList.of(1, "one", 2, "two");

        // WHEN
        PairFlux<Integer, String> pairFlux = PairFlux.fromIterable(pairList);

        // THEN
        assertThat(pairFlux).isNotNull();
        StepVerifier.create(pairFlux.getFlux())
                .expectNext(Pair.of(1, "one"))
                .expectNext(Pair.of(2, "two"))
                .verifyComplete();
    }
}
