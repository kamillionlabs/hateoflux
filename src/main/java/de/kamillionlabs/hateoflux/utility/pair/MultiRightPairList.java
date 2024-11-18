/*
 * Copyright (c)  2024 kamillion-suite contributors
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
 * @since 12.11.2024
 */
/*
 * Copyright (c) 2024 kamillion contributors
 *
 * This work is licensed under the GNU General Public License (GPL).
 *
 * @since 14.10.2024
 */

package de.kamillionlabs.hateoflux.utility.pair;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.util.MultiValueMap;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static de.kamillionlabs.hateoflux.utility.pair.MultiRightPairListCollector.toMultiRightPairList;

/**
 * Utility class that associates multiple {@code RightT} values with a single {@code LeftT} value. It serves as a
 * shorthand for {@code List<MultiRightPair<LeftT, RightT>>}.
 * <p>
 * A key difference from {@link MultiValueMap} is that this class maintains a list of pairs rather than a map. This
 * distinction emphasizes the semantic difference between "pairs" and "key/value" relationships that this class aims to
 * convey.
 * <p>
 * Unlike a map, {@code MultiRightPairList} also allows duplicate {@code LeftT} entries, enabling multiple
 * associations of the same {@code LeftT} with different lists of {@code RightT} values.</p>
 *
 * @param <LeftT>
 *         the type of the left elements in the pairs
 * @param <RightT>
 *         the type of the right elements in the pairs
 * @author Younes El Ouarti
 * @see MultiValueMap
 * @see MultiRightPair
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class MultiRightPairList<LeftT, RightT> extends LinkedList<MultiRightPair<LeftT, RightT>> {

    /**
     * Constructs a new {@link MultiRightPairList} with the specified list of pairs.
     *
     * @param pairs
     *         the list of pairs to initialize the list with
     */
    private MultiRightPairList(List<MultiRightPair<LeftT, RightT>> pairs) {
        addAll(pairs);
    }

    /**
     * Converts this {@link MultiRightPairList} instance to a {@link MultiRightPairFlux} instance,
     * allowing the pairs in this list to be used in a reactive stream.
     *
     * <p>This method creates a new {@link MultiRightPairFlux} that emits each pair contained in
     * this {@link MultiRightPairList}.</p>
     *
     * @return a {@link MultiRightPairFlux} instance containing the same pairs as this {@link MultiRightPairList}
     */
    public MultiRightPairFlux<LeftT, RightT> toMultiRightPairFlux() {
        return MultiRightPairFlux.fromIterable(this);
    }

    /**
     * Adds a new pair to the list.
     *
     * @param left
     *         the left element of the pair
     * @param rights
     *         the right elements of the pair
     * @return {@code true} (as specified by {@link Collection#add})
     */
    public boolean add(LeftT left, List<RightT> rights) {
        return add(new MultiRightPair<>(left, rights));
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
     * Retrieves the right elements of the pair at the specified index.
     *
     * @param i
     *         the index of the pair
     * @return the right elements of the pair at the specified index
     */
    public List<RightT> getRights(int i) {
        return get(i).rights();
    }

    /**
     * Returns a list of all left elements in the pairs.
     *
     * @return a list containing all left elements
     */
    public List<LeftT> getLefts() {
        return this.stream().map(MultiRightPair::left).toList();
    }

    /**
     * Returns a flattened list of all right elements in the pairs.
     *
     * @return a list containing all right elements
     */
    public List<RightT> getFlattenedRights() {
        return this.stream().flatMap(mrp -> mrp.rights().stream()).toList();
    }

    /**
     * Creates an empty {@link MultiRightPairList}.
     *
     * @param <LeftT>
     *         the type of the left elements in the pairs
     * @param <RightT>
     *         the type of the right elements in the pairs
     * @return a new empty {@link MultiRightPairList}
     */
    public static <LeftT, RightT> MultiRightPairList<LeftT, RightT> of() {
        return new MultiRightPairList<>();
    }

    /**
     * Creates a {@link MultiRightPairList} from a list of {@link MultiRightPair}s.
     *
     * @param pairs
     *         the list of pairs
     * @param <LeftT>
     *         the type of the left elements in the pairs
     * @param <RightT>
     *         the type of the right elements in the pairs
     * @return a new {@link MultiRightPairList} containing the specified pairs
     */
    public static <LeftT, RightT> MultiRightPairList<LeftT, RightT> of(List<MultiRightPair<LeftT, RightT>> pairs) {
        return new MultiRightPairList<>(pairs);
    }


    /**
     * Creates a {@link MultiRightPairList} from a {@link MultiValueMap}.
     *
     * @param map
     *         the map containing left and right elements
     * @param <LeftT>
     *         the type of the left elements in the map
     * @param <RightT>
     *         the type of the right elements in the map
     * @return a new {@link MultiRightPairList} containing values from the map
     */
    public static <LeftT, RightT> MultiRightPairList<LeftT, RightT> of(MultiValueMap<LeftT, RightT> map) {
        return map.entrySet().stream()
                .map(e -> MultiRightPair.of(e.getKey(), e.getValue()))
                .collect(toMultiRightPairList());
    }

    /**
     * Creates a {@link MultiRightPairList} with a single pair.
     *
     * @param l1
     *         the left element of the first pair
     * @param r1
     *         the right elements of the first pair
     * @param <LeftT>
     *         the type of the left elements in the pairs
     * @param <RightT>
     *         the type of the right elements in the pairs
     * @return a new {@link MultiRightPairList} containing the specified pair
     */
    public static <LeftT, RightT> MultiRightPairList<LeftT, RightT> of(LeftT l1, List<RightT> r1) {
        List<MultiRightPair<LeftT, RightT>> pairs = new LinkedList<>();
        pairs.add(MultiRightPair.of(l1, r1));
        return new MultiRightPairList<>(pairs);
    }

    /**
     * Creates a {@link MultiRightPairList} with two pairs.
     *
     * @param l1
     *         the left element of the first pair
     * @param r1
     *         the right elements of the first pair
     * @param l2
     *         the left element of the second pair
     * @param r2
     *         the right elements of the second pair
     * @param <LeftT>
     *         the type of the left elements in the pairs
     * @param <RightT>
     *         the type of the right elements in the pairs
     * @return a new {@link MultiRightPairList} containing the specified pairs
     */
    public static <LeftT, RightT> MultiRightPairList<LeftT, RightT> of(LeftT l1, List<RightT> r1,
                                                                       LeftT l2, List<RightT> r2) {
        var pairs = MultiRightPairList.of(l1, r1);
        pairs.add(l2, r2);
        return pairs;
    }

    /**
     * Creates a {@link MultiRightPairList} with three pairs.
     *
     * @param l1
     *         the left element of the first pair
     * @param r1
     *         the right elements of the first pair
     * @param l2
     *         the left element of the second pair
     * @param r2
     *         the right elements of the second pair
     * @param l3
     *         the left element of the third pair
     * @param r3
     *         the right elements of the third pair
     * @param <LeftT>
     *         the type of the left elements in the pairs
     * @param <RightT>
     *         the type of the right elements in the pairs
     * @return a new {@link MultiRightPairList} containing the specified pairs
     */
    public static <LeftT, RightT> MultiRightPairList<LeftT, RightT> of(LeftT l1, List<RightT> r1,
                                                                       LeftT l2, List<RightT> r2,
                                                                       LeftT l3, List<RightT> r3) {
        var pairs = MultiRightPairList.of(l1, r1, l2, r2);
        pairs.add(l3, r3);
        return pairs;
    }

    /**
     * Creates a {@link MultiRightPairList} with four pairs.
     *
     * @param l1
     *         the left element of the first pair
     * @param r1
     *         the right elements of the first pair
     * @param l2
     *         the left element of the second pair
     * @param r2
     *         the right elements of the second pair
     * @param l3
     *         the left element of the third pair
     * @param r3
     *         the right elements of the third pair
     * @param l4
     *         the left element of the fourth pair
     * @param r4
     *         the right elements of the fourth pair
     * @param <LeftT>
     *         the type of the left elements in the pairs
     * @param <RightT>
     *         the type of the right elements in the pairs
     * @return a new {@link MultiRightPairList} containing the specified pairs
     */
    public static <LeftT, RightT> MultiRightPairList<LeftT, RightT> of(LeftT l1, List<RightT> r1,
                                                                       LeftT l2, List<RightT> r2,
                                                                       LeftT l3, List<RightT> r3,
                                                                       LeftT l4, List<RightT> r4) {
        var pairs = MultiRightPairList.of(l1, r1, l2, r2, l3, r3);
        pairs.add(l4, r4);
        return pairs;
    }

    /**
     * Creates a {@link MultiRightPairList} with five pairs.
     *
     * @param l1
     *         the left element of the first pair
     * @param r1
     *         the right elements of the first pair
     * @param l2
     *         the left element of the second pair
     * @param r2
     *         the right elements of the second pair
     * @param l3
     *         the left element of the third pair
     * @param r3
     *         the right elements of the third pair
     * @param l4
     *         the left element of the fourth pair
     * @param r4
     *         the right elements of the fourth pair
     * @param l5
     *         the left element of the fifth pair
     * @param r5
     *         the right elements of the fifth pair
     * @param <LeftT>
     *         the type of the left elements in the pairs
     * @param <RightT>
     *         the type of the right elements in the pairs
     * @return a new {@link MultiRightPairList} containing the specified pairs
     */
    public static <LeftT, RightT> MultiRightPairList<LeftT, RightT> of(LeftT l1, List<RightT> r1,
                                                                       LeftT l2, List<RightT> r2,
                                                                       LeftT l3, List<RightT> r3,
                                                                       LeftT l4, List<RightT> r4,
                                                                       LeftT l5, List<RightT> r5) {
        var pairs = MultiRightPairList.of(l1, r1, l2, r2, l3, r3, l4, r4);
        pairs.add(l5, r5);
        return pairs;
    }

    /**
     * Creates a {@link MultiRightPairList} with six pairs.
     *
     * @param l1
     *         the left element of the first pair
     * @param r1
     *         the right elements of the first pair
     * @param l2
     *         the left element of the second pair
     * @param r2
     *         the right elements of the second pair
     * @param l3
     *         the left element of the third pair
     * @param r3
     *         the right elements of the third pair
     * @param l4
     *         the left element of the fourth pair
     * @param r4
     *         the right elements of the fourth pair
     * @param l5
     *         the left element of the fifth pair
     * @param r5
     *         the right elements of the fifth pair
     * @param l6
     *         the left element of the sixth pair
     * @param r6
     *         the right elements of the sixth pair
     * @param <LeftT>
     *         the type of the left elements in the pairs
     * @param <RightT>
     *         the type of the right elements in the pairs
     * @return a new {@link MultiRightPairList} containing the specified pairs
     */
    public static <LeftT, RightT> MultiRightPairList<LeftT, RightT> of(LeftT l1, List<RightT> r1,
                                                                       LeftT l2, List<RightT> r2,
                                                                       LeftT l3, List<RightT> r3,
                                                                       LeftT l4, List<RightT> r4,
                                                                       LeftT l5, List<RightT> r5,
                                                                       LeftT l6, List<RightT> r6) {
        var pairs = MultiRightPairList.of(l1, r1, l2, r2, l3, r3, l4, r4, l5, r5);
        pairs.add(l6, r6);
        return pairs;
    }

    /**
     * Creates a {@link MultiRightPairList} with seven pairs.
     *
     * @param l1
     *         the left element of the first pair
     * @param r1
     *         the right elements of the first pair
     * @param l2
     *         the left element of the second pair
     * @param r2
     *         the right elements of the second pair
     * @param l3
     *         the left element of the third pair
     * @param r3
     *         the right elements of the third pair
     * @param l4
     *         the left element of the fourth pair
     * @param r4
     *         the right elements of the fourth pair
     * @param l5
     *         the left element of the fifth pair
     * @param r5
     *         the right elements of the fifth pair
     * @param l6
     *         the left element of the sixth pair
     * @param r6
     *         the right elements of the sixth pair
     * @param l7
     *         the left element of the seventh pair
     * @param r7
     *         the right elements of the seventh pair
     * @param <LeftT>
     *         the type of the left elements in the pairs
     * @param <RightT>
     *         the type of the right elements in the pairs
     * @return a new {@link MultiRightPairList} containing the specified pairs
     */
    public static <LeftT, RightT> MultiRightPairList<LeftT, RightT> of(LeftT l1, List<RightT> r1,
                                                                       LeftT l2, List<RightT> r2,
                                                                       LeftT l3, List<RightT> r3,
                                                                       LeftT l4, List<RightT> r4,
                                                                       LeftT l5, List<RightT> r5,
                                                                       LeftT l6, List<RightT> r6,
                                                                       LeftT l7, List<RightT> r7) {
        var pairs = MultiRightPairList.of(l1, r1, l2, r2, l3, r3, l4, r4, l5, r5, l6, r6);
        pairs.add(l7, r7);
        return pairs;
    }

    /**
     * Creates a {@link MultiRightPairList} with eight pairs.
     *
     * @param l1
     *         the left element of the first pair
     * @param r1
     *         the right elements of the first pair
     * @param l2
     *         the left element of the second pair
     * @param r2
     *         the right elements of the second pair
     * @param l3
     *         the left element of the third pair
     * @param r3
     *         the right elements of the third pair
     * @param l4
     *         the left element of the fourth pair
     * @param r4
     *         the right elements of the fourth pair
     * @param l5
     *         the left element of the fifth pair
     * @param r5
     *         the right elements of the fifth pair
     * @param l6
     *         the left element of the sixth pair
     * @param r6
     *         the right elements of the sixth pair
     * @param l7
     *         the left element of the seventh pair
     * @param r7
     *         the right elements of the seventh pair
     * @param l8
     *         the left element of the eighth pair
     * @param r8
     *         the right elements of the eighth pair
     * @param <LeftT>
     *         the type of the left elements in the pairs
     * @param <RightT>
     *         the type of the right elements in the pairs
     * @return a new {@link MultiRightPairList} containing the specified pairs
     */
    public static <LeftT, RightT> MultiRightPairList<LeftT, RightT> of(LeftT l1, List<RightT> r1,
                                                                       LeftT l2, List<RightT> r2,
                                                                       LeftT l3, List<RightT> r3,
                                                                       LeftT l4, List<RightT> r4,
                                                                       LeftT l5, List<RightT> r5,
                                                                       LeftT l6, List<RightT> r6,
                                                                       LeftT l7, List<RightT> r7,
                                                                       LeftT l8, List<RightT> r8) {
        var pairs = MultiRightPairList.of(l1, r1, l2, r2, l3, r3, l4, r4, l5, r5, l6, r6, l7, r7);
        pairs.add(l8, r8);
        return pairs;
    }

    /**
     * Creates a {@link MultiRightPairList} with nine pairs.
     *
     * @param l1
     *         the left element of the first pair
     * @param r1
     *         the right elements of the first pair
     * @param l2
     *         the left element of the second pair
     * @param r2
     *         the right elements of the second pair
     * @param l3
     *         the left element of the third pair
     * @param r3
     *         the right elements of the third pair
     * @param l4
     *         the left element of the fourth pair
     * @param r4
     *         the right elements of the fourth pair
     * @param l5
     *         the left element of the fifth pair
     * @param r5
     *         the right elements of the fifth pair
     * @param l6
     *         the left element of the sixth pair
     * @param r6
     *         the right elements of the sixth pair
     * @param l7
     *         the left element of the seventh pair
     * @param r7
     *         the right elements of the seventh pair
     * @param l8
     *         the left element of the eighth pair
     * @param r8
     *         the right elements of the eighth pair
     * @param l9
     *         the left element of the ninth pair
     * @param r9
     *         the right elements of the ninth pair
     * @param <LeftT>
     *         the type of the left elements in the pairs
     * @param <RightT>
     *         the type of the right elements in the pairs
     * @return a new {@link MultiRightPairList} containing the specified pairs
     */
    public static <LeftT, RightT> MultiRightPairList<LeftT, RightT> of(LeftT l1, List<RightT> r1,
                                                                       LeftT l2, List<RightT> r2,
                                                                       LeftT l3, List<RightT> r3,
                                                                       LeftT l4, List<RightT> r4,
                                                                       LeftT l5, List<RightT> r5,
                                                                       LeftT l6, List<RightT> r6,
                                                                       LeftT l7, List<RightT> r7,
                                                                       LeftT l8, List<RightT> r8,
                                                                       LeftT l9, List<RightT> r9) {
        var pairs = MultiRightPairList.of(l1, r1, l2, r2, l3, r3, l4, r4, l5, r5, l6, r6, l7, r7, l8, r8);
        pairs.add(l9, r9);
        return pairs;
    }

    /**
     * Creates a {@link MultiRightPairList} with ten pairs.
     *
     * @param l1
     *         the left element of the first pair
     * @param r1
     *         the right elements of the first pair
     * @param l2
     *         the left element of the second pair
     * @param r2
     *         the right elements of the second pair
     * @param l3
     *         the left element of the third pair
     * @param r3
     *         the right elements of the third pair
     * @param l4
     *         the left element of the fourth pair
     * @param r4
     *         the right elements of the fourth pair
     * @param l5
     *         the left element of the fifth pair
     * @param r5
     *         the right elements of the fifth pair
     * @param l6
     *         the left element of the sixth pair
     * @param r6
     *         the right elements of the sixth pair
     * @param l7
     *         the left element of the seventh pair
     * @param r7
     *         the right elements of the seventh pair
     * @param l8
     *         the left element of the eighth pair
     * @param r8
     *         the right elements of the eighth pair
     * @param l9
     *         the left element of the ninth pair
     * @param r9
     *         the right elements of the ninth pair
     * @param l10
     *         the left element of the tenth pair
     * @param r10
     *         the right elements of the tenth pair
     * @param <LeftT>
     *         the type of the left elements in the pairs
     * @param <RightT>
     *         the type of the right elements in the pairs
     * @return a new {@link MultiRightPairList} containing the specified pairs
     */
    public static <LeftT, RightT> MultiRightPairList<LeftT, RightT> of(LeftT l1, List<RightT> r1,
                                                                       LeftT l2, List<RightT> r2,
                                                                       LeftT l3, List<RightT> r3,
                                                                       LeftT l4, List<RightT> r4,
                                                                       LeftT l5, List<RightT> r5,
                                                                       LeftT l6, List<RightT> r6,
                                                                       LeftT l7, List<RightT> r7,
                                                                       LeftT l8, List<RightT> r8,
                                                                       LeftT l9, List<RightT> r9,
                                                                       LeftT l10, List<RightT> r10) {
        var pairs = MultiRightPairList.of(l1, r1, l2, r2, l3, r3, l4, r4, l5, r5, l6, r6, l7, r7, l8, r8, l9, r9);
        pairs.add(l10, r10);
        return pairs;
    }


    /**
     * Creates a {@link MultiRightPairList} with a single pair.
     *
     * @param l1
     *         the left element of the first pair
     * @param r1
     *         the right element of the first pair
     * @param <LeftT>
     *         the type of the left elements in the pairs
     * @param <RightT>
     *         the type of the right elements in the pairs
     * @return a new {@link MultiRightPairList} containing the specified pair
     */
    public static <LeftT, RightT> MultiRightPairList<LeftT, RightT> of(LeftT l1, RightT r1) {
        List<MultiRightPair<LeftT, RightT>> pairs = new LinkedList<>();
        pairs.add(MultiRightPair.of(l1, r1));
        return new MultiRightPairList<>(pairs);
    }

    /**
     * Creates a {@link MultiRightPairList} with two pairs.
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
     * @return a new {@link MultiRightPairList} containing the specified pairs
     */
    public static <LeftT, RightT> MultiRightPairList<LeftT, RightT> of(LeftT l1, RightT r1,
                                                                       LeftT l2, RightT r2) {
        var pairs = MultiRightPairList.of(l1, r1);
        pairs.add(l2, List.of(r2));
        return pairs;
    }

    /**
     * Creates a {@link MultiRightPairList} with three pairs.
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
     * @return a new {@link MultiRightPairList} containing the specified pairs
     */
    public static <LeftT, RightT> MultiRightPairList<LeftT, RightT> of(LeftT l1, RightT r1,
                                                                       LeftT l2, RightT r2,
                                                                       LeftT l3, RightT r3) {
        var pairs = MultiRightPairList.of(l1, r1, l2, r2);
        pairs.add(l3, List.of(r3));
        return pairs;
    }

    /**
     * Creates a {@link MultiRightPairList} with four pairs.
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
     * @return a new {@link MultiRightPairList} containing the specified pairs
     */
    public static <LeftT, RightT> MultiRightPairList<LeftT, RightT> of(LeftT l1, RightT r1,
                                                                       LeftT l2, RightT r2,
                                                                       LeftT l3, RightT r3,
                                                                       LeftT l4, RightT r4) {
        var pairs = MultiRightPairList.of(l1, r1, l2, r2, l3, r3);
        pairs.add(l4, List.of(r4));
        return pairs;
    }

    /**
     * Creates a {@link MultiRightPairList} with five pairs.
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
     * @return a new {@link MultiRightPairList} containing the specified pairs
     */
    public static <LeftT, RightT> MultiRightPairList<LeftT, RightT> of(LeftT l1, RightT r1,
                                                                       LeftT l2, RightT r2,
                                                                       LeftT l3, RightT r3,
                                                                       LeftT l4, RightT r4,
                                                                       LeftT l5, RightT r5) {
        var pairs = MultiRightPairList.of(l1, r1, l2, r2, l3, r3, l4, r4);
        pairs.add(l5, List.of(r5));
        return pairs;
    }

    /**
     * Creates a {@link MultiRightPairList} with six pairs.
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
     * @return a new {@link MultiRightPairList} containing the specified pairs
     */
    public static <LeftT, RightT> MultiRightPairList<LeftT, RightT> of(LeftT l1, RightT r1,
                                                                       LeftT l2, RightT r2,
                                                                       LeftT l3, RightT r3,
                                                                       LeftT l4, RightT r4,
                                                                       LeftT l5, RightT r5,
                                                                       LeftT l6, RightT r6) {
        var pairs = MultiRightPairList.of(l1, r1, l2, r2, l3, r3, l4, r4, l5, r5);
        pairs.add(l6, List.of(r6));
        return pairs;
    }

    /**
     * Creates a {@link MultiRightPairList} with seven pairs.
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
     * @return a new {@link MultiRightPairList} containing the specified pairs
     */
    public static <LeftT, RightT> MultiRightPairList<LeftT, RightT> of(LeftT l1, RightT r1,
                                                                       LeftT l2, RightT r2,
                                                                       LeftT l3, RightT r3,
                                                                       LeftT l4, RightT r4,
                                                                       LeftT l5, RightT r5,
                                                                       LeftT l6, RightT r6,
                                                                       LeftT l7, RightT r7) {
        var pairs = MultiRightPairList.of(l1, r1, l2, r2, l3, r3, l4, r4, l5, r5, l6, r6);
        pairs.add(l7, List.of(r7));
        return pairs;
    }

    /**
     * Creates a {@link MultiRightPairList} with eight pairs.
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
     * @return a new {@link MultiRightPairList} containing the specified pairs
     */
    public static <LeftT, RightT> MultiRightPairList<LeftT, RightT> of(LeftT l1, RightT r1,
                                                                       LeftT l2, RightT r2,
                                                                       LeftT l3, RightT r3,
                                                                       LeftT l4, RightT r4,
                                                                       LeftT l5, RightT r5,
                                                                       LeftT l6, RightT r6,
                                                                       LeftT l7, RightT r7,
                                                                       LeftT l8, RightT r8) {
        var pairs = MultiRightPairList.of(l1, r1, l2, r2, l3, r3, l4, r4, l5, r5, l6, r6, l7, r7);
        pairs.add(l8, List.of(r8));
        return pairs;
    }

    /**
     * Creates a {@link MultiRightPairList} with nine pairs.
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
     * @return a new {@link MultiRightPairList} containing the specified pairs
     */
    public static <LeftT, RightT> MultiRightPairList<LeftT, RightT> of(LeftT l1, RightT r1,
                                                                       LeftT l2, RightT r2,
                                                                       LeftT l3, RightT r3,
                                                                       LeftT l4, RightT r4,
                                                                       LeftT l5, RightT r5,
                                                                       LeftT l6, RightT r6,
                                                                       LeftT l7, RightT r7,
                                                                       LeftT l8, RightT r8,
                                                                       LeftT l9, RightT r9) {
        var pairs = MultiRightPairList.of(l1, r1, l2, r2, l3, r3, l4, r4, l5, r5, l6, r6, l7, r7, l8, r8);
        pairs.add(l9, List.of(r9));
        return pairs;
    }

    /**
     * Creates a {@link MultiRightPairList} with ten pairs.
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
     * @return a new {@link MultiRightPairList} containing the specified pairs
     */
    public static <LeftT, RightT> MultiRightPairList<LeftT, RightT> of(LeftT l1, RightT r1,
                                                                       LeftT l2, RightT r2,
                                                                       LeftT l3, RightT r3,
                                                                       LeftT l4, RightT r4,
                                                                       LeftT l5, RightT r5,
                                                                       LeftT l6, RightT r6,
                                                                       LeftT l7, RightT r7,
                                                                       LeftT l8, RightT r8,
                                                                       LeftT l9, RightT r9,
                                                                       LeftT l10, RightT r10) {
        var pairs = MultiRightPairList.of(l1, r1, l2, r2, l3, r3, l4, r4, l5, r5, l6, r6, l7, r7, l8, r8, l9, r9);
        pairs.add(l10, List.of(r10));
        return pairs;
    }
}
