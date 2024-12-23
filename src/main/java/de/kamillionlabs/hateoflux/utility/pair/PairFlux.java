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
 *
 * @since 15.11.2024
 */
package de.kamillionlabs.hateoflux.utility.pair;

import lombok.Getter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

/**
 * Wraps a {@link Flux} of {@link Pair} objects, providing convenient factory methods to create reactive streams of
 * pairs.
 *
 * @param <LeftT>
 *         the type of the left element in the pair
 * @param <RightT>
 *         the type of the right element in the pair
 * @author Younes El Ouarti
 * @see PairList
 * @see MultiRightPairFlux
 */
@Getter
public final class PairFlux<LeftT, RightT> {

    private final Flux<Pair<LeftT, RightT>> flux;

    private PairFlux(final Flux<Pair<LeftT, RightT>> internalFlux) {
        this.flux = internalFlux;
    }

    /**
     * Creates an empty {@link PairFlux}.
     *
     * @param <LeftT>
     *         the type of the left element in the pair
     * @param <RightT>
     *         the type of the right element in the pair
     * @return a new empty {@link PairFlux} instance
     */
    public static <LeftT, RightT> PairFlux<LeftT, RightT> empty() {
        return new PairFlux<>(Flux.empty());
    }

    /**
     * Creates a {@link PairFlux} from an existing {@link Flux} of {@link Pair} objects.
     *
     * @param flux
     *         the {@link Flux} emitting {@link Pair} objects
     * @param <LeftT>
     *         the type of the left element in the pair
     * @param <RightT>
     *         the type of the right element in the pair
     * @return a new {@link PairFlux} instance wrapping the provided {@link Flux}
     */
    public static <LeftT, RightT> PairFlux<LeftT, RightT> of(Flux<Pair<LeftT, RightT>> flux) {
        return new PairFlux<>(flux);
    }

    /**
     * Creates a {@link PairFlux} from an {@link Iterable} of {@link Pair} objects  (e.g. from a {@link PairList}).
     *
     * @param iterable
     *         the {@link Iterable} containing {@link Pair} objects
     * @param <LeftT>
     *         the type of the left element in the pair
     * @param <RightT>
     *         the type of the right element in the pair
     * @return a new {@link PairFlux} emitting the pairs from the provided {@link Iterable}
     */
    public static <LeftT, RightT> PairFlux<LeftT, RightT> fromIterable(Iterable<Pair<LeftT, RightT>> iterable) {
        return new PairFlux<>(Flux.fromIterable(iterable));
    }

    /**
     * Zips a {@link Flux} with a corresponding {@link Mono} to create a {@link PairFlux}.
     * <p>
     * <b>Usage example:</b>
     * <blockquote><pre>
     * Flux&lt;Book&gt; books = getBooksOnTopic("coding");
     *
     * // given getAuthorByBookTitle() returns Mono&lt; Author&gt;
     * PairFlux&lt;Book, Author&gt; pairFlux =
     *         PairFlux.zipWith(books, book -> getAuthorByBookTitle(book.getTitle()));
     * </pre></blockquote>
     *
     * @param flux
     *         the {@link Flux} emitting the first elements of the pair
     * @param mapper
     *         a function that maps each element to a {@link Mono} emitting the corresponding second element
     * @param <LeftT>
     *         the type of the left element in the pair
     * @param <RightT>
     *         the type of the right element in the pair
     * @return a new {@link PairFlux} instance emitting zipped pairs of elements
     */
    public static <LeftT, RightT> PairFlux<LeftT, RightT> zipWith(
            Flux<LeftT> flux, Function<LeftT, Mono<RightT>> mapper) {
        Flux<Pair<LeftT, RightT>> zippedFlux = flux.flatMap(
                left -> mapper.apply(left)
                        .map(right -> Pair.of(left, right))
                        .switchIfEmpty(Mono.just(Pair.of(left, null)))
        );
        return new PairFlux<>(zippedFlux);
    }

    /**
     * Initiates building a {@link PairFlux} by providing the first {@link Flux}. Should be used with
     * {@link Builder#with}.
     * <p>
     * <b>Usage example:</b>
     * <blockquote><pre>
     * Flux&lt;Book&gt; books = getBooksOnTopic("coding");
     *
     * // given getAuthorByBookTitle() returns Mono&lt; Author&gt;
     * PairFlux&lt;Book, Author&gt; pairFlux =
     *         PairFlux.from(books)
     *                 .with(book -> getAuthorByBookTitle(book.getTitle()));
     * </pre></blockquote>
     *
     * @param flux
     *         the {@link Flux} emitting the first elements of the pair
     * @param <LeftT>
     *         the type of the left element in the pair
     * @return a {@link Builder} to continue building the {@link PairFlux}
     */
    public static <LeftT> Builder<LeftT> from(Flux<LeftT> flux) {
        return new Builder<>(flux);
    }

    /**
     * Builder class for {@link PairFlux}.
     *
     * @param <LeftT>
     *         the type of the left element in the pair
     */
    public static class Builder<LeftT> {
        private final Flux<LeftT> flux;

        private Builder(Flux<LeftT> flux) {
            this.flux = flux;
        }

        /**
         * Specifies how to map each element to the right element of the pair asynchronously.
         *
         * <p>
         * <b>Usage example:</b>
         * <blockquote><pre>
         * Flux&lt;Book&gt; books = getBooksOnTopic("coding");
         *
         * // given getAuthorByBookTitle() returns Mono&lt; Author&gt;
         * PairFlux&lt;Book, Author&gt; pairFlux =
         *         PairFlux.from(books)
         *                 .with(book -> getAuthorByBookTitle(book.getTitle()));
         * </pre></blockquote>
         *
         * @param mapper
         *         a function that maps each element to a {@link Mono} emitting the right element
         * @param <RightT>
         *         the type of the right element in the pair
         * @return a new {@link PairFlux} instance emitting paired elements
         */
        public <RightT> PairFlux<LeftT, RightT> with(Function<LeftT, Mono<RightT>> mapper) {
            Flux<Pair<LeftT, RightT>> pairedFlux = flux.flatMap(left ->
                    mapper.apply(left)
                            .map(right -> Pair.of(left, right))
                            .switchIfEmpty(Mono.just(Pair.of(left, null)))
            );
            return new PairFlux<>(pairedFlux);
        }
    }


}
