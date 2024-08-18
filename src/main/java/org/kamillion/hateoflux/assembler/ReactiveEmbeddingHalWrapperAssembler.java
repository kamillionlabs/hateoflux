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

import org.kamillion.hateoflux.model.Pair;
import org.kamillion.hateoflux.model.Pairs;
import org.kamillion.hateoflux.model.hal.HalEntityWrapper;
import org.kamillion.hateoflux.model.hal.HalListWrapper;
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

    default Mono<HalListWrapper<EntityT, EmbeddedT>> toListWrapper(Flux<Pair<EntityT, EmbeddedT>> entitiesToWrap,
                                                                   ServerWebExchange exchange) {
        return convertToPairs(entitiesToWrap)
                .map(pairs -> toListWrapper(pairs, exchange));
    }

    default Mono<HalListWrapper<EntityT, EmbeddedT>> toPagedListWrapper(Flux<Pair<EntityT, EmbeddedT>> entitiesToWrap,
                                                                        Mono<Long> totalElements,
                                                                        int pageSize,
                                                                        @Nullable Long offset,
                                                                        ServerWebExchange exchange) {
        Mono<Pairs<EntityT, EmbeddedT>> entitiesAsPairs = convertToPairs(entitiesToWrap);
        return Mono.zip(entitiesAsPairs, totalElements,
                (entities, total) -> toPagedListWrapper(entities, total, pageSize, offset, exchange));
    }


    private Mono<Pairs<EntityT, EmbeddedT>> convertToPairs(Flux<Pair<EntityT, EmbeddedT>> entitiesToWrap) {
        return entitiesToWrap.collect(Pairs::new, Pairs::add);
    }


    default Mono<HalEntityWrapper<EntityT, EmbeddedT>> toEntityWrapper(Mono<EntityT> entityToWrap,
                                                                       Mono<EmbeddedT> embedded,
                                                                       ServerWebExchange exchange) {
        return Mono.zip(entityToWrap, embedded,
                (entityValue, embeddedValue) -> toEntityWrapper(entityValue, embeddedValue, exchange));
    }


    default Mono<HalEntityWrapper<EntityT, EmbeddedT>> toEntityWrapper(Mono<EntityT> entityWrap,
                                                                       Flux<EmbeddedT> embeddedList,
                                                                       ServerWebExchange exchange) {
        Mono<List<EmbeddedT>> embeddedListAsMono = embeddedList.collectList();
        return Mono.zip(entityWrap, embeddedListAsMono,
                (entityValue, embeddedListValue) -> toEntityWrapper(entityValue, embeddedListValue, exchange));
    }


    default Mono<HalEntityWrapper<EntityT, EmbeddedT>> toEntityWrapper(Mono<EntityT> entityToWrap,
                                                                       String embeddedListName,
                                                                       Flux<EmbeddedT> embeddedList,
                                                                       ServerWebExchange exchange) {
        Mono<List<EmbeddedT>> embeddedListAsMono = embeddedList.collectList();
        return Mono.zip(entityToWrap, embeddedListAsMono,
                (entityValue, embeddedListValue) -> toEntityWrapper(entityValue, embeddedListName, embeddedListValue,
                        exchange));
    }

    default Mono<HalEntityWrapper<EntityT, EmbeddedT>> toEntityWrapper(Mono<EntityT> entityToWrap,
                                                                       Class<?> embeddedTypeAsNameOrigin,
                                                                       Flux<EmbeddedT> embeddedList,
                                                                       ServerWebExchange exchange) {
        Mono<List<EmbeddedT>> embeddedListAsMono = embeddedList.collectList();
        return Mono.zip(entityToWrap, embeddedListAsMono,
                (entityValue, embeddedListValue) -> toEntityWrapper(entityValue, embeddedTypeAsNameOrigin,
                        embeddedListValue, exchange));
    }
}
