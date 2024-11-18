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

package de.kamillionlabs.hateoflux.utility.pair;

/**
 * Represents an immutable pair of two values.
 * <p>
 * This record holds a pair of related objects: a left element of type {@code LeftT} and a right element of type
 * {@code RightT}. It provides a simple way to group two objects without enforcing any key-value semantics.
 *
 * @param left
 *         the left value
 * @param right
 *         the right value
 * @param <LeftT>
 *         the type of the left element in the pair
 * @param <RightT>
 *         the type of the right element in the pair
 * @author Younes El Ouarti
 * @see PairList
 * @see PairFlux
 * @see MultiRightPair
 */
public record Pair<LeftT, RightT>(LeftT left, RightT right) {


    /**
     * Gets the left element
     *
     * @return the left element
     */
    public LeftT getLeft() {
        return left;
    }

    /**
     * Gets the right element
     *
     * @return the right element
     */
    public RightT getRight() {
        return right;
    }

    /**
     * Indicates whether the {@link Pair} is empty
     *
     * @return {@code true} true if the pair has values; {@code false} otherwise
     */
    public boolean isEmpty() {
        return left == null && right == null;
    }

    /**
     * Creates an empty {@code Pair}.
     * <p>
     * <b>{@code Pair}s are immutable, so only used to signify that there exist no pair.</b>
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

    /**
     * Creates a new {@code Pair} with the specified left and right values.
     *
     * @param left
     *         the left value
     * @param right
     *         the right value
     * @param <LeftT>
     *         the type of the left value
     * @param <RightT>
     *         the type of the right value
     * @return a new {@code Pair} instance containing the given values
     *
     * @see #Pair(Object, Object)
     */
    public static <LeftT, RightT> Pair<LeftT, RightT> of(LeftT left, RightT right) {
        return new Pair<>(left, right);
    }
}