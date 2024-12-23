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
