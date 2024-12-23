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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MultiRightPairTest {

    @Test
    void givenLeftAndRightsList_whenOfMethodCalled_thenMultiRightPairCreated() {
        String left = "one";
        List<Integer> rights = List.of(1, 11, 111);

        MultiRightPair<String, Integer> pair = MultiRightPair.of(left, rights);

        assertThat(pair.left()).isEqualTo(left);
        assertThat(pair.rights()).containsExactly(1, 11, 111);
    }

    @Test
    void givenLeftAndSingleRight_whenOfMethodCalled_thenMultiRightPairCreatedWithSingleRight() {
        String left = "two";
        Integer right = 2;

        MultiRightPair<String, Integer> pair = MultiRightPair.of(left, right);

        assertThat(pair.left()).isEqualTo(left);
        assertThat(pair.rights()).containsExactly(right);
    }

    @Test
    void givenPair_whenOfMethodCalled_thenMultiRightPairCreatedFromPair() {
        Pair<String, Integer> pair = Pair.of("three", 3);

        MultiRightPair<String, Integer> multiRightPair = MultiRightPair.of(pair);

        assertThat(multiRightPair.left()).isEqualTo("three");
        assertThat(multiRightPair.rights()).containsExactly(3);
    }

    @Test
    void givenValidMultiRightPair_whenIsEmpty_thenReturnsFalse() {
        MultiRightPair<String, Integer> pair = MultiRightPair.of("four", List.of(4, 44));

        assertThat(pair.isEmpty()).isFalse();
    }

    @Test
    void givenEmptyMultiRightPair_whenIsEmpty_thenReturnsTrue() {
        MultiRightPair<String, Integer> pair = MultiRightPair.of(null, null);

        assertThat(pair.isEmpty()).isTrue();
    }

    @Test
    void givenMultiRightPairWithNullLeft_whenIsEmpty_thenDependsOnRights() {
        MultiRightPair<String, Integer> pairWithNullLeftAndRights = MultiRightPair.of(null, null);
        MultiRightPair<String, Integer> pairWithNullLeftButRights = MultiRightPair.of(null, List.of(5));

        assertThat(pairWithNullLeftAndRights.isEmpty()).isTrue();
        assertThat(pairWithNullLeftButRights.isEmpty()).isFalse();
    }

    @Test
    void givenEmptyStaticMethod_whenCalled_thenEmptyPairCreated() {
        Pair<String, Integer> emptyPair = MultiRightPair.empty();

        assertThat(emptyPair.left()).isNull();
        assertThat(emptyPair.right()).isNull();
    }


    @Test
    void givenNullLeft_whenOfMethodCalledWithList_thenPairCreated() {
        List<Integer> rights = List.of(6, 66);

        MultiRightPair<String, Integer> pair = MultiRightPair.of(null, rights);

        assertThat(pair.left()).isNull();
        assertThat(pair.rights()).containsExactly(6, 66);
    }


    @Test
    void givenNullLeftAndRights_whenOfMethodCalled_thenPairCreated() {
        MultiRightPair<String, Integer> pair = MultiRightPair.of(null, null);

        assertThat(pair.left()).isNull();
        assertThat(pair.rights()).isNull();
    }


    @Test
    void givenMultipleMultiRightPairs_whenEqualsAndHashCode_thenCorrectBehavior() {
        MultiRightPair<String, Integer> pair1 = MultiRightPair.of("nine", List.of(9, 99));
        MultiRightPair<String, Integer> pair2 = MultiRightPair.of("nine", List.of(9, 99));
        MultiRightPair<String, Integer> pair3 = MultiRightPair.of("ten", List.of(10, 100));

        assertThat(pair1).isEqualTo(pair2);
        assertThat(pair1.hashCode()).isEqualTo(pair2.hashCode());
        assertThat(pair1).isNotEqualTo(pair3);
    }

    @Test
    void givenMultiRightPair_whenToStringCalled_thenReturnsExpectedString() {
        MultiRightPair<String, Integer> pair = MultiRightPair.of("eleven", List.of(11, 111));

        String expected = "MultiRightPair[left=eleven, rights=[11, 111]]";
        assertThat(pair.toString()).isEqualTo(expected);
    }
}
