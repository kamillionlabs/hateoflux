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

/**
 * Represents a generic pair of two values.
 *
 * @param left
 *         the left value
 * @param right
 *         the right value
 * @param <LeftT>
 *         the type of the left value
 * @param <RightT>
 *         the type of the right value
 * @author Younes El Ouarti
 * @see PairList
 */
public record Pair<LeftT, RightT>(LeftT left, RightT right) {

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
     */
    public static <LeftT, RightT> Pair<LeftT, RightT> of(LeftT left, RightT right) {
        return new Pair<>(left, right);
    }
}
