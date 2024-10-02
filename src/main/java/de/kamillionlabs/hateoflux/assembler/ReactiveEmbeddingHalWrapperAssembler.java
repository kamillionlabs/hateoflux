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

import de.kamillionlabs.hateoflux.model.hal.HalEntityWrapper;
import de.kamillionlabs.hateoflux.model.hal.HalListWrapper;
import de.kamillionlabs.hateoflux.model.hal.Relation;
import de.kamillionlabs.hateoflux.utility.Pair;
import de.kamillionlabs.hateoflux.utility.PairList;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Reactive interface for managing the transformation of entities and their associated embedded entities into
 * HAL-compliant representations, supplemented with hypermedia links. This interface is designed for use in reactive
 * programming environments where entities are emitted as a stream. It facilitates the enhancement of entity streams
 * with the necessary fields and structure to comply with HAL standards, enabling reactive streams of entities to become
 * HAL-compliant.
 * <p>
 * While the interface's main focus is the transformation of reactive streams, it also comes equipped with the means to
 * transform in an imperative manner, i.e., with direct objects and, for example, lists.
 *
 * <p> Core functionalities include:
 * <ul>
 *     <li>Enhancing streams of main entities and their embedded entities to meet HAL structure requirements
 *     reactively.</li>
 *     <li>Appending hypermedia links to entities within the stream to support navigability and resource interaction
 *     in a HAL-based API reactively.</li>
 *     <li>Enabling custom naming and linking definitions for collections of embedded entities through reactive
 *     implementation.</li>
 *     <li>Supporting pagination and backpressure in reactive streams when wrapping entities to provide structured
 *     navigation across large datasets.</li>
 * </ul>
 * <p>
 * This interface abstracts the reactive tasks associated with modifying entity streams to fit HAL specifications,
 * streamlining the  creation of HAL-compliant entity representations in a reactive programming context.
 *
 * <p>See also:
 * <ul>
 *    <li>{@link FlatHalWrapperAssembler} - for imperative (non-reactive) handling of entities <b>without</b>
 *    embedded entities.</li>
 *    <li>{@link EmbeddingHalWrapperAssembler} - for imperative handling of entities <b>with</b> embedded entities.</li>
 *    <li>{@link ReactiveFlatHalWrapperAssembler} - for reactive <b>and</b> imperative handling of standalone
 *    entities <b>without</b> embedded entities.</li>
 * </ul>
 *
 * @author Younes El Ouarti
 */
