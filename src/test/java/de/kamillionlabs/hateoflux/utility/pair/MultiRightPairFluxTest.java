package de.kamillionlabs.hateoflux.utility.pair;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MultiRightPairFluxTest {

    @Test
    void givenFlux_whenOf_thenWrapsFluxCorrectly() {
        // GIVEN
        Flux<MultiRightPair<String, Integer>> flux = Flux.just(MultiRightPair.of("A", 1), MultiRightPair.of("B", 2));

        // WHEN
        MultiRightPairFlux<String, Integer> actualPairFlux = MultiRightPairFlux.of(flux);

        // THEN
        assertThat(actualPairFlux).isNotNull();
        StepVerifier.create(actualPairFlux.getFlux())
                .expectNext(MultiRightPair.of("A", 1))
                .expectNext(MultiRightPair.of("B", 2))
                .verifyComplete();
    }


    @Test
    void givenIterable_whenFromIterable_thenEmitsAllPairs() {
        // GIVEN
        MultiRightPairList<Integer, String> multiRightPairs = MultiRightPairList.of(1, List.of("o", "n", "e"),
                2, List.of("t", "w", "o"));

        // WHEN
        MultiRightPairFlux<Integer, String> pairFlux = MultiRightPairFlux.fromIterable(multiRightPairs);

        // THEN
        assertThat(pairFlux).isNotNull();
        StepVerifier.create(pairFlux.getFlux())
                .expectNext(MultiRightPair.of(1, List.of("o", "n", "e")))
                .expectNext(MultiRightPair.of(2, List.of("t", "w", "o")))
                .verifyComplete();
    }

}