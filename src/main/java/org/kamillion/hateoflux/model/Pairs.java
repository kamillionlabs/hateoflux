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
 * @since 13.07.2024
 */

package org.kamillion.hateoflux.model;

import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Younes El Ouarti
 */
public class Pairs {

    public static <LeftT, RightT> List<Pair<LeftT, RightT>> of(@NonNull LeftT l1, @NonNull RightT r1) {
        List<Pair<LeftT, RightT>> pairs = new ArrayList<>();
        pairs.add(Pair.of(l1, r1));
        return pairs;
    }

    public static <LeftT, RightT> List<Pair<LeftT, RightT>> of(@NonNull LeftT l1, @NonNull RightT r1,
                                                               @NonNull LeftT l2, @NonNull RightT r2) {
        List<Pair<LeftT, RightT>> pairs = new ArrayList<>();
        pairs.add(Pair.of(l1, r1));
        pairs.add(Pair.of(l2, r2));
        return pairs;
    }

    public static <LeftT, RightT> List<Pair<LeftT, RightT>> of(@NonNull LeftT l1, @NonNull RightT r1,
                                                               @NonNull LeftT l2, @NonNull RightT r2,
                                                               @NonNull LeftT l3, @NonNull RightT r3) {
        var pairs = Pairs.of(l1, r1, l2, r2);
        pairs.add(Pair.of(l3, r3));
        return pairs;
    }

    public static <LeftT, RightT> List<Pair<LeftT, RightT>> of(@NonNull LeftT l1, @NonNull RightT r1,
                                                               @NonNull LeftT l2, @NonNull RightT r2,
                                                               @NonNull LeftT l3, @NonNull RightT r3,
                                                               @NonNull LeftT l4, @NonNull RightT r4) {
        var pairs = Pairs.of(l1, r1, l2, r2, l3, r3);
        pairs.add(Pair.of(l4, r4));
        return pairs;
    }

    public static <LeftT, RightT> List<Pair<LeftT, RightT>> of(@NonNull LeftT l1, @NonNull RightT r1,
                                                               @NonNull LeftT l2, @NonNull RightT r2,
                                                               @NonNull LeftT l3, @NonNull RightT r3,
                                                               @NonNull LeftT l4, @NonNull RightT r4,
                                                               @NonNull LeftT l5, @NonNull RightT r5) {
        var pairs = Pairs.of(l1, r1, l2, r2, l3, r3, l4, r4);
        pairs.add(Pair.of(l5, r5));
        return pairs;
    }

    public static <LeftT, RightT> List<Pair<LeftT, RightT>> of(@NonNull LeftT l1, @NonNull RightT r1,
                                                               @NonNull LeftT l2, @NonNull RightT r2,
                                                               @NonNull LeftT l3, @NonNull RightT r3,
                                                               @NonNull LeftT l4, @NonNull RightT r4,
                                                               @NonNull LeftT l5, @NonNull RightT r5,
                                                               @NonNull LeftT l6, @NonNull RightT r6) {
        var pairs = Pairs.of(l1, r1, l2, r2, l3, r3, l4, r4, l5, r5);
        pairs.add(Pair.of(l6, r6));
        return pairs;
    }

    public static <LeftT, RightT> List<Pair<LeftT, RightT>> of(@NonNull LeftT l1, @NonNull RightT r1,
                                                               @NonNull LeftT l2, @NonNull RightT r2,
                                                               @NonNull LeftT l3, @NonNull RightT r3,
                                                               @NonNull LeftT l4, @NonNull RightT r4,
                                                               @NonNull LeftT l5, @NonNull RightT r5,
                                                               @NonNull LeftT l6, @NonNull RightT r6,
                                                               @NonNull LeftT l7, @NonNull RightT r7) {
        var pairs = Pairs.of(l1, r1, l2, r2, l3, r3, l4, r4, l5, r5, l6, r6);
        pairs.add(Pair.of(l7, r7));
        return pairs;
    }

    public static <LeftT, RightT> List<Pair<LeftT, RightT>> of(@NonNull LeftT l1, @NonNull RightT r1,
                                                               @NonNull LeftT l2, @NonNull RightT r2,
                                                               @NonNull LeftT l3, @NonNull RightT r3,
                                                               @NonNull LeftT l4, @NonNull RightT r4,
                                                               @NonNull LeftT l5, @NonNull RightT r5,
                                                               @NonNull LeftT l6, @NonNull RightT r6,
                                                               @NonNull LeftT l7, @NonNull RightT r7,
                                                               @NonNull LeftT l8, @NonNull RightT r8) {
        var pairs = Pairs.of(l1, r1, l2, r2, l3, r3, l4, r4, l5, r5, l6, r6, l7, r7);
        pairs.add(Pair.of(l8, r8));
        return pairs;
    }

    public static <LeftT, RightT> List<Pair<LeftT, RightT>> of(@NonNull LeftT l1, @NonNull RightT r1,
                                                               @NonNull LeftT l2, @NonNull RightT r2,
                                                               @NonNull LeftT l3, @NonNull RightT r3,
                                                               @NonNull LeftT l4, @NonNull RightT r4,
                                                               @NonNull LeftT l5, @NonNull RightT r5,
                                                               @NonNull LeftT l6, @NonNull RightT r6,
                                                               @NonNull LeftT l7, @NonNull RightT r7,
                                                               @NonNull LeftT l8, @NonNull RightT r8,
                                                               @NonNull LeftT l9, @NonNull RightT r9) {
        var pairs = Pairs.of(l1, r1, l2, r2, l3, r3, l4, r4, l5, r5, l6, r6, l7, r7, l8, r8);
        pairs.add(Pair.of(l9, r9));
        return pairs;
    }

    public static <LeftT, RightT> List<Pair<LeftT, RightT>> of(@NonNull LeftT l1, @NonNull RightT r1,
                                                               @NonNull LeftT l2, @NonNull RightT r2,
                                                               @NonNull LeftT l3, @NonNull RightT r3,
                                                               @NonNull LeftT l4, @NonNull RightT r4,
                                                               @NonNull LeftT l5, @NonNull RightT r5,
                                                               @NonNull LeftT l6, @NonNull RightT r6,
                                                               @NonNull LeftT l7, @NonNull RightT r7,
                                                               @NonNull LeftT l8, @NonNull RightT r8,
                                                               @NonNull LeftT l9, @NonNull RightT r9,
                                                               @NonNull LeftT l10, @NonNull RightT r10) {
        var pairs = Pairs.of(l1, r1, l2, r2, l3, r3, l4, r4, l5, r5, l6, r6, l7, r7, l8, r8, l9, r9);
        pairs.add(Pair.of(l10, r10));
        return pairs;
    }

}
