package org.kamillion.hateoflux.model.hal;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HalPageInfoTest {

    @Test
    void givenParameters_whenOfWithFourParameters() {
        HalPageInfo pageInfo = HalPageInfo.of(10, 100L, 10, 1);

        assertThat(pageInfo).isNotNull();
        assertThat(pageInfo.size()).isEqualTo(10);
        assertThat(pageInfo.totalElements()).isEqualTo(100L);
        assertThat(pageInfo.totalPages()).isEqualTo(10);
        assertThat(pageInfo.number()).isEqualTo(1);
    }

    @Test
    void givenListAndTotalElements_whenOfWithListAndPageSize() {
        List<?> entities = Collections.nCopies(10, new Object());
        HalPageInfo pageInfo = HalPageInfo.of(entities, 100L, 10);

        assertThat(pageInfo).isNotNull();
        assertThat(pageInfo.size()).isEqualTo(10);
        assertThat(pageInfo.totalElements()).isEqualTo(100L);
        assertThat(pageInfo.totalPages()).isEqualTo(10);
        assertThat(pageInfo.number()).isEqualTo(0);
    }

    @Test
    void givenListTotalElementsPageSizeAndOffset_whenOfWithListOffset() {
        List<?> entities = Collections.nCopies(10, new Object());
        HalPageInfo pageInfo = HalPageInfo.of(entities, 100L, 10, 10L);

        assertThat(pageInfo).isNotNull();
        assertThat(pageInfo.size()).isEqualTo(10);
        assertThat(pageInfo.totalElements()).isEqualTo(100L);
        assertThat(pageInfo.totalPages()).isEqualTo(10);
        assertThat(pageInfo.number()).isEqualTo(1);
    }

    @Test
    void givenFluxTotalElementsPageSizeAndOffset_whenOfWithFluxOffset() {
        Flux<?> entities = Flux.just(new Object(), new Object(), new Object());
        Mono<Long> totalElements = Mono.just(100L);
        int pageSize = 10;
        Long offset = 10L;

        Mono<HalPageInfo> pageInfoMono = HalPageInfo.of(entities, totalElements, pageSize, offset);

        StepVerifier.create(pageInfoMono)
                .assertNext(pageInfo -> {
                    assertThat(pageInfo).isNotNull();
                    assertThat(pageInfo.size()).isEqualTo(3);
                    assertThat(pageInfo.totalElements()).isEqualTo(100L);
                    assertThat(pageInfo.totalPages()).isEqualTo(10);
                    assertThat(pageInfo.number()).isEqualTo(1);
                })
                .verifyComplete();
    }

    @Test
    void givenFluxAndTotalElements_whenOfWithFluxNoOffset() {
        Flux<?> entities = Flux.just(new Object(), new Object(), new Object());
        Mono<Long> totalElements = Mono.just(100L);
        int pageSize = 10;

        Mono<HalPageInfo> pageInfoMono = HalPageInfo.of(entities, totalElements, pageSize);

        StepVerifier.create(pageInfoMono)
                .assertNext(pageInfo -> {
                    assertThat(pageInfo).isNotNull();
                    assertThat(pageInfo.size()).isEqualTo(3);
                    assertThat(pageInfo.totalElements()).isEqualTo(100L);
                    assertThat(pageInfo.totalPages()).isEqualTo(10);
                    assertThat(pageInfo.number()).isEqualTo(0);
                })
                .verifyComplete();
    }
}
