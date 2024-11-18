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
 * @since 16.11.2024
 */
/*
 * Copyright (c) 2024 kamillion contributors
 *
 * This work is licensed under the GNU General Public License (GPL).
 *
 * @since 14.10.2024
 */

package de.kamillionlabs.hateoflux.utility.pair;

import lombok.Getter;
import reactor.core.publisher.Flux;

/**
 * Wraps a {@link Flux} of {@link MultiRightPair} objects, providing convenient factory methods to create reactive
 * streams of pairs.
 *
 * @param <LeftT>
 *         the type of the left element in the pair
 * @param <RightT>
 *         the type of the right elements in the pair
 * @author Younes El Ouarti
 * @see MultiRightPairList
 * @see PairFlux
 */
@Getter
public class MultiRightPairFlux<LeftT, RightT> {


    private final Flux<MultiRightPair<LeftT, RightT>> flux;

    private MultiRightPairFlux(final Flux<MultiRightPair<LeftT, RightT>> internalFlux) {
        this.flux = internalFlux;
    }

    /**
     * Creates an empty {@link MultiRightPair}.
     *
     * @param <LeftT>
     *         the type of the left element in the pair
     * @param <RightT>
     *         the type of the right element in the pair
     * @return a new empty {@link MultiRightPair} instance
     */
    public static <LeftT, RightT> MultiRightPairFlux<LeftT, RightT> empty() {
        return new MultiRightPairFlux<>(Flux.empty());
    }

    /**
     * Creates a {@link MultiRightPairFlux} from an existing {@link Flux} of {@link MultiRightPair} objects.
     *
     * @param flux
     *         the {@link Flux} emitting {@link MultiRightPair} objects
     * @param <LeftT>
     *         the type of the left element in the pair
     * @param <RightT>
     *         the type of the right elements in the pair
     * @return a new {@link PairFlux} instance wrapping the provided {@link Flux}
     */
    public static <LeftT, RightT> MultiRightPairFlux<LeftT, RightT> of(Flux<MultiRightPair<LeftT, RightT>> flux) {
        return new MultiRightPairFlux<>(flux);
    }

    /**
     * Creates a {@link MultiRightPairFlux} from an {@link Iterable} of {@link MultiRightPair} objects (e.g. from a
     * {@link MultiRightPairList}).
     *
     * @param iterable
     *         the {@link Iterable} containing {@link MultiRightPair} objects
     * @param <LeftT>
     *         the type of the left element in the pair
     * @param <RightT>
     *         the type of the right elements in the pair
     * @return a new {@link MultiRightPairFlux} emitting the pairs from the provided {@link Iterable}
     */
    public static <LeftT, RightT> MultiRightPairFlux<LeftT, RightT> fromIterable(Iterable<MultiRightPair<LeftT,
            RightT>> iterable) {
        return new MultiRightPairFlux<>(Flux.fromIterable(iterable));
    }
}
