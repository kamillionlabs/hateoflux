package de.kamillionlabs.hateoflux.model.hal;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HalPageInfoTest {

    @Test
    void givenParameters_whenAssembleWithFourParameters() {
        HalPageInfo pageInfo = HalPageInfo.assemble(10, 100L, 10, 1L);

        assertThat(pageInfo).isNotNull();
        assertThat(pageInfo.size()).isEqualTo(10);
        assertThat(pageInfo.totalElements()).isEqualTo(100L);
        assertThat(pageInfo.totalPages()).isEqualTo(10);
        assertThat(pageInfo.number()).isEqualTo(0);
    }

    @Test
    void givenListAndTotalElements_whenAssembleWithListAndPageSize() {
        List<?> resources = Collections.nCopies(10, new Object());
        HalPageInfo pageInfo = HalPageInfo.assemble(resources, 100L, 10);

        assertThat(pageInfo).isNotNull();
        assertThat(pageInfo.size()).isEqualTo(10);
        assertThat(pageInfo.totalElements()).isEqualTo(100L);
        assertThat(pageInfo.totalPages()).isEqualTo(10);
        assertThat(pageInfo.number()).isEqualTo(0);
    }

    @Test
    void givenListTotalElementsPageSizeAndOffset_whenAssembleWithListOffset() {
        List<?> resources = Collections.nCopies(10, new Object());
        HalPageInfo pageInfo = HalPageInfo.assemble(resources, 100L, 10, 10L);

        assertThat(pageInfo).isNotNull();
        assertThat(pageInfo.size()).isEqualTo(10);
        assertThat(pageInfo.totalElements()).isEqualTo(100L);
        assertThat(pageInfo.totalPages()).isEqualTo(10);
        assertThat(pageInfo.number()).isEqualTo(1);
    }
}
