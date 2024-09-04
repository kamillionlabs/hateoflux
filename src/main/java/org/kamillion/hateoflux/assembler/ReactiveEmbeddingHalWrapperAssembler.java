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
 * @since 21.07.2024
 */

package org.kamillion.hateoflux.assembler;

import org.kamillion.hateoflux.model.hal.HalEntityWrapper;
import org.kamillion.hateoflux.model.hal.HalListWrapper;
import org.kamillion.hateoflux.utility.Pair;
import org.kamillion.hateoflux.utility.PairList;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author Younes El Ouarti
 */
public interface ReactiveEmbeddingHalWrapperAssembler<EntityT, EmbeddedT> extends
        EmbeddingHalWrapperAssembler<EntityT, EmbeddedT> {

    default Mono<HalListWrapper<EntityT, EmbeddedT>> toListWrapper(@NonNull Flux<Pair<EntityT, EmbeddedT>> entitiesToWrap,
                                                                   ServerWebExchange exchange) {
        return convertToPairs(entitiesToWrap)
                .map(pairList -> toListWrapper(pairList, exchange));
    }

    default Mono<HalListWrapper<EntityT, EmbeddedT>> toPagedListWrapper(@NonNull Flux<Pair<EntityT, EmbeddedT>> entitiesToWrap,
                                                                        @NonNull Mono<Long> totalElements,
                                                                        int pageSize,
                                                                        @Nullable Long offset,
                                                                        ServerWebExchange exchange) {
        Mono<PairList<EntityT, EmbeddedT>> entitiesAsPairs = convertToPairs(entitiesToWrap);
        return Mono.zip(entitiesAsPairs, totalElements,
                (entities, total) -> toPagedListWrapper(entities, total, pageSize, offset, exchange));
    }


    private Mono<PairList<EntityT, EmbeddedT>> convertToPairs(@NonNull Flux<Pair<EntityT, EmbeddedT>> entitiesToWrap) {
        return entitiesToWrap.collect(PairList::new, PairList::add);
    }


    default Mono<HalEntityWrapper<EntityT, EmbeddedT>> toEntityWrapper(@NonNull Mono<EntityT> entityToWrap,
                                                                       @NonNull Mono<EmbeddedT> embedded,
                                                                       ServerWebExchange exchange) {
        return Mono.zip(entityToWrap, embedded,
                (entityValue, embeddedValue) -> toEntityWrapper(entityValue, embeddedValue, exchange));
    }


    default Mono<HalEntityWrapper<EntityT, EmbeddedT>> toEntityWrapper(@NonNull Mono<EntityT> entityWrap,
                                                                       @NonNull Flux<EmbeddedT> embeddedList,
                                                                       ServerWebExchange exchange) {
        Mono<List<EmbeddedT>> embeddedListAsMono = embeddedList.collectList();
        return Mono.zip(entityWrap, embeddedListAsMono,
                (entityValue, embeddedListValue) -> toEntityWrapper(entityValue, embeddedListValue, exchange));
    }


    default Mono<HalEntityWrapper<EntityT, EmbeddedT>> toEntityWrapper(@NonNull Mono<EntityT> entityToWrap,
                                                                       @NonNull String embeddedListName,
                                                                       @NonNull Flux<EmbeddedT> embeddedList,
                                                                       ServerWebExchange exchange) {
        Mono<List<EmbeddedT>> embeddedListAsMono = embeddedList.collectList();
        return Mono.zip(entityToWrap, embeddedListAsMono,
                (entityValue, embeddedListValue) -> toEntityWrapper(entityValue, embeddedListName, embeddedListValue,
                        exchange));
    }

    default Mono<HalEntityWrapper<EntityT, EmbeddedT>> toEntityWrapper(@NonNull Mono<EntityT> entityToWrap,
                                                                       @NonNull Class<?> embeddedTypeAsNameOrigin,
                                                                       @NonNull Flux<EmbeddedT> embeddedList,
                                                                       ServerWebExchange exchange) {
        Mono<List<EmbeddedT>> embeddedListAsMono = embeddedList.collectList();
        return Mono.zip(entityToWrap, embeddedListAsMono,
                (entityValue, embeddedListValue) -> toEntityWrapper(entityValue, embeddedTypeAsNameOrigin,
                        embeddedListValue, exchange));
    }
}
