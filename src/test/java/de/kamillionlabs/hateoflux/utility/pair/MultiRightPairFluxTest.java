package de.kamillionlabs.hateoflux.utility.pair;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MultiRightPairFluxTest {

    @Test
    void givenFluxOfLeftTAndMapper_whenUsingZipWith_thenMultiRightPairsAreCreated() {
        // GIVEN
        Flux<String> leftFlux = Flux.just("A", "B", "C");

        // WHEN
        MultiRightPairFlux<String, Integer> multiRightPairFlux = MultiRightPairFlux.zipWith(
                leftFlux,
                left -> {
                    if (left.equalsIgnoreCase("A")) {
                        return Flux.empty();
                    }
                    return Flux.just(left.length(), left.length() * 2);
                }
        );

        // THEN
        StepVerifier.create(multiRightPairFlux.getFlux())
                .assertNext(pair -> {
                    assertThat(pair.getLeft()).isEqualTo("A");
                    assertThat(pair.getRights()).isEmpty();
                })
                .assertNext(pair -> {
                    assertThat(pair.getLeft()).isEqualTo("B");
                    assertThat(pair.getRights()).containsExactly(1, 2);
                })
                .assertNext(pair -> {
                    assertThat(pair.getLeft()).isEqualTo("C");
                    assertThat(pair.getRights()).containsExactly(1, 2);
                })
                .verifyComplete();
    }

    @Test
    void givenFluxOfLeftTAndMapper_whenUsingBuilder_thenMultiRightPairsAreCreated() {
        // GIVEN
        Flux<String> leftFlux = Flux.just("Alpha", "Beta", "Gamma");

        // WHEN
        MultiRightPairFlux<String, Integer> multiRightPairFlux = MultiRightPairFlux.from(leftFlux)
                .with(left -> {
                    if (left.equalsIgnoreCase("Beta")) {
                        return Flux.empty();
                    }
                    return Flux.just(left.length(), left.length() + 1);
                });

        // THEN
        StepVerifier.create(multiRightPairFlux.getFlux())
                .assertNext(pair -> {
                    assertThat(pair.getLeft()).isEqualTo("Alpha");
                    assertThat(pair.getRights()).containsExactly(5, 6);
                })
                .assertNext(pair -> {
                    assertThat(pair.getLeft()).isEqualTo("Beta");
                    assertThat(pair.getRights()).isEmpty();
                })
                .assertNext(pair -> {
                    assertThat(pair.getLeft()).isEqualTo("Gamma");
                    assertThat(pair.getRights()).containsExactly(5, 6);
                })
                .verifyComplete();
    }

    @Test
    void givenEmptyFlux_whenUsingZipWith_thenResultIsEmpty() {
        // GIVEN
        Flux<String> emptyFlux = Flux.empty();

        // WHEN
        MultiRightPairFlux<String, Integer> multiRightPairFlux = MultiRightPairFlux.zipWith(
                emptyFlux,
                left -> Flux.just(left.length(), left.length() + 1)
        );

        // THEN
        StepVerifier.create(multiRightPairFlux.getFlux())
                .verifyComplete();
    }

    @Test
    void givenEmptyFlux_whenUsingBuilder_thenResultIsEmpty() {
        // GIVEN
        Flux<String> emptyFlux = Flux.empty();

        // WHEN
        MultiRightPairFlux<String, Integer> multiRightPairFlux = MultiRightPairFlux.from(emptyFlux)
                .with(left -> Flux.just(left.length(), left.length() + 1));

        // THEN
        StepVerifier.create(multiRightPairFlux.getFlux())
                .verifyComplete();
    }


    @Test
    void givenFromIterable_whenMultiRightPairsAreCreated_thenFluxEmitsCorrectPairs() {
        // GIVEN
        List<MultiRightPair<String, Integer>> multiRightPairs = List.of(
                new MultiRightPair<>("X", List.of(10, 20)),
                new MultiRightPair<>("Y", List.of(30, 40)),
                new MultiRightPair<>("Z", List.of(50, 60))
        );

        // WHEN
        MultiRightPairFlux<String, Integer> multiRightPairFlux = MultiRightPairFlux.fromIterable(multiRightPairs);

        // THEN
        StepVerifier.create(multiRightPairFlux.getFlux())
                .assertNext(pair -> {
                    assertThat(pair.getLeft()).isEqualTo("X");
                    assertThat(pair.getRights()).containsExactly(10, 20);
                })
                .assertNext(pair -> {
                    assertThat(pair.getLeft()).isEqualTo("Y");
                    assertThat(pair.getRights()).containsExactly(30, 40);
                })
                .assertNext(pair -> {
                    assertThat(pair.getLeft()).isEqualTo("Z");
                    assertThat(pair.getRights()).containsExactly(50, 60);
                })
                .verifyComplete();
    }

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