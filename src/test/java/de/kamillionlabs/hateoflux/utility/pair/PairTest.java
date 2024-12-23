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

import static org.assertj.core.api.Assertions.assertThat;

public class PairTest {

    @Test
    void givenLeftAndRight_whenOfMethodCalled_thenPairCreated() {
        String left = "alpha";
        Integer right = 1;

        Pair<String, Integer> pair = Pair.of(left, right);

        assertThat(pair.left()).isEqualTo(left);
        assertThat(pair.right()).isEqualTo(right);
    }

    @Test
    void givenLeftAndRight_whenPairCreated_thenFieldsAreCorrect() {
        String left = "beta";
        Integer right = 2;

        Pair<String, Integer> pair = new Pair<>(left, right);

        assertThat(pair.left()).isEqualTo(left);
        assertThat(pair.right()).isEqualTo(right);
    }

    @Test
    void givenEmptyPair_whenIsEmpty_thenReturnsTrue() {
        Pair<String, Integer> emptyPair = Pair.empty();

        assertThat(emptyPair.isEmpty()).isTrue();
    }

    @Test
    void givenNonEmptyPair_whenIsEmpty_thenReturnsFalse() {
        Pair<String, Integer> pair = Pair.of("gamma", 3);

        assertThat(pair.isEmpty()).isFalse();
    }

    @Test
    void givenPairWithNullLeft_whenIsEmpty_thenReturnsFalse() {
        Pair<String, Integer> pair = Pair.of(null, 4);

        assertThat(pair.isEmpty()).isFalse();
    }

    @Test
    void givenPairWithNullRight_whenIsEmpty_thenReturnsFalse() {
        Pair<String, Integer> pair = Pair.of("delta", null);

        assertThat(pair.isEmpty()).isFalse();
    }

    @Test
    void givenPairWithNullLeftAndRight_whenIsEmpty_thenReturnsTrue() {
        Pair<String, Integer> pair = Pair.of(null, null);

        assertThat(pair.isEmpty()).isTrue();
    }

    @Test
    void givenEmptyStaticMethod_whenCalled_thenEmptyPairCreated() {
        Pair<String, Integer> emptyPair = Pair.empty();

        assertThat(emptyPair.left()).isNull();
        assertThat(emptyPair.right()).isNull();
        assertThat(emptyPair.isEmpty()).isTrue();
    }

    @Test
    void givenPair_whenEqualsAndHashCode_thenCorrectBehavior() {
        Pair<String, Integer> pair1 = Pair.of("epsilon", 5);
        Pair<String, Integer> pair2 = Pair.of("epsilon", 5);
        Pair<String, Integer> pair3 = Pair.of("zeta", 6);

        assertThat(pair1).isEqualTo(pair2);
        assertThat(pair1.hashCode()).isEqualTo(pair2.hashCode());
        assertThat(pair1).isNotEqualTo(pair3);
    }

    @Test
    void givenPair_whenToStringCalled_thenReturnsExpectedString() {
        Pair<String, Integer> pair = Pair.of("eta", 7);

        String expected = "Pair[left=eta, right=7]";
        assertThat(pair.toString()).isEqualTo(expected);
    }
}
