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
 * @since 21.07.2024
 */

package de.kamillionlabs.hateoflux.assembler;

import de.kamillionlabs.hateoflux.model.hal.HalListWrapper;
import de.kamillionlabs.hateoflux.model.hal.HalResourceWrapper;
import de.kamillionlabs.hateoflux.model.hal.Relation;
import de.kamillionlabs.hateoflux.utility.SortCriteria;
import de.kamillionlabs.hateoflux.utility.pair.MultiRightPairFlux;
import de.kamillionlabs.hateoflux.utility.pair.PairFlux;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import static de.kamillionlabs.hateoflux.utility.pair.MultiRightPairListCollector.toMultiRightPairList;
import static de.kamillionlabs.hateoflux.utility.pair.PairListCollector.toPairList;

/**
 * Interface for managing the transformation of resources and their associated embedded resources into
 * HAL-compliant representations, supplemented with hypermedia links. This interface is designed for use in reactive
 * programming environments where resources are emitted as a stream. It facilitates the enhancement of resource streams
 * with the necessary fields and structure to comply with HAL standards, enabling reactive streams of resources to
 * become HAL-compliant.
 * <p>
 * While the interface's main focus is the transformation of reactive streams, it also comes equipped with the means to
 * transform in an imperative manner, i.e., with direct objects and, for example, lists.
 *
 * <p> Core functionalities include:
 * <ul>
 *     <li>Enhancing streams of main resources and their embedded resources to meet HAL structure requirements
 *     reactively.</li>
 *     <li>Appending hypermedia links to resources within the stream to support navigability and resource interaction
 *     in a HAL-based API reactively.</li>
 *     <li>Enabling custom naming and linking definitions for collections of embedded resources through reactive
 *     implementation.</li>
 *     <li>Supporting pagination and backpressure in reactive streams when wrapping resources to provide structured
 *     navigation across large datasets.</li>
 * </ul>
 *
 * @param <ResourceT>
 *         the type of the object being wrapped, which contains the main data
 * @param <EmbeddedT>
 *         the type of the object representing additional embedded resources related to the main data, if any
 * @author Younes El Ouarti
 * @see FlatHalWrapperAssembler
 */
