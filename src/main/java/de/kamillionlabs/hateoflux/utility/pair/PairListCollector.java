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
 * @since 04.11.2024
 */

package de.kamillionlabs.hateoflux.utility.pair;

import java.util.stream.Collector;

/**
 * Utility class providing collectors for {@link PairList}.
 *
 * @author Younes El Ouarti
 */
public class PairListCollector {

    private PairListCollector() {
    }

    /**
     * Creates a {@link Collector} that accumulates {@link Pair} elements into a {@link PairList}.
     *
     * @param <LeftT>
     *         the type of the left elements in the pairs
     * @param <RightT>
     *         the type of the right elements in the pairs
     * @return a Collector that collects Pair elements into a PairList
     */
    public static <LeftT, RightT> Collector<Pair<LeftT, RightT>, ?, PairList<LeftT, RightT>> toPairList() {
        return Collector.of(
                PairList::of,
                PairList::add,
                (left, right) -> {
                    left.addAll(right);
                    return left;
                },
                Collector.Characteristics.UNORDERED
        );
    }
}
