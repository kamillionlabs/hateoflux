package de.kamillionlabs.hateoflux.model.hal;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HalPageInfoTest {

    @Test
    void givenOffset_whenAssembleWithPageNumberWithOffsetWithFourParameters() {
        HalPageInfo pageInfo = HalPageInfo.assembleWithOffset(5, 100L, 10L);

        assertThat(pageInfo).isNotNull();
        assertThat(pageInfo.size()).isEqualTo(5);
        assertThat(pageInfo.totalElements()).isEqualTo(100L);
        assertThat(pageInfo.totalPages()).isEqualTo(20);
        assertThat(pageInfo.number()).isEqualTo(2);
    }


    @Test
    void givenPageNumber_whenAssembleWithPageNumberWithFourParameters() {
        HalPageInfo pageInfo = HalPageInfo.assembleWithPageNumber(5, 100L, 3);

        assertThat(pageInfo).isNotNull();
        assertThat(pageInfo.size()).isEqualTo(5);
        assertThat(pageInfo.totalElements()).isEqualTo(100L);
        assertThat(pageInfo.totalPages()).isEqualTo(20);
        assertThat(pageInfo.number()).isEqualTo(3);
    }

}
