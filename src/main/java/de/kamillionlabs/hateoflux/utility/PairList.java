/*
 * Copyright (c)  2024 kamillionlabs contributors
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
 *
 * @since 13.07.2024
 */

package de.kamillionlabs.hateoflux.utility;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static de.kamillionlabs.hateoflux.utility.ValidationMessageTemplates.valueNotAllowedToBeNull;

/**
 * A list implementation that stores pairs of values.
 *
 * @param <LeftT>
 *         the type of the left elements in the pairs
 * @param <RightT>
 *         the type of the right elements in the pairs
 * @author Younes El Ouarti
 * @see Pair
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PairList<LeftT, RightT> extends LinkedList<Pair<LeftT, RightT>> {

    /**
     * Constructs a new {@link PairList} with the specified list of pairs.
     *
     * @param pairs
     *         the list of pairs to initialize the list with
     */
    private PairList(List<Pair<LeftT, RightT>> pairs) {
        addAll(pairs);
    }

    /**
     * Adds a new pair to the list.
     *
     * @param left
     *         the left element of the pair
     * @param right
     *         the right element of the pair
     */
    public void add(LeftT left, RightT right) {
        add(new Pair<>(left, right));
    }

    /**
     * Retrieves the left element of the pair at the specified index.
     *
     * @param i
     *         the index of the pair
     * @return the left element of the pair at the specified index
     */
    public LeftT getLeft(int i) {
        return get(i).left();
    }

    /**
     * Retrieves the right element of the pair at the specified index.
     *
     * @param i
     *         the index of the pair
     * @return the right element of the pair at the specified index
     */
    public RightT getRight(int i) {
        return get(i).right();
    }

    /**
     * Returns a list of all left elements in the pairs.
     *
     * @return a list containing all left elements
     */
    public List<LeftT> getLefts() {
        return this.stream().map(Pair::left).toList();
    }

    /**
     * Returns a list of all right elements in the pairs.
     *
     * @return a list containing all right elements
     */
    public List<RightT> getRights() {
        return this.stream().map(Pair::right).toList();
    }

    /**
     * Creates an empty {@link PairList}.
     *
     * @param <LeftT>
     *         the type of the left elements in the pairs
     * @param <RightT>
     *         the type of the right elements in the pairs
     * @return a new empty {@link PairList}
     */
    public static <LeftT, RightT> PairList<LeftT, RightT> of() {
        return new PairList<>();
    }

    /**
     * Creates a {@link PairList} from a list of pairs.
     *
     * @param pairs
     *         the list of pairs
     * @param <LeftT>
     *         the type of the left elements in the pairs
     * @param <RightT>
     *         the type of the right elements in the pairs
     * @return a new {@link PairList} containing the specified pairs
     */
    public static <LeftT, RightT> PairList<LeftT, RightT> of(List<Pair<LeftT, RightT>> pairs) {
        return new PairList<>(pairs);
    }

    /**
     * Creates a {@link PairList} from two lists of left and right elements.
     *
     * @param lefts
     *         the list of left elements
     * @param rights
     *         the list of right elements
     * @param <LeftT>
     *         the type of the left elements in the pairs
     * @param <RightT>
     *         the type of the right elements in the pairs
     * @return a new {@link PairList} containing pairs formed from the given lists
     *
     * @throws IllegalArgumentException
     *         if the lists are null or of different sizes
     */
    public static <LeftT, RightT> PairList<LeftT, RightT> of(List<LeftT> lefts, List<RightT> rights) {
        Assert.notNull(lefts, valueNotAllowedToBeNull("lefts"));
        Assert.notNull(rights, valueNotAllowedToBeNull("rights"));
        Assert.isTrue(lefts.size() == rights.size(), "Different sizes in lefts and rights are not allowed");

        List<Pair<LeftT, RightT>> pairs = new LinkedList<>();
        for (int i = 0; i < lefts.size(); i++) {
            pairs.add(new Pair<>(lefts.get(i), rights.get(i)));
        }
        return new PairList<>(pairs);
    }

    /**
     * Creates a {@link PairList} from a map of left and right elements.
     *
     * @param pairs
     *         the map containing pairs of left and right elements
     * @param <LeftT>
     *         the type of the left elements in the pairs
     * @param <RightT>
     *         the type of the right elements in the pairs
     * @return a new {@link PairList} containing pairs from the map
     */
    public static <LeftT, RightT> PairList<LeftT, RightT> of(Map<LeftT, RightT> pairs) {
        return new PairList<>(pairs.entrySet().stream()
                .map(e -> Pair.of(e.getKey(), e.getValue()))
                .toList());
    }

    /**
     * Creates a {@link PairList} with a single pair.
     *
     * @param l1
     *         the left element of the first pair
     * @param r1
     *         the right element of the first pair
     * @param <LeftT>
     *         the type of the left elements in the pairs
     * @param <RightT>
     *         the type of the right elements in the pairs
     * @return a new {@link PairList} containing the specified pair
     */
    public static <LeftT, RightT> PairList<LeftT, RightT> of(LeftT l1, RightT r1) {
        List<Pair<LeftT, RightT>> pairs = new LinkedList<>();
        pairs.add(Pair.of(l1, r1));
        return new PairList<>(pairs);
    }

    /**
     * Creates a {@link PairList} with two pairs.
     *
     * @param l1
     *         the left element of the first pair
     * @param r1
     *         the right element of the first pair
     * @param l2
     *         the left element of the second pair
     * @param r2
     *         the right element of the second pair
     * @param <LeftT>
     *         the type of the left elements in the pairs
     * @param <RightT>
     *         the type of the right elements in the pairs
     * @return a new {@link PairList} containing the specified pairs
     */
    public static <LeftT, RightT> PairList<LeftT, RightT> of(LeftT l1, RightT r1,
                                                             LeftT l2, RightT r2) {
        var pairs = PairList.of(l1, r1);
        pairs.add(l2, r2);
        return pairs;
    }

    /**
     * Creates a {@link PairList} with three pairs.
     *
     * @param l1
     *         the left element of the first pair
     * @param r1
     *         the right element of the first pair
     * @param l2
     *         the left element of the second pair
     * @param r2
     *         the right element of the second pair
     * @param l3
     *         the left element of the third pair
     * @param r3
     *         the right element of the third pair
     * @param <LeftT>
     *         the type of the left elements in the pairs
     * @param <RightT>
     *         the type of the right elements in the pairs
     * @return a new {@link PairList} containing the specified pairs
     */
    public static <LeftT, RightT> PairList<LeftT, RightT> of(LeftT l1, RightT r1,
                                                             LeftT l2, RightT r2,
                                                             LeftT l3, RightT r3) {
        var pairs = PairList.of(l1, r1, l2, r2);
        pairs.add(l3, r3);
        return pairs;
    }

    /**
     * Creates a {@link PairList} with four pairs.
     *
     * @param l1
     *         the left element of the first pair
     * @param r1
     *         the right element of the first pair
     * @param l2
     *         the left element of the second pair
     * @param r2
     *         the right element of the second pair
     * @param l3
     *         the left element of the third pair
     * @param r3
     *         the right element of the third pair
     * @param l4
     *         the left element of the fourth pair
     * @param r4
     *         the right element of the fourth pair
     * @param <LeftT>
     *         the type of the left elements in the pairs
     * @param <RightT>
     *         the type of the right elements in the pairs
     * @return a new {@link PairList} containing the specified pairs
     */
    public static <LeftT, RightT> PairList<LeftT, RightT> of(LeftT l1, RightT r1,
                                                             LeftT l2, RightT r2,
                                                             LeftT l3, RightT r3,
                                                             LeftT l4, RightT r4) {
        var pairs = PairList.of(l1, r1, l2, r2, l3, r3);
        pairs.add(l4, r4);
        return pairs;
    }

    /**
     * Creates a {@link PairList} with five pairs.
     *
     * @param l1
     *         the left element of the first pair
     * @param r1
     *         the right element of the first pair
     * @param l2
     *         the left element of the second pair
     * @param r2
     *         the right element of the second pair
     * @param l3
     *         the left element of the third pair
     * @param r3
     *         the right element of the third pair
     * @param l4
     *         the left element of the fourth pair
     * @param r4
     *         the right element of the fourth pair
     * @param l5
     *         the left element of the fifth pair
     * @param r5
     *         the right element of the fifth pair
     * @param <LeftT>
     *         the type of the left elements in the pairs
     * @param <RightT>
     *         the type of the right elements in the pairs
     * @return a new {@link PairList} containing the specified pairs
     */
    public static <LeftT, RightT> PairList<LeftT, RightT> of(LeftT l1, RightT r1,
                                                             LeftT l2, RightT r2,
                                                             LeftT l3, RightT r3,
                                                             LeftT l4, RightT r4,
                                                             LeftT l5, RightT r5) {
        var pairs = PairList.of(l1, r1, l2, r2, l3, r3, l4, r4);
        pairs.add(l5, r5);
        return pairs;
    }

    /**
     * Creates a {@link PairList} with six pairs.
     *
     * @param l1
     *         the left element of the first pair
     * @param r1
     *         the right element of the first pair
     * @param l2
     *         the left element of the second pair
     * @param r2
     *         the right element of the second pair
     * @param l3
     *         the left element of the third pair
     * @param r3
     *         the right element of the third pair
     * @param l4
     *         the left element of the fourth pair
     * @param r4
     *         the right element of the fourth pair
     * @param l5
     *         the left element of the fifth pair
     * @param r5
     *         the right element of the fifth pair
     * @param l6
     *         the left element of the sixth pair
     * @param r6
     *         the right element of the sixth pair
     * @param <LeftT>
     *         the type of the left elements in the pairs
     * @param <RightT>
     *         the type of the right elements in the pairs
     * @return a new {@link PairList} containing the specified pairs
     */
    public static <LeftT, RightT> PairList<LeftT, RightT> of(LeftT l1, RightT r1,
                                                             LeftT l2, RightT r2,
                                                             LeftT l3, RightT r3,
                                                             LeftT l4, RightT r4,
                                                             LeftT l5, RightT r5,
                                                             LeftT l6, RightT r6) {
        var pairs = PairList.of(l1, r1, l2, r2, l3, r3, l4, r4, l5, r5);
        pairs.add(l6, r6);
        return pairs;
    }

    /**
     * Creates a {@link PairList} with seven pairs.
     *
     * @param l1
     *         the left element of the first pair
     * @param r1
     *         the right element of the first pair
     * @param l2
     *         the left element of the second pair
     * @param r2
     *         the right element of the second pair
     * @param l3
     *         the left element of the third pair
     * @param r3
     *         the right element of the third pair
     * @param l4
     *         the left element of the fourth pair
     * @param r4
     *         the right element of the fourth pair
     * @param l5
     *         the left element of the fifth pair
     * @param r5
     *         the right element of the fifth pair
     * @param l6
     *         the left element of the sixth pair
     * @param r6
     *         the right element of the sixth pair
     * @param l7
     *         the left element of the seventh pair
     * @param r7
     *         the right element of the seventh pair
     * @param <LeftT>
     *         the type of the left elements in the pairs
     * @param <RightT>
     *         the type of the right elements in the pairs
     * @return a new {@link PairList} containing the specified pairs
     */
    public static <LeftT, RightT> PairList<LeftT, RightT> of(LeftT l1, RightT r1,
                                                             LeftT l2, RightT r2,
                                                             LeftT l3, RightT r3,
                                                             LeftT l4, RightT r4,
                                                             LeftT l5, RightT r5,
                                                             LeftT l6, RightT r6,
                                                             LeftT l7, RightT r7) {
        var pairs = PairList.of(l1, r1, l2, r2, l3, r3, l4, r4, l5, r5, l6, r6);
        pairs.add(l7, r7);
        return pairs;
    }

    /**
     * Creates a {@link PairList} with eight pairs.
     *
     * @param l1
     *         the left element of the first pair
     * @param r1
     *         the right element of the first pair
     * @param l2
     *         the left element of the second pair
     * @param r2
     *         the right element of the second pair
     * @param l3
     *         the left element of the third pair
     * @param r3
     *         the right element of the third pair
     * @param l4
     *         the left element of the fourth pair
     * @param r4
     *         the right element of the fourth pair
     * @param l5
     *         the left element of the fifth pair
     * @param r5
     *         the right element of the fifth pair
     * @param l6
     *         the left element of the sixth pair
     * @param r6
     *         the right element of the sixth pair
     * @param l7
     *         the left element of the seventh pair
     * @param r7
     *         the right element of the seventh pair
     * @param l8
     *         the left element of the eighth pair
     * @param r8
     *         the right element of the eighth pair
     * @param <LeftT>
     *         the type of the left elements in the pairs
     * @param <RightT>
     *         the type of the right elements in the pairs
     * @return a new {@link PairList} containing the specified pairs
     */
    public static <LeftT, RightT> PairList<LeftT, RightT> of(LeftT l1, RightT r1,
                                                             LeftT l2, RightT r2,
                                                             LeftT l3, RightT r3,
                                                             LeftT l4, RightT r4,
                                                             LeftT l5, RightT r5,
                                                             LeftT l6, RightT r6,
                                                             LeftT l7, RightT r7,
                                                             LeftT l8, RightT r8) {
        var pairs = PairList.of(l1, r1, l2, r2, l3, r3, l4, r4, l5, r5, l6, r6, l7, r7);
        pairs.add(l8, r8);
        return pairs;
    }

    /**
     * Creates a {@link PairList} with nine pairs.
     *
     * @param l1
     *         the left element of the first pair
     * @param r1
     *         the right element of the first pair
     * @param l2
     *         the left element of the second pair
     * @param r2
     *         the right element of the second pair
     * @param l3
     *         the left element of the third pair
     * @param r3
     *         the right element of the third pair
     * @param l4
     *         the left element of the fourth pair
     * @param r4
     *         the right element of the fourth pair
     * @param l5
     *         the left element of the fifth pair
     * @param r5
     *         the right element of the fifth pair
     * @param l6
     *         the left element of the sixth pair
     * @param r6
     *         the right element of the sixth pair
     * @param l7
     *         the left element of the seventh pair
     * @param r7
     *         the right element of the seventh pair
     * @param l8
     *         the left element of the eighth pair
     * @param r8
     *         the right element of the eighth pair
     * @param l9
     *         the left element of the ninth pair
     * @param r9
     *         the right element of the ninth pair
     * @param <LeftT>
     *         the type of the left elements in the pairs
     * @param <RightT>
     *         the type of the right elements in the pairs
     * @return a new {@link PairList} containing the specified pairs
     */
    public static <LeftT, RightT> PairList<LeftT, RightT> of(LeftT l1, RightT r1,
                                                             LeftT l2, RightT r2,
                                                             LeftT l3, RightT r3,
                                                             LeftT l4, RightT r4,
                                                             LeftT l5, RightT r5,
                                                             LeftT l6, RightT r6,
                                                             LeftT l7, RightT r7,
                                                             LeftT l8, RightT r8,
                                                             LeftT l9, RightT r9) {
        var pairs = PairList.of(l1, r1, l2, r2, l3, r3, l4, r4, l5, r5, l6, r6, l7, r7, l8, r8);
        pairs.add(l9, r9);
        return pairs;
    }

    /**
     * Creates a {@link PairList} with ten pairs.
     *
     * @param l1
     *         the left element of the first pair
     * @param r1
     *         the right element of the first pair
     * @param l2
     *         the left element of the second pair
     * @param r2
     *         the right element of the second pair
     * @param l3
     *         the left element of the third pair
     * @param r3
     *         the right element of the third pair
     * @param l4
     *         the left element of the fourth pair
     * @param r4
     *         the right element of the fourth pair
     * @param l5
     *         the left element of the fifth pair
     * @param r5
     *         the right element of the fifth pair
     * @param l6
     *         the left element of the sixth pair
     * @param r6
     *         the right element of the sixth pair
     * @param l7
     *         the left element of the seventh pair
     * @param r7
     *         the right element of the seventh pair
     * @param l8
     *         the left element of the eighth pair
     * @param r8
     *         the right element of the eighth pair
     * @param l9
     *         the left element of the ninth pair
     * @param r9
     *         the right element of the ninth pair
     * @param l10
     *         the left element of the tenth pair
     * @param r10
     *         the right element of the tenth pair
     * @param <LeftT>
     *         the type of the left elements in the pairs
     * @param <RightT>
     *         the type of the right elements in the pairs
     * @return a new {@link PairList} containing the specified pairs
     */
    public static <LeftT, RightT> PairList<LeftT, RightT> of(LeftT l1, RightT r1,
                                                             LeftT l2, RightT r2,
                                                             LeftT l3, RightT r3,
                                                             LeftT l4, RightT r4,
                                                             LeftT l5, RightT r5,
                                                             LeftT l6, RightT r6,
                                                             LeftT l7, RightT r7,
                                                             LeftT l8, RightT r8,
                                                             LeftT l9, RightT r9,
                                                             LeftT l10, RightT r10) {
        var pairs = PairList.of(l1, r1, l2, r2, l3, r3, l4, r4, l5, r5, l6, r6, l7, r7, l8, r8, l9, r9);
        pairs.add(l10, r10);
        return pairs;
    }
}