public interface ReactiveEmbeddingHalWrapperAssembler<EntityT, EmbeddedT> extends
        EmbeddingHalWrapperAssembler<EntityT, EmbeddedT> {

    /**
     * Wraps a reactive stream of entity pairs into a Mono of a {@link HalListWrapper}, enhancing them with hypermedia
     * links as defined by the assembler.
     *
     * @param entitiesToWrap
     *         the reactive stream of entities and their associated embedded entities to be wrapped
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a Mono of a {@link HalListWrapper} containing the entities enhanced with hypermedia links
     */
    default Mono<HalListWrapper<EntityT, EmbeddedT>> wrapInListWrapper(@NonNull Flux<Pair<EntityT, EmbeddedT>> entitiesToWrap,
                                                                       ServerWebExchange exchange) {
        return convertToPairs(entitiesToWrap)
                .map(pairList -> wrapInListWrapper(pairList, exchange));
    }

    /**
     * Wraps a reactive stream of entity pairs into a Mono of a {@link HalListWrapper} with pagination details. This
     * includes hypermedia links as defined by the assembler, along with pagination parameters.
     *
     * @param entitiesToWrap
     *         the reactive stream of entities and their associated embedded entities to be wrapped
     * @param totalElements
     *         a {@link Mono<Long>} providing the total number of elements across all pages
     * @param pageSize
     *         the number of items per page
     * @param offset
     *         the starting offset of the page, if specified
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a Mono of a {@link HalListWrapper} containing the paginated list of entities enhanced with hypermedia
     * links
     */
    default Mono<HalListWrapper<EntityT, EmbeddedT>> wrapInListWrapper(@NonNull Flux<Pair<EntityT, EmbeddedT>> entitiesToWrap,
                                                                       @NonNull Mono<Long> totalElements,
                                                                       int pageSize,
                                                                       @Nullable Long offset,
                                                                       ServerWebExchange exchange) {
        Mono<PairList<EntityT, EmbeddedT>> entitiesAsPairs = convertToPairs(entitiesToWrap);
        return Mono.zip(entitiesAsPairs, totalElements,
                (entities, total) -> wrapInListWrapper(entities, total, pageSize, offset, exchange));
    }


    private Mono<PairList<EntityT, EmbeddedT>> convertToPairs(@NonNull Flux<Pair<EntityT, EmbeddedT>> entitiesToWrap) {
        return entitiesToWrap.collect(PairList::new, PairList::add);
    }


    /**
     * Wraps a reactive Mono of an entity and its associated embedded entity into a {@link HalEntityWrapper}, enhancing
     * both with hypermedia links as defined by the assembler.
     *
     * @param entityToWrap
     *         the Mono of the entity to be wrapped
     * @param embedded
     *         the Mono of the associated embedded entity
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a Mono of a {@link HalEntityWrapper} containing the wrapped entity and embedded entity, enhanced with
     * hypermedia links
     */
    default Mono<HalEntityWrapper<EntityT, EmbeddedT>> wrapInEntityWrapper(@NonNull Mono<EntityT> entityToWrap,
                                                                           @NonNull Mono<EmbeddedT> embedded,
                                                                           ServerWebExchange exchange) {
        return Mono.zip(entityToWrap, embedded,
                (entityValue, embeddedValue) -> wrapInEntityWrapper(entityValue, embeddedValue, exchange));
    }


    /**
     * Wraps a reactive Mono of an entity and a non-empty Flux of its embedded entities into a {@link HalEntityWrapper},
     * appending hypermedia links as defined by the assembler.
     *
     * @param entityWrap
     *         the Mono of the main entity to wrap
     * @param embeddedList
     *         the Flux of embedded entities associated with the main entity; this list must not be empty. The list
     *         name is derived from the embedded entity's class name (see also {@link Relation})
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a Mono of a {@link HalEntityWrapper} that includes the main entity and its embedded entities, all
     * enhanced with hypermedia links
     *
     * @throws IllegalArgumentException
     *         if the embedded list is null or empty
     */
    default Mono<HalEntityWrapper<EntityT, EmbeddedT>> wrapInEntityWrapper(@NonNull Mono<EntityT> entityWrap,
                                                                           @NonNull Flux<EmbeddedT> embeddedList,
                                                                           ServerWebExchange exchange) {
        Mono<List<EmbeddedT>> embeddedListAsMono = embeddedList.collectList();
        return Mono.zip(entityWrap, embeddedListAsMono,
                (entityValue, embeddedListValue) -> wrapInEntityWrapper(entityValue, embeddedListValue, exchange));
    }


    /**
     * Wraps a reactive Mono of an entity and a Flux of its associated embedded entities into a
     * {@link HalEntityWrapper}, appending hypermedia links as defined by the assembler. The list is identified by a
     * directly provided list name. The flux may be empty.
     *
     * @param entityToWrap
     *         the Mono of the main entity to wrap
     * @param embeddedListName
     *         the explicitly provided name for the list of embedded entities
     * @param embeddedList
     *         the Flux of embedded entities associated with the main entity, which may be empty
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a Mono of a {@link HalEntityWrapper} that includes the main entity and its named list of embedded
     * entities, all enhanced with hypermedia links
     */
    default Mono<HalEntityWrapper<EntityT, EmbeddedT>> wrapInEntityWrapper(@NonNull Mono<EntityT> entityToWrap,
                                                                           @NonNull String embeddedListName,
                                                                           @NonNull Flux<EmbeddedT> embeddedList,
                                                                           ServerWebExchange exchange) {
        Mono<List<EmbeddedT>> embeddedListAsMono = embeddedList.collectList();
        return Mono.zip(entityToWrap, embeddedListAsMono,
                (entityValue, embeddedListValue) -> wrapInEntityWrapper(entityValue, embeddedListName,
                        embeddedListValue,
                        exchange));
    }

    /**
     * Wraps a reactive Mono of an entity and a Flux of its associated embedded entities into a
     * {@link HalEntityWrapper}, appending hypermedia links as defined by the assembler. The list name is derived from
     * the specified class {@code embeddedTypeAsNameOrigin}. The list may be empty.
     *
     * @param entityToWrap
     *         the Mono of the main entity to wrap
     * @param embeddedTypeAsNameOrigin
     *         the class from which the list name is derived (see also {@link Relation})
     * @param embeddedList
     *         the Flux of embedded entities associated with the main entity, which may be empty
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a Mono of a {@link HalEntityWrapper} that includes the main entity and its derived named list of embedded
     * entities, all enhanced with hypermedia links
     */
    default Mono<HalEntityWrapper<EntityT, EmbeddedT>> wrapInEntityWrapper(@NonNull Mono<EntityT> entityToWrap,
                                                                           @NonNull Class<?> embeddedTypeAsNameOrigin,
                                                                           @NonNull Flux<EmbeddedT> embeddedList,
                                                                           ServerWebExchange exchange) {
        Mono<List<EmbeddedT>> embeddedListAsMono = embeddedList.collectList();
        return Mono.zip(entityToWrap, embeddedListAsMono,
                (entityValue, embeddedListValue) -> wrapInEntityWrapper(entityValue, embeddedTypeAsNameOrigin,
                        embeddedListValue, exchange));
    }
}
