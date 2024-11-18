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

import java.util.List;

/**
 * Represents an immutable pair consisting of a single left value and multiple right values.
 * <p>
 * This record associates a left element of type {@code LeftT} with a list of right elements of type
 * {@code RightT}. It provides a way to group a single left value with multiple related right values
 * without enforcing key-value semantics. Unlike {@link Pair}, which holds only one right value, {@code
 * MultiRightPair} allows for multiple right values to be associated with a single left value.
 *
 * @param left
 *         the left element of the pair
 * @param rights
 *         the list of right elements associated with the left element
 * @param <LeftT>
 *         the type of the left element in the pair
 * @param <RightT>
 *         the type of the right elements in the pair
 * @author Younes
 * @see MultiRightPairList
 * @see MultiRightPairFlux
 * @see Pair
 */
public record MultiRightPair<LeftT, RightT>(LeftT left, List<RightT> rights) {


    /**
     * Gets the left element
     *
     * @return the left element
     */
    public LeftT getLeft() {
        return left;
    }

    /**
     * Gets the right elements
     *
     * @return the right elements
     */
    public List<RightT> getRights() {
        return rights;
    }

    /**
     * Creates a new {@code MultiRightPair} with the specified left and right values.
     *
     * @param left
     *         the left value
     * @param rights
     *         the rights value
     * @param <LeftT>
     *         the type of the left value
     * @param <RightT>
     *         the type of the right value in the list
     * @return a new {@code MultiRightPair} instance containing the given values
     */
    public static <LeftT, RightT> MultiRightPair<LeftT, RightT> of(LeftT left, List<RightT> rights) {
        return new MultiRightPair<>(left, rights);
    }

    /**
     * Creates a new {@code MultiRightPair} with the specified left and single right value.
     *
     * @param left
     *         the left value
     * @param right
     *         the right value
     * @param <LeftT>
     *         the type of the left value
     * @param <RightT>
     *         the type of the right value
     * @return a new {@code MultiRightPair} instance containing the given values
     *
     * @see Pair
     */
    public static <LeftT, RightT> MultiRightPair<LeftT, RightT> of(LeftT left, RightT right) {
        return new MultiRightPair<>(left, List.of(right));
    }

    /**
     * Creates a new {@code MultiRightPair} from a {@link Pair}.
     *
     * @param pair
     *         paired values to take over
     * @param <LeftT>
     *         the type of the left value
     * @param <RightT>
     *         the type of the right value
     * @return a new {@code MultiRightPair} instance containing the given values
     */
    public static <LeftT, RightT> MultiRightPair<LeftT, RightT> of(Pair<LeftT, RightT> pair) {
        return new MultiRightPair<>(pair.left(), List.of(pair.right()));
    }

    /**
     * Indicates whether the {@link MultiRightPair} is empty
     *
     * @return {@code true} true if the pair has values; {@code false} otherwise
     */
    public boolean isEmpty() {
        return left == null && (rights == null || rights.isEmpty());
    }

    /**
     * Creates an empty {@code MultiRightPair}.
     * <p>
     * <b>{@code MultiRightPair}s are immutable, so only used to signify that there exist no pair.</b>
     *
     * @param <LeftT>
     *         the type of the left value
     * @param <RightT>
     *         the type of the right value
     * @return an empty pair
     */
    public static <LeftT, RightT> Pair<LeftT, RightT> empty() {
        return new Pair<>(null, null);
    }
}