public non-sealed interface EmbeddingHalWrapperAssembler<ResourceT, EmbeddedT> extends
        SealedNonReactiveEmbeddingHalWrapperAssembler<ResourceT, EmbeddedT> {

    /**
     * Wraps a reactive stream of resource pairs into a Mono of a {@link HalListWrapper}, enhancing them with hypermedia
     * links as defined by the assembler.
     * <p>
     * The embedded resources (list) for a main resource i.e. the list with the right elements of the
     * {@link MultiRightPairFlux}, is allowed to be empty or null resulting in either the removal of the
     * {@code _embedded} node or the addition of an empty array in the {@code _embedded} node in the serialized JSON.
     *
     * @param resourcesToWrap
     *         the reactive stream of resources and their associated embedded resources to be wrapped
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a Mono of a {@link HalListWrapper} containing the resources enhanced with hypermedia links
     *
     * @see #wrapInListWrapper(MultiRightPairFlux, Mono, int, Long, List, ServerWebExchange)
     */
    default Mono<HalListWrapper<ResourceT, EmbeddedT>> wrapInListWrapper(
            @NonNull MultiRightPairFlux<ResourceT, EmbeddedT> resourcesToWrap, ServerWebExchange exchange) {
        return resourcesToWrap.getFlux().collect(toMultiRightPairList())
                .map(pairList -> wrapInListWrapper(pairList, exchange));
    }


    /**
     * Wraps a reactive stream of resource pairs into a Mono of a {@link HalListWrapper} with pagination details. This
     * includes hypermedia links as defined by the assembler, along with pagination parameters.
     *
     * <p>
     * The embedded resources (list) for a main resource i.e. the list with the right elements of the
     * {@link MultiRightPairFlux}, is allowed to be empty or null resulting in either the removal of the
     * {@code _embedded} node or the addition of an empty array in the {@code _embedded} node in the serialized JSON.
     *
     * @param resourcesToWrap
     *         the reactive stream of resources and their associated embedded resources to be wrapped
     * @param totalElements
     *         a {@link Mono<Long>} providing the total number of elements across all pages
     * @param pageSize
     *         the number of items per page
     * @param offset
     *         the starting offset of the page, if specified
     * @param sortCriteria
     *         sort criteria (property and direction) of the page
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a Mono of a {@link HalListWrapper} containing the paginated list of resources enhanced with hypermedia
     * links
     *
     * @see #wrapInListWrapper(MultiRightPairFlux, ServerWebExchange)
     */
    default Mono<HalListWrapper<ResourceT, EmbeddedT>> wrapInListWrapper(
            @NonNull MultiRightPairFlux<ResourceT, EmbeddedT> resourcesToWrap,
            @NonNull Mono<Long> totalElements,
            int pageSize,
            @Nullable Long offset,
            List<SortCriteria> sortCriteria,
            ServerWebExchange exchange) {

        var resourcesAsPairs = resourcesToWrap.getFlux().collect(toMultiRightPairList());
        return Mono.zip(resourcesAsPairs, totalElements,
                (resources, total) -> wrapInListWrapper(resources, total, pageSize, offset, sortCriteria, exchange));
    }

    /**
     * Wraps a reactive stream of resource pairs into a Mono of a {@link HalListWrapper} with pagination details. This
     * includes hypermedia links as defined by the assembler, along with pagination parameters.
     * <p>
     * The embedded resources in the {@link PairFlux} are allowed to be null resulting in the removal of the
     * {@code _embedded} node in the serialized JSON.
     *
     * @param resourcesToWrap
     *         the reactive stream of resources and their associated embedded resources to be wrapped
     * @param totalElements
     *         a {@link Mono<Long>} providing the total number of elements across all pages
     * @param pageSize
     *         the number of items per page
     * @param offset
     *         the starting offset of the page, if specified
     * @param sortCriteria
     *         sort criteria (property and direction) of the page
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a Mono of a {@link HalListWrapper} containing the paginated list of resources enhanced with hypermedia
     * links
     *
     * @see #wrapInListWrapper(PairFlux, ServerWebExchange)
     */
    default Mono<HalListWrapper<ResourceT, EmbeddedT>> wrapInListWrapper(
            @NonNull PairFlux<ResourceT, EmbeddedT> resourcesToWrap,
            @NonNull Mono<Long> totalElements,
            int pageSize,
            @Nullable Long offset,
            List<SortCriteria> sortCriteria,
            ServerWebExchange exchange) {

        var resourcesAsPairs = resourcesToWrap.getFlux().collect(toPairList());
        return Mono.zip(resourcesAsPairs, totalElements,
                (resources, total) -> wrapInListWrapper(resources, total, pageSize, offset, sortCriteria, exchange));
    }

    /**
     * Wraps a reactive stream of resource pairs into a Mono of a {@link HalListWrapper}, enhancing them with hypermedia
     * links as defined by the assembler.
     * <p>
     * The embedded resources in the {@link PairFlux} are allowed to be null resulting in the removal of the
     * {@code _embedded} node in the serialized JSON.
     *
     * @param resourcesToWrap
     *         the reactive stream of resources and their associated embedded resources to be wrapped
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a Mono of a {@link HalListWrapper} containing the resources enhanced with hypermedia links
     *
     * @see #wrapInListWrapper(PairFlux, Mono, int, Long, List, ServerWebExchange)
     */
    default Mono<HalListWrapper<ResourceT, EmbeddedT>> wrapInListWrapper(
            @NonNull PairFlux<ResourceT, EmbeddedT> resourcesToWrap,
            ServerWebExchange exchange) {

        return resourcesToWrap.getFlux().collect(toPairList())
                .map(pairList -> wrapInListWrapper(pairList, exchange));
    }


    /**
     * Wraps a reactive Mono of a resource and its associated embedded resource into a {@link HalResourceWrapper},
     * enhancing both with hypermedia links as defined by the assembler.
     * <p>
     * If {@code resourceToWrap} is empty, the result will also be an empty {@code Mono}. If the {@code embedded} is
     * empty, when serialized, the resulting JSON will not have an {@code _embedded} node.
     *
     * @param resourceToWrap
     *         the Mono of the resource to be wrapped
     * @param embedded
     *         the Mono of the associated embedded resource
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a Mono of a {@link HalResourceWrapper} containing the wrapped resource and embedded resource, enhanced
     * with hypermedia links
     *
     * @see #wrapInResourceWrapper(Mono, Flux, ServerWebExchange)
     */
    default Mono<HalResourceWrapper<ResourceT, EmbeddedT>> wrapInResourceWrapper(@NonNull Mono<ResourceT> resourceToWrap,
                                                                                 @NonNull Mono<EmbeddedT> embedded,
                                                                                 ServerWebExchange exchange) {
        //Make sure the mono emits a value in case the result is an empty mono
        Mono<Optional<EmbeddedT>> optionalEmbedded = embedded.map(Optional::of)
                .defaultIfEmpty(Optional.empty());

        return Mono.zip(resourceToWrap, optionalEmbedded,
                (resourceValue, embeddedValue) -> wrapInResourceWrapper(resourceValue, embeddedValue.orElse(null),
                        exchange));
    }


    /**
     * Wraps a reactive {@code Mono} of a resource and a non-empty {@code Flux} of its embedded resources into a
     * {@link HalResourceWrapper}, appending hypermedia links as defined by the assembler.
     * <p>
     * If {@code resourceToWrap} is empty, the result will also be an empty {@code Mono}. If the {@code embedded} is
     * empty, when serialized, the resulting JSON will have an empty array in the {@code _embedded} node.
     *
     * @param resourceWrap
     *         the Mono of the main resource to wrap
     * @param embeddedList
     *         the Flux of embedded resources associated with the main resource; this list must not be empty. The list
     *         name is derived from the embedded resource's class name (see also {@link Relation})
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a Mono of a {@link HalResourceWrapper} that includes the main resource and its embedded resources, all
     * enhanced with hypermedia links
     *
     * @throws IllegalArgumentException
     *         if the embedded list is null or empty
     * @see #wrapInResourceWrapper(Mono, String, Flux, ServerWebExchange)
     * @see #wrapInResourceWrapper(Mono, Class, Flux, ServerWebExchange)
     * @see #wrapInResourceWrapper(Mono, Mono, ServerWebExchange)
     */
    default Mono<HalResourceWrapper<ResourceT, EmbeddedT>> wrapInResourceWrapper(@NonNull Mono<ResourceT> resourceWrap,
                                                                                 @NonNull Flux<EmbeddedT> embeddedList,
                                                                                 ServerWebExchange exchange) {
        Mono<List<EmbeddedT>> embeddedListAsMono = embeddedList.collectList();
        return Mono.zip(resourceWrap, embeddedListAsMono,
                (resourceValue, embeddedListValue) -> wrapInResourceWrapper(resourceValue, embeddedListValue,
                        exchange));
    }


    /**
     * Wraps a reactive Mono of an resource and a Flux of its associated embedded resources into a
     * {@link HalResourceWrapper}, appending hypermedia links as defined by the assembler. The list is identified by a
     * directly provided list name. The flux may be empty.
     * <p>
     * If {@code resourceToWrap} is empty, the result will also be an empty {@code Mono}. If the {@code embedded} is
     * empty, when serialized, the resulting JSON will have an empty array in the {@code _embedded} node.
     *
     * @param resourceToWrap
     *         the Mono of the main resource to wrap
     * @param embeddedListName
     *         the explicitly provided name for the list of embedded resources
     * @param embeddedList
     *         the Flux of embedded resources associated with the main resource, which may be empty
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a Mono of a {@link HalResourceWrapper} that includes the main resource and its named list of embedded
     * resources, all enhanced with hypermedia links
     *
     * @see #wrapInResourceWrapper(Mono, Class, Flux, ServerWebExchange)
     * @see #wrapInResourceWrapper(Mono, Flux, ServerWebExchange)
     * @see #wrapInResourceWrapper(Mono, Mono, ServerWebExchange)
     */
    default Mono<HalResourceWrapper<ResourceT, EmbeddedT>> wrapInResourceWrapper(@NonNull Mono<ResourceT> resourceToWrap,
                                                                                 @NonNull String embeddedListName,
                                                                                 @NonNull Flux<EmbeddedT> embeddedList,
                                                                                 ServerWebExchange exchange) {
        Mono<List<EmbeddedT>> embeddedListAsMono = embeddedList.collectList();
        return Mono.zip(resourceToWrap, embeddedListAsMono,
                (resourceValue, embeddedListValue) -> wrapInResourceWrapper(resourceValue, embeddedListName,
                        embeddedListValue,
                        exchange));
    }

    /**
     * Wraps a reactive Mono of an resource and a Flux of its associated embedded resources into a
     * {@link HalResourceWrapper}, appending hypermedia links as defined by the assembler. The list name is derived from
     * the specified class {@code embeddedTypeAsNameOrigin}. The list may be empty.
     * <p>
     * If {@code resourceToWrap} is empty, the result will also be an empty {@code Mono}. If the {@code embedded} is
     * empty, when serialized, the resulting JSON will have an empty array in the {@code _embedded} node.
     *
     * @param resourceToWrap
     *         the Mono of the main resource to wrap
     * @param embeddedTypeAsNameOrigin
     *         the class from which the list name is derived (see also {@link Relation})
     * @param embeddedList
     *         the Flux of embedded resources associated with the main resource, which may be empty
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a Mono of a {@link HalResourceWrapper} that includes the main resource and its derived named list of
     * embedded
     * resources, all enhanced with hypermedia links
     *
     * @see #wrapInResourceWrapper(Mono, String, Flux, ServerWebExchange)
     * @see #wrapInResourceWrapper(Mono, Flux, ServerWebExchange)
     * @see #wrapInResourceWrapper(Mono, Mono, ServerWebExchange)
     */
    default Mono<HalResourceWrapper<ResourceT, EmbeddedT>> wrapInResourceWrapper(@NonNull Mono<ResourceT> resourceToWrap,
                                                                                 @NonNull Class<EmbeddedT> embeddedTypeAsNameOrigin,
                                                                                 @NonNull Flux<EmbeddedT> embeddedList,
                                                                                 ServerWebExchange exchange) {
        Mono<List<EmbeddedT>> embeddedListAsMono = embeddedList.collectList();
        return Mono.zip(resourceToWrap, embeddedListAsMono,
                (resourceValue, embeddedListValue) -> wrapInResourceWrapper(resourceValue, embeddedTypeAsNameOrigin,
                        embeddedListValue, exchange));
    }
}
