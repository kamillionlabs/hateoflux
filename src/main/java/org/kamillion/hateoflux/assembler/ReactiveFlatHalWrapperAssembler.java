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
import org.springframework.lang.Nullable;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author Younes El Ouarti
 */
public interface ReactiveFlatHalWrapperAssembler<EntityT> extends FlatHalWrapperAssembler<EntityT> {

    default Mono<HalListWrapper<EntityT, Void>> toListWrapper(Flux<EntityT> entitiesToWrap,
                                                              ServerWebExchange exchange) {
        return entitiesToWrap.collectList()
                .map(entitiesToWrapValue -> toListWrapper(entitiesToWrapValue, exchange));
    }

    default Mono<HalListWrapper<EntityT, Void>> toPagedListWrapper(Flux<EntityT> entitiesToWrap,
                                                                   Mono<Long> totalElements,
                                                                   int pageSize,
                                                                   @Nullable Long offset,
                                                                   ServerWebExchange exchange) {
        Mono<List<EntityT>> entitiesAsListMono = entitiesToWrap.collectList();
        return Mono.zip(entitiesAsListMono, totalElements,
                (entitiesValue, totalElementsValue) -> toPagedListWrapper(entitiesValue, totalElementsValue, pageSize,
                        offset, exchange));
    }

    default Mono<HalEntityWrapper<EntityT, Void>> toEntityWrapper(Mono<EntityT> entityToWrap,
                                                                  ServerWebExchange exchange) {

        return entityToWrap.map(e -> toEntityWrapper(e, exchange));
    }

}
