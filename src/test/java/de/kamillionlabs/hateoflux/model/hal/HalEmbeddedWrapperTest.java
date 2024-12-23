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

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class HalEmbeddedWrapperTest {

    @Test
    void givenString_whenWrapping_thenThrowsException() {
        assertThatThrownBy(() -> HalEmbeddedWrapper.wrap("hello"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Embedded is not allowed to be of type scalar (e.g. String, int, etc.)");
    }

    @Test
    void givenCharacter_whenWrapping_thenThrowsException() {
        assertThatThrownBy(() -> HalEmbeddedWrapper.wrap('h'))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Embedded is not allowed to be of type scalar (e.g. String, int, etc.)");
    }

    @Test
    void givenPrimitiveInt_whenWrapping_thenThrowsException() {
        assertThatThrownBy(() -> HalEmbeddedWrapper.wrap(3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Embedded is not allowed to be of type scalar (e.g. String, int, etc.)");
    }

    @Test
    void givenBigDecimal_whenWrapping_thenThrowsException() {
        assertThatThrownBy(() -> HalEmbeddedWrapper.wrap(new BigDecimal("4536433.58")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Embedded is not allowed to be of type scalar (e.g. String, int, etc.)");
    }

    @Test
    void givenBoolean_whenWrapping_thenThrowsException() {
        assertThatThrownBy(() -> HalEmbeddedWrapper.wrap(false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Embedded is not allowed to be of type scalar (e.g. String, int, etc.)");
    }
}