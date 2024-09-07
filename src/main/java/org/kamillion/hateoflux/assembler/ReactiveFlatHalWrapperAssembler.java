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
 * @since 17.08.2024
 */

package org.kamillion.hateoflux.assembler;

import org.kamillion.hateoflux.model.hal.HalEntityWrapper;
import org.kamillion.hateoflux.model.hal.HalListWrapper;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Reactive interface for managing the transformation of standalone entities into HAL-compliant representations,
 * supplemented with hypermedia links in a reactive programming context. This interface is tailored for reactive
 * environments, facilitating the enhancement of entity streams with the necessary fields and structure to comply with
 * HAL standards, enabling reactive streams of entities to become HAL-compliant.
 * <p>
 * While the interface's main focus is the transformation of reactive streams, it also comes equipped with the means to
 * transform in an imperative manner, i.e., with direct objects and, for example, lists.
 *
 * <p> Core functionalities include:
 * <ul>
 *     <li>Enhancing streams of entities to meet HAL structure requirements reactively.</li>
 *     <li>Appending hypermedia links to entities within the stream to support navigability and resource interaction
 *     in a HAL-based API reactively.</li>
 *     <li>Supporting pagination and backpressure in reactive streams when wrapping entities to provide structured
 *     navigation across large datasets reactively.</li>
 * </ul>
 * <p>
 * This interface abstracts the reactive tasks associated with modifying entity streams to fit HAL specifications,
 * streamlining the creation of HAL-compliant entity representations in a reactive programming context.
 *
 * <p>See also:
 * <ul>
 *    <li>{@link FlatHalWrapperAssembler} - for imperative (non-reactive) handling of entities <b>without</b>
 *    embedded entities.</li>
 *    <li>{@link EmbeddingHalWrapperAssembler} - for imperative handling of entities <b>with</b> embedded entities.</li>
 *    <li>{@link ReactiveEmbeddingHalWrapperAssembler} - for reactive <b>and</b> imperative handling of standalone
 *    entities <b>with</b> embedded entities.</li>
 * </ul>
 * <p>
 *  @author Younes El Ouarti
 */

public interface ReactiveFlatHalWrapperAssembler<EntityT> extends FlatHalWrapperAssembler<EntityT> {

    default Mono<HalListWrapper<EntityT, Void>> WrapInListWrapper(@NonNull Flux<EntityT> entitiesToWrap,
                                                                  ServerWebExchange exchange) {
        return entitiesToWrap.collectList()
                .map(entitiesToWrapValue -> wrapInListWrapper(entitiesToWrapValue, exchange));
    }

    default Mono<HalListWrapper<EntityT, Void>> wrapInListWrapper(@NonNull Flux<EntityT> entitiesToWrap,
                                                                  @NonNull Mono<Long> totalElements,
                                                                  int pageSize,
                                                                  @Nullable Long offset,
                                                                  ServerWebExchange exchange) {
        Mono<List<EntityT>> entitiesAsListMono = entitiesToWrap.collectList();
        return Mono.zip(entitiesAsListMono, totalElements,
                (entitiesValue, totalElementsValue) -> wrapInListWrapper(entitiesValue, totalElementsValue,
                        pageSize, offset, exchange));
    }

    default Mono<HalEntityWrapper<EntityT, Void>> wrapInEntityWrapper(@NonNull Mono<EntityT> entityToWrap,
                                                                      ServerWebExchange exchange) {

        return entityToWrap.map(e -> wrapInEntityWrapper(e, exchange));
    }

}
