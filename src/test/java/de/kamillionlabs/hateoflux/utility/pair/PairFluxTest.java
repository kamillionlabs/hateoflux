package de.kamillionlabs.hateoflux.utility.pair;


import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

class PairFluxTest {

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
