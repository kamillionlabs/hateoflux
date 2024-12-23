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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MultiRightPairListTest {

    @Test
    void givenEmptyMultiRightPairList_whenAdd_thenElementsAreAdded() {
        MultiRightPairList<String, Integer> multiRightPairList = MultiRightPairList.of();
        multiRightPairList.add("one", List.of(1, 11));
        multiRightPairList.add("two", List.of(2, 22));

        assertThat(multiRightPairList).hasSize(2);
        assertThat(multiRightPairList.getLeft(0)).isEqualTo("one");
        assertThat(multiRightPairList.getRights(0)).containsExactly(1, 11);
        assertThat(multiRightPairList.getLeft(1)).isEqualTo("two");
        assertThat(multiRightPairList.getRights(1)).containsExactly(2, 22);
    }

    @Test
    void givenMultiRightPairListWithElements_whenGetLeftsAndFlattenedRights_thenCorrectListsReturned() {
        MultiRightPairList<String, Integer> multiRightPairList = MultiRightPairList.of();
        multiRightPairList.add("one", List.of(1, 11));
        multiRightPairList.add("two", List.of(2, 22));
        multiRightPairList.add("three", List.of(3, 33));

        List<String> lefts = multiRightPairList.getLefts();
        List<Integer> flattenedRights = multiRightPairList.getFlattenedRights();

        assertThat(lefts).containsExactly("one", "two", "three");
        assertThat(flattenedRights).containsExactly(1, 11, 2, 22, 3, 33);
    }

    @Test
    void givenMultiRightPairList_whenOfMethodCalledWithList_thenMultiRightPairListCreated() {
        List<MultiRightPair<String, Integer>> pairs = List.of(
                MultiRightPair.of("one", List.of(1, 11)),
                MultiRightPair.of("two", List.of(2, 22)),
                MultiRightPair.of("three", List.of(3, 33))
        );
        MultiRightPairList<String, Integer> multiRightPairList = MultiRightPairList.of(pairs);

        assertThat(multiRightPairList).hasSize(3);
        assertThat(multiRightPairList.getLeft(0)).isEqualTo("one");
        assertThat(multiRightPairList.getRights(0)).containsExactly(1, 11);
        assertThat(multiRightPairList.getLeft(1)).isEqualTo("two");
        assertThat(multiRightPairList.getRights(1)).containsExactly(2, 22);
        assertThat(multiRightPairList.getLeft(2)).isEqualTo("three");
        assertThat(multiRightPairList.getRights(2)).containsExactly(3, 33);
    }

    @Test
    void givenMultiValueMap_whenOfMethodCalled_thenMultiRightPairListCreated() {
        MultiValueMap<String, Integer> map = new LinkedMultiValueMap<>();
        map.add("one", 1);
        map.add("one", 11);
        map.add("two", 2);
        map.add("two", 22);
        map.add("three", 3);
        map.add("three", 33);

        MultiRightPairList<String, Integer> multiRightPairList = MultiRightPairList.of(map);

        assertThat(multiRightPairList).hasSize(3);
        assertThat(multiRightPairList.getLefts()).containsExactlyInAnyOrder("one", "two", "three");
        assertThat(multiRightPairList.getFlattenedRights()).containsExactlyInAnyOrder(1, 11, 2, 22, 3, 33);
    }

    @Test
    void givenVarargs_whenOfMethodCalled_thenMultiRightPairListCreated() {
        MultiRightPairList<String, Integer> multiRightPairList = MultiRightPairList.of(
                "one", List.of(1, 11),
                "two", List.of(2, 22),
                "three", List.of(3, 33)
        );

        assertThat(multiRightPairList).hasSize(3);
        assertThat(multiRightPairList.getLeft(0)).isEqualTo("one");
        assertThat(multiRightPairList.getRights(0)).containsExactly(1, 11);
        assertThat(multiRightPairList.getLeft(1)).isEqualTo("two");
        assertThat(multiRightPairList.getRights(1)).containsExactly(2, 22);
        assertThat(multiRightPairList.getLeft(2)).isEqualTo("three");
        assertThat(multiRightPairList.getRights(2)).containsExactly(3, 33);
    }

    @Test
    void givenMultiRightPairListWithElements_whenGetLeft_thenCorrectLeftReturned() {
        MultiRightPairList<String, Integer> multiRightPairList = MultiRightPairList.of(
                "one", List.of(1, 11),
                "two", List.of(2, 22),
                "three", List.of(3, 33)
        );

        assertThat(multiRightPairList.getLeft(0)).isEqualTo("one");
        assertThat(multiRightPairList.getLeft(1)).isEqualTo("two");
        assertThat(multiRightPairList.getLeft(2)).isEqualTo("three");
    }

    @Test
    void givenMultiRightPairListWithElements_whenGetRights_thenCorrectRightsReturned() {
        MultiRightPairList<String, Integer> multiRightPairList = MultiRightPairList.of(
                "one", List.of(1, 11),
                "two", List.of(2, 22),
                "three", List.of(3, 33)
        );

        assertThat(multiRightPairList.getRights(0)).containsExactly(1, 11);
        assertThat(multiRightPairList.getRights(1)).containsExactly(2, 22);
        assertThat(multiRightPairList.getRights(2)).containsExactly(3, 33);
    }

    @Test
    void givenTwoPairs_whenOfMethodCalledWithRightsAsLists_thenMultiRightPairListCreated() {
        MultiRightPairList<String, Integer> multiRightPairList = MultiRightPairList.of(
                "one", List.of(1, 11),
                "two", List.of(2, 22)
        );

        assertThat(multiRightPairList).hasSize(2);
        assertThat(multiRightPairList.getLeft(0)).isEqualTo("one");
        assertThat(multiRightPairList.getRights(0)).containsExactly(1, 11);
        assertThat(multiRightPairList.getLeft(1)).isEqualTo("two");
        assertThat(multiRightPairList.getRights(1)).containsExactly(2, 22);
    }

    @Test
    void givenThreePairs_whenOfMethodCalledWithRightsAsLists_thenMultiRightPairListCreated() {
        MultiRightPairList<String, Integer> multiRightPairList = MultiRightPairList.of(
                "one", List.of(1, 11),
                "two", List.of(2, 22),
                "three", List.of(3, 33)
        );

        assertThat(multiRightPairList).hasSize(3);
        assertThat(multiRightPairList.getLeft(0)).isEqualTo("one");
        assertThat(multiRightPairList.getRights(0)).containsExactly(1, 11);
        assertThat(multiRightPairList.getLeft(1)).isEqualTo("two");
        assertThat(multiRightPairList.getRights(1)).containsExactly(2, 22);
        assertThat(multiRightPairList.getLeft(2)).isEqualTo("three");
        assertThat(multiRightPairList.getRights(2)).containsExactly(3, 33);
    }

    @Test
    void givenFourPairs_whenOfMethodCalledWithRightsAsLists_thenMultiRightPairListCreated() {
        MultiRightPairList<String, Integer> multiRightPairList = MultiRightPairList.of(
                "one", List.of(1, 11),
                "two", List.of(2, 22),
                "three", List.of(3, 33),
                "four", List.of(4, 44)
        );

        assertThat(multiRightPairList).hasSize(4);
        assertThat(multiRightPairList.getLeft(0)).isEqualTo("one");
        assertThat(multiRightPairList.getRights(0)).containsExactly(1, 11);
        assertThat(multiRightPairList.getLeft(1)).isEqualTo("two");
        assertThat(multiRightPairList.getRights(1)).containsExactly(2, 22);
        assertThat(multiRightPairList.getLeft(2)).isEqualTo("three");
        assertThat(multiRightPairList.getRights(2)).containsExactly(3, 33);
        assertThat(multiRightPairList.getLeft(3)).isEqualTo("four");
        assertThat(multiRightPairList.getRights(3)).containsExactly(4, 44);
    }

    @Test
    void givenFivePairs_whenOfMethodCalledWithRightsAsLists_thenMultiRightPairListCreated() {
        MultiRightPairList<String, Integer> multiRightPairList = MultiRightPairList.of(
                "one", List.of(1, 11),
                "two", List.of(2, 22),
                "three", List.of(3, 33),
                "four", List.of(4, 44),
                "five", List.of(5, 55)
        );

        assertThat(multiRightPairList).hasSize(5);
        assertThat(multiRightPairList.getLeft(0)).isEqualTo("one");
        assertThat(multiRightPairList.getRights(0)).containsExactly(1, 11);
        assertThat(multiRightPairList.getLeft(1)).isEqualTo("two");
        assertThat(multiRightPairList.getRights(1)).containsExactly(2, 22);
        assertThat(multiRightPairList.getLeft(2)).isEqualTo("three");
        assertThat(multiRightPairList.getRights(2)).containsExactly(3, 33);
        assertThat(multiRightPairList.getLeft(3)).isEqualTo("four");
        assertThat(multiRightPairList.getRights(3)).containsExactly(4, 44);
        assertThat(multiRightPairList.getLeft(4)).isEqualTo("five");
        assertThat(multiRightPairList.getRights(4)).containsExactly(5, 55);
    }

    @Test
    void givenSixPairs_whenOfMethodCalledWithRightsAsLists_thenMultiRightPairListCreated() {
        MultiRightPairList<String, Integer> multiRightPairList = MultiRightPairList.of(
                "one", List.of(1, 11),
                "two", List.of(2, 22),
                "three", List.of(3, 33),
                "four", List.of(4, 44),
                "five", List.of(5, 55),
                "six", List.of(6, 66)
        );

        assertThat(multiRightPairList).hasSize(6);
        assertThat(multiRightPairList.getLeft(0)).isEqualTo("one");
        assertThat(multiRightPairList.getRights(0)).containsExactly(1, 11);
        assertThat(multiRightPairList.getLeft(1)).isEqualTo("two");
        assertThat(multiRightPairList.getRights(1)).containsExactly(2, 22);
        assertThat(multiRightPairList.getLeft(2)).isEqualTo("three");
        assertThat(multiRightPairList.getRights(2)).containsExactly(3, 33);
        assertThat(multiRightPairList.getLeft(3)).isEqualTo("four");
        assertThat(multiRightPairList.getRights(3)).containsExactly(4, 44);
        assertThat(multiRightPairList.getLeft(4)).isEqualTo("five");
        assertThat(multiRightPairList.getRights(4)).containsExactly(5, 55);
        assertThat(multiRightPairList.getLeft(5)).isEqualTo("six");
        assertThat(multiRightPairList.getRights(5)).containsExactly(6, 66);
    }

    @Test
    void givenSevenPairs_whenOfMethodCalledWithRightsAsLists_thenMultiRightPairListCreated() {
        MultiRightPairList<String, Integer> multiRightPairList = MultiRightPairList.of(
                "one", List.of(1, 11),
                "two", List.of(2, 22),
                "three", List.of(3, 33),
                "four", List.of(4, 44),
                "five", List.of(5, 55),
                "six", List.of(6, 66),
                "seven", List.of(7, 77)
        );

        assertThat(multiRightPairList).hasSize(7);
        assertThat(multiRightPairList.getLeft(0)).isEqualTo("one");
        assertThat(multiRightPairList.getRights(0)).containsExactly(1, 11);
        assertThat(multiRightPairList.getLeft(1)).isEqualTo("two");
        assertThat(multiRightPairList.getRights(1)).containsExactly(2, 22);
        assertThat(multiRightPairList.getLeft(2)).isEqualTo("three");
        assertThat(multiRightPairList.getRights(2)).containsExactly(3, 33);
        assertThat(multiRightPairList.getLeft(3)).isEqualTo("four");
        assertThat(multiRightPairList.getRights(3)).containsExactly(4, 44);
        assertThat(multiRightPairList.getLeft(4)).isEqualTo("five");
        assertThat(multiRightPairList.getRights(4)).containsExactly(5, 55);
        assertThat(multiRightPairList.getLeft(5)).isEqualTo("six");
        assertThat(multiRightPairList.getRights(5)).containsExactly(6, 66);
        assertThat(multiRightPairList.getLeft(6)).isEqualTo("seven");
        assertThat(multiRightPairList.getRights(6)).containsExactly(7, 77);
    }

    @Test
    void givenEightPairs_whenOfMethodCalledWithRightsAsLists_thenMultiRightPairListCreated() {
        MultiRightPairList<String, Integer> multiRightPairList = MultiRightPairList.of(
                "one", List.of(1, 11),
                "two", List.of(2, 22),
                "three", List.of(3, 33),
                "four", List.of(4, 44),
                "five", List.of(5, 55),
                "six", List.of(6, 66),
                "seven", List.of(7, 77),
                "eight", List.of(8, 88)
        );

        assertThat(multiRightPairList).hasSize(8);
        assertThat(multiRightPairList.getLeft(0)).isEqualTo("one");
        assertThat(multiRightPairList.getRights(0)).containsExactly(1, 11);
        assertThat(multiRightPairList.getLeft(1)).isEqualTo("two");
        assertThat(multiRightPairList.getRights(1)).containsExactly(2, 22);
        assertThat(multiRightPairList.getLeft(2)).isEqualTo("three");
        assertThat(multiRightPairList.getRights(2)).containsExactly(3, 33);
        assertThat(multiRightPairList.getLeft(3)).isEqualTo("four");
        assertThat(multiRightPairList.getRights(3)).containsExactly(4, 44);
        assertThat(multiRightPairList.getLeft(4)).isEqualTo("five");
        assertThat(multiRightPairList.getRights(4)).containsExactly(5, 55);
        assertThat(multiRightPairList.getLeft(5)).isEqualTo("six");
        assertThat(multiRightPairList.getRights(5)).containsExactly(6, 66);
        assertThat(multiRightPairList.getLeft(6)).isEqualTo("seven");
        assertThat(multiRightPairList.getRights(6)).containsExactly(7, 77);
        assertThat(multiRightPairList.getLeft(7)).isEqualTo("eight");
        assertThat(multiRightPairList.getRights(7)).containsExactly(8, 88);
    }

    @Test
    void givenNinePairs_whenOfMethodCalledWithRightsAsLists_thenMultiRightPairListCreated() {
        MultiRightPairList<String, Integer> multiRightPairList = MultiRightPairList.of(
                "one", List.of(1, 11),
                "two", List.of(2, 22),
                "three", List.of(3, 33),
                "four", List.of(4, 44),
                "five", List.of(5, 55),
                "six", List.of(6, 66),
                "seven", List.of(7, 77),
                "eight", List.of(8, 88),
                "nine", List.of(9, 99)
        );

        assertThat(multiRightPairList).hasSize(9);
        assertThat(multiRightPairList.getLeft(0)).isEqualTo("one");
        assertThat(multiRightPairList.getRights(0)).containsExactly(1, 11);
        assertThat(multiRightPairList.getLeft(1)).isEqualTo("two");
        assertThat(multiRightPairList.getRights(1)).containsExactly(2, 22);
        assertThat(multiRightPairList.getLeft(2)).isEqualTo("three");
        assertThat(multiRightPairList.getRights(2)).containsExactly(3, 33);
        assertThat(multiRightPairList.getLeft(3)).isEqualTo("four");
        assertThat(multiRightPairList.getRights(3)).containsExactly(4, 44);
        assertThat(multiRightPairList.getLeft(4)).isEqualTo("five");
        assertThat(multiRightPairList.getRights(4)).containsExactly(5, 55);
        assertThat(multiRightPairList.getLeft(5)).isEqualTo("six");
        assertThat(multiRightPairList.getRights(5)).containsExactly(6, 66);
        assertThat(multiRightPairList.getLeft(6)).isEqualTo("seven");
        assertThat(multiRightPairList.getRights(6)).containsExactly(7, 77);
        assertThat(multiRightPairList.getLeft(7)).isEqualTo("eight");
        assertThat(multiRightPairList.getRights(7)).containsExactly(8, 88);
        assertThat(multiRightPairList.getLeft(8)).isEqualTo("nine");
        assertThat(multiRightPairList.getRights(8)).containsExactly(9, 99);
    }

    @Test
    void givenTenPairs_whenOfMethodCalledWithRightsAsLists_thenMultiRightPairListCreated() {
        MultiRightPairList<String, Integer> multiRightPairList = MultiRightPairList.of(
                "one", List.of(1, 11),
                "two", List.of(2, 22),
                "three", List.of(3, 33),
                "four", List.of(4, 44),
                "five", List.of(5, 55),
                "six", List.of(6, 66),
                "seven", List.of(7, 77),
                "eight", List.of(8, 88),
                "nine", List.of(9, 99),
                "ten", List.of(10, 110)
        );

        assertThat(multiRightPairList).hasSize(10);
        assertThat(multiRightPairList.getLeft(0)).isEqualTo("one");
        assertThat(multiRightPairList.getRights(0)).containsExactly(1, 11);
        assertThat(multiRightPairList.getLeft(1)).isEqualTo("two");
        assertThat(multiRightPairList.getRights(1)).containsExactly(2, 22);
        assertThat(multiRightPairList.getLeft(2)).isEqualTo("three");
        assertThat(multiRightPairList.getRights(2)).containsExactly(3, 33);
        assertThat(multiRightPairList.getLeft(3)).isEqualTo("four");
        assertThat(multiRightPairList.getRights(3)).containsExactly(4, 44);
        assertThat(multiRightPairList.getLeft(4)).isEqualTo("five");
        assertThat(multiRightPairList.getRights(4)).containsExactly(5, 55);
        assertThat(multiRightPairList.getLeft(5)).isEqualTo("six");
        assertThat(multiRightPairList.getRights(5)).containsExactly(6, 66);
        assertThat(multiRightPairList.getLeft(6)).isEqualTo("seven");
        assertThat(multiRightPairList.getRights(6)).containsExactly(7, 77);
        assertThat(multiRightPairList.getLeft(7)).isEqualTo("eight");
        assertThat(multiRightPairList.getRights(7)).containsExactly(8, 88);
        assertThat(multiRightPairList.getLeft(8)).isEqualTo("nine");
        assertThat(multiRightPairList.getRights(8)).containsExactly(9, 99);
        assertThat(multiRightPairList.getLeft(9)).isEqualTo("ten");
        assertThat(multiRightPairList.getRights(9)).containsExactly(10, 110);
    }


    @Test
    void givenTwoPairs_whenOfMethodCalledWithSingleRights_thenPairListCreated() {
        MultiRightPairList<String, Integer> pairList = MultiRightPairList.of("one", 1, "two", 2);

        assertThat(pairList).hasSize(2);
        assertThat(pairList.getLeft(0)).isEqualTo("one");
        assertThat(pairList.getRights(0).get(0)).isEqualTo(1);
        assertThat(pairList.getLeft(1)).isEqualTo("two");
        assertThat(pairList.getRights(1).get(0)).isEqualTo(2);
    }

    @Test
    void givenThreePairs_whenOfMethodCalledWithSingleRights_thenPairListCreated() {
        MultiRightPairList<String, Integer> pairList = MultiRightPairList.of("one", 1, "two", 2, "three", 3);

        assertThat(pairList).hasSize(3);
        assertThat(pairList.getLeft(0)).isEqualTo("one");
        assertThat(pairList.getRights(0).get(0)).isEqualTo(1);
        assertThat(pairList.getLeft(1)).isEqualTo("two");
        assertThat(pairList.getRights(1).get(0)).isEqualTo(2);
        assertThat(pairList.getLeft(2)).isEqualTo("three");
        assertThat(pairList.getRights(2).get(0)).isEqualTo(3);
    }

    @Test
    void givenFourPairs_whenOfMethodCalledWithSingleRights_thenPairListCreated() {
        MultiRightPairList<String, Integer> pairList = MultiRightPairList.of("one", 1, "two", 2, "three", 3, "four", 4);

        assertThat(pairList).hasSize(4);
        assertThat(pairList.getLeft(0)).isEqualTo("one");
        assertThat(pairList.getRights(0).get(0)).isEqualTo(1);
        assertThat(pairList.getLeft(1)).isEqualTo("two");
        assertThat(pairList.getRights(1).get(0)).isEqualTo(2);
        assertThat(pairList.getLeft(2)).isEqualTo("three");
        assertThat(pairList.getRights(2).get(0)).isEqualTo(3);
        assertThat(pairList.getLeft(3)).isEqualTo("four");
        assertThat(pairList.getRights(3).get(0)).isEqualTo(4);
    }

    @Test
    void givenFivePairs_whenOfMethodCalledWithSingleRights_thenPairListCreated() {
        MultiRightPairList<String, Integer> pairList = MultiRightPairList.of("one", 1, "two", 2, "three", 3, "four",
                4, "five", 5);

        assertThat(pairList).hasSize(5);
        assertThat(pairList.getLeft(0)).isEqualTo("one");
        assertThat(pairList.getRights(0).get(0)).isEqualTo(1);
        assertThat(pairList.getLeft(1)).isEqualTo("two");
        assertThat(pairList.getRights(1).get(0)).isEqualTo(2);
        assertThat(pairList.getLeft(2)).isEqualTo("three");
        assertThat(pairList.getRights(2).get(0)).isEqualTo(3);
        assertThat(pairList.getLeft(3)).isEqualTo("four");
        assertThat(pairList.getRights(3).get(0)).isEqualTo(4);
        assertThat(pairList.getLeft(4)).isEqualTo("five");
        assertThat(pairList.getRights(4).get(0)).isEqualTo(5);
    }

    @Test
    void givenSixPairs_whenOfMethodCalledWithSingleRights_thenPairListCreated() {
        MultiRightPairList<String, Integer> pairList = MultiRightPairList.of("one", 1, "two", 2, "three", 3, "four",
                4, "five", 5, "six",
                6);

        assertThat(pairList).hasSize(6);
        assertThat(pairList.getLeft(0)).isEqualTo("one");
        assertThat(pairList.getRights(0).get(0)).isEqualTo(1);
        assertThat(pairList.getLeft(1)).isEqualTo("two");
        assertThat(pairList.getRights(1).get(0)).isEqualTo(2);
        assertThat(pairList.getLeft(2)).isEqualTo("three");
        assertThat(pairList.getRights(2).get(0)).isEqualTo(3);
        assertThat(pairList.getLeft(3)).isEqualTo("four");
        assertThat(pairList.getRights(3).get(0)).isEqualTo(4);
        assertThat(pairList.getLeft(4)).isEqualTo("five");
        assertThat(pairList.getRights(4).get(0)).isEqualTo(5);
        assertThat(pairList.getLeft(5)).isEqualTo("six");
        assertThat(pairList.getRights(5).get(0)).isEqualTo(6);
    }

    @Test
    void givenSevenPairs_whenOfMethodCalledWithSingleRights_thenPairListCreated() {
        MultiRightPairList<String, Integer> pairList = MultiRightPairList.of("one", 1, "two", 2, "three", 3, "four",
                4, "five", 5, "six",
                6, "seven", 7);

        assertThat(pairList).hasSize(7);
        assertThat(pairList.getLeft(0)).isEqualTo("one");
        assertThat(pairList.getRights(0).get(0)).isEqualTo(1);
        assertThat(pairList.getLeft(1)).isEqualTo("two");
        assertThat(pairList.getRights(1).get(0)).isEqualTo(2);
        assertThat(pairList.getLeft(2)).isEqualTo("three");
        assertThat(pairList.getRights(2).get(0)).isEqualTo(3);
        assertThat(pairList.getLeft(3)).isEqualTo("four");
        assertThat(pairList.getRights(3).get(0)).isEqualTo(4);
        assertThat(pairList.getLeft(4)).isEqualTo("five");
        assertThat(pairList.getRights(4).get(0)).isEqualTo(5);
        assertThat(pairList.getLeft(5)).isEqualTo("six");
        assertThat(pairList.getRights(5).get(0)).isEqualTo(6);
        assertThat(pairList.getLeft(6)).isEqualTo("seven");
        assertThat(pairList.getRights(6).get(0)).isEqualTo(7);
    }

    @Test
    void givenEightPairs_whenOfMethodCalledWithSingleRights_thenPairListCreated() {
        MultiRightPairList<String, Integer> pairList = MultiRightPairList.of("one", 1, "two", 2, "three", 3, "four",
                4, "five", 5, "six",
                6, "seven", 7, "eight", 8);

        assertThat(pairList).hasSize(8);
        assertThat(pairList.getLeft(0)).isEqualTo("one");
        assertThat(pairList.getRights(0).get(0)).isEqualTo(1);
        assertThat(pairList.getLeft(1)).isEqualTo("two");
        assertThat(pairList.getRights(1).get(0)).isEqualTo(2);
        assertThat(pairList.getLeft(2)).isEqualTo("three");
        assertThat(pairList.getRights(2).get(0)).isEqualTo(3);
        assertThat(pairList.getLeft(3)).isEqualTo("four");
        assertThat(pairList.getRights(3).get(0)).isEqualTo(4);
        assertThat(pairList.getLeft(4)).isEqualTo("five");
        assertThat(pairList.getRights(4).get(0)).isEqualTo(5);
        assertThat(pairList.getLeft(5)).isEqualTo("six");
        assertThat(pairList.getRights(5).get(0)).isEqualTo(6);
        assertThat(pairList.getLeft(6)).isEqualTo("seven");
        assertThat(pairList.getRights(6).get(0)).isEqualTo(7);
        assertThat(pairList.getLeft(7)).isEqualTo("eight");
        assertThat(pairList.getRights(7).get(0)).isEqualTo(8);
    }

    @Test
    void givenNinePairs_whenOfMethodCalledWithSingleRights_thenPairListCreated() {
        MultiRightPairList<String, Integer> pairList = MultiRightPairList.of("one", 1, "two", 2, "three", 3, "four",
                4, "five", 5, "six",
                6, "seven", 7, "eight", 8, "nine", 9);

        assertThat(pairList).hasSize(9);
        assertThat(pairList.getLeft(0)).isEqualTo("one");
        assertThat(pairList.getRights(0).get(0)).isEqualTo(1);
        assertThat(pairList.getLeft(1)).isEqualTo("two");
        assertThat(pairList.getRights(1).get(0)).isEqualTo(2);
        assertThat(pairList.getLeft(2)).isEqualTo("three");
        assertThat(pairList.getRights(2).get(0)).isEqualTo(3);
        assertThat(pairList.getLeft(3)).isEqualTo("four");
        assertThat(pairList.getRights(3).get(0)).isEqualTo(4);
        assertThat(pairList.getLeft(4)).isEqualTo("five");
        assertThat(pairList.getRights(4).get(0)).isEqualTo(5);
        assertThat(pairList.getLeft(5)).isEqualTo("six");
        assertThat(pairList.getRights(5).get(0)).isEqualTo(6);
        assertThat(pairList.getLeft(6)).isEqualTo("seven");
        assertThat(pairList.getRights(6).get(0)).isEqualTo(7);
        assertThat(pairList.getLeft(7)).isEqualTo("eight");
        assertThat(pairList.getRights(7).get(0)).isEqualTo(8);
        assertThat(pairList.getLeft(8)).isEqualTo("nine");
        assertThat(pairList.getRights(8).get(0)).isEqualTo(9);
    }

    @Test
    void givenTenPairs_whenOfMethodCalledWithSingleRights_thenPairListCreated() {
        MultiRightPairList<String, Integer> pairList = MultiRightPairList.of("one", 1, "two", 2, "three", 3, "four",
                4, "five", 5, "six",
                6, "seven", 7, "eight", 8, "nine", 9, "ten", 10);

        assertThat(pairList).hasSize(10);
        assertThat(pairList.getLeft(0)).isEqualTo("one");
        assertThat(pairList.getRights(0).get(0)).isEqualTo(1);
        assertThat(pairList.getLeft(1)).isEqualTo("two");
        assertThat(pairList.getRights(1).get(0)).isEqualTo(2);
        assertThat(pairList.getLeft(2)).isEqualTo("three");
        assertThat(pairList.getRights(2).get(0)).isEqualTo(3);
        assertThat(pairList.getLeft(3)).isEqualTo("four");
        assertThat(pairList.getRights(3).get(0)).isEqualTo(4);
        assertThat(pairList.getLeft(4)).isEqualTo("five");
        assertThat(pairList.getRights(4).get(0)).isEqualTo(5);
        assertThat(pairList.getLeft(5)).isEqualTo("six");
        assertThat(pairList.getRights(5).get(0)).isEqualTo(6);
        assertThat(pairList.getLeft(6)).isEqualTo("seven");
        assertThat(pairList.getRights(6).get(0)).isEqualTo(7);
        assertThat(pairList.getLeft(7)).isEqualTo("eight");
        assertThat(pairList.getRights(7).get(0)).isEqualTo(8);
        assertThat(pairList.getLeft(8)).isEqualTo("nine");
        assertThat(pairList.getRights(8).get(0)).isEqualTo(9);
        assertThat(pairList.getLeft(9)).isEqualTo("ten");
        assertThat(pairList.getRights(9).get(0)).isEqualTo(10);
    }
}
