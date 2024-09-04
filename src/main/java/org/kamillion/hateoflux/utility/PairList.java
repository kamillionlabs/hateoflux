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

package org.kamillion.hateoflux.utility;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Younes El Ouarti
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PairList<LeftT, RightT> extends LinkedList<Pair<LeftT, RightT>> {

    private PairList(List<Pair<LeftT, RightT>> pairs) {
        addAll(pairs);
    }

    public void add(LeftT left, RightT right) {
        add(new Pair<>(left, right));
    }

    public LeftT getLeft(int i) {
        return get(i).left();
    }

    public RightT getRight(int i) {
        return get(i).right();
    }

    public List<LeftT> getLefts() {
        return this.stream().map(Pair::left).toList();
    }

    public List<RightT> getRights() {
        return this.stream().map(Pair::right).toList();
    }

    public static <LeftT, RightT> PairList<LeftT, RightT> of() {
        return new PairList<>();
    }

    public static <LeftT, RightT> PairList<LeftT, RightT> of(List<Pair<LeftT, RightT>> pairs) {
        return new PairList<>(pairs);
    }

    public static <LeftT, RightT> PairList<LeftT, RightT> of(List<LeftT> lefts, List<RightT> rights) {
        Assert.notNull(lefts, "lefts must not be null");
        Assert.notNull(rights, "rights must not be null");
        Assert.isTrue(lefts.size() == rights.size(), "Different sizes in lefts and rights are not allowed");

        List<Pair<LeftT, RightT>> pairs = new LinkedList<>();
        for (int i = 0; i < lefts.size(); i++) {
            pairs.add(new Pair<>(lefts.get(i), rights.get(i)));
        }
        return new PairList<>(pairs);
    }

    public static <LeftT, RightT> PairList<LeftT, RightT> of(Map<LeftT, RightT> pairs) {
        return new PairList<>(pairs.entrySet().stream()
                .map(e -> Pair.of(e.getKey(), e.getValue()))
                .toList());
    }

    public static <LeftT, RightT> PairList<LeftT, RightT> of(LeftT l1, RightT r1) {
        List<Pair<LeftT, RightT>> pairs = new LinkedList<>();
        pairs.add(Pair.of(l1, r1));
        return new PairList<>(pairs);
    }

    public static <LeftT, RightT> PairList<LeftT, RightT> of(LeftT l1, RightT r1,
                                                             LeftT l2, RightT r2) {
        var pairs = PairList.of(l1, r1);
        pairs.add(l2, r2);
        return pairs;
    }

    public static <LeftT, RightT> PairList<LeftT, RightT> of(LeftT l1, RightT r1,
                                                             LeftT l2, RightT r2,
                                                             LeftT l3, RightT r3) {
        var pairs = PairList.of(l1, r1, l2, r2);
        pairs.add(l3, r3);
        return pairs;
    }

    public static <LeftT, RightT> PairList<LeftT, RightT> of(LeftT l1, RightT r1,
                                                             LeftT l2, RightT r2,
                                                             LeftT l3, RightT r3,
                                                             LeftT l4, RightT r4) {
        var pairs = PairList.of(l1, r1, l2, r2, l3, r3);
        pairs.add(l4, r4);
        return pairs;
    }

    public static <LeftT, RightT> PairList<LeftT, RightT> of(LeftT l1, RightT r1,
                                                             LeftT l2, RightT r2,
                                                             LeftT l3, RightT r3,
                                                             LeftT l4, RightT r4,
                                                             LeftT l5, RightT r5) {
        var pairs = PairList.of(l1, r1, l2, r2, l3, r3, l4, r4);
        pairs.add(l5, r5);
        return pairs;
    }

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
