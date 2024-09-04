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

package org.kamillion.hateoflux.assembler;

import org.kamillion.hateoflux.model.hal.HalEmbeddedWrapper;
import org.kamillion.hateoflux.model.hal.HalEntityWrapper;
import org.kamillion.hateoflux.model.hal.HalListWrapper;
import org.kamillion.hateoflux.model.hal.HalPageInfo;
import org.kamillion.hateoflux.utility.PairList;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;

/**
 * @author Younes El Ouarti
 */
public non-sealed interface EmbeddingHalWrapperAssembler<EntityT, EmbeddedT> extends
        SealedEntityLinkAssemblerModule<EntityT>,
        SealedEntityListAssemblerModule<EntityT, EmbeddedT>,
        SealedEmbeddedLinkAssemblerModule<EmbeddedT> {

    default HalListWrapper<EntityT, EmbeddedT> toListWrapper(@NonNull PairList<EntityT, EmbeddedT> entitiesToWrap,
                                                             ServerWebExchange exchange) {
        return toPagedListWrapper(entitiesToWrap, null, exchange);
    }

    default HalListWrapper<EntityT, EmbeddedT> toPagedListWrapper(@NonNull PairList<EntityT, EmbeddedT> entitiesToWrap,
                                                                  long totalElements,
                                                                  int pageSize,
                                                                  @Nullable Long offset,
                                                                  ServerWebExchange exchange) {
        HalPageInfo pageInfo = HalPageInfo.assemble(entitiesToWrap, totalElements, pageSize, offset);
        return toPagedListWrapper(entitiesToWrap, pageInfo, exchange);
    }


    default HalListWrapper<EntityT, EmbeddedT> toPagedListWrapper(@NonNull PairList<EntityT, EmbeddedT> entitiesToWrap,
                                                                  @Nullable HalPageInfo pageInfo,
                                                                  ServerWebExchange exchange) {
        List<HalEntityWrapper<EntityT, EmbeddedT>> listOfWrappedEntitiesWithEmbedded =
                entitiesToWrap.stream()
                        .map(pair -> {
                            EntityT entity = pair.left();
                            EmbeddedT embedded = pair.right();
                            return toEntityWrapper(entity, embedded, exchange);
                        }).toList();

        HalListWrapper<EntityT, EmbeddedT> result = HalListWrapper.wrap(listOfWrappedEntitiesWithEmbedded)
                .withLinks(buildLinksForEntityList(exchange));

        if (pageInfo == null) {
            return result;
        } else {
            return result.withPageInfo(pageInfo);
        }
    }

    default HalEntityWrapper<EntityT, EmbeddedT> toEntityWrapper(@NonNull EntityT entityToWrap,
                                                                 @NonNull EmbeddedT embedded,
                                                                 ServerWebExchange exchange) {
        return HalEntityWrapper.wrap(entityToWrap)
                .withLinks(buildLinksForEntity(entityToWrap, exchange))
                .withEmbeddedEntity(
                        HalEmbeddedWrapper.wrap(embedded)
                                .withLinks(buildLinksForEmbedded(embedded, exchange))
                );
    }

    default HalEntityWrapper<EntityT, EmbeddedT> toEntityWrapper(@NonNull EntityT entityToWrap,
                                                                 @NonNull List<EmbeddedT> embeddedList,
                                                                 ServerWebExchange exchange) {
        var wrappedEmbeddedList = wrapEmbeddedElementsInList(embeddedList, exchange);
        return HalEntityWrapper.wrap(entityToWrap)
                .withLinks(buildLinksForEntity(entityToWrap, exchange))
                .withNonEmptyEmbeddedList(wrappedEmbeddedList);
    }

    default HalEntityWrapper<EntityT, EmbeddedT> toEntityWrapper(@NonNull EntityT entityToWrap,
                                                                 @NonNull String embeddedListName,
                                                                 @NonNull List<EmbeddedT> embeddedList,
                                                                 ServerWebExchange exchange) {
        var wrappedEmbeddedList = wrapEmbeddedElementsInList(embeddedList, exchange);
        return HalEntityWrapper.wrap(entityToWrap)
                .withLinks(buildLinksForEntity(entityToWrap, exchange))
                .withEmbeddedList(embeddedListName, wrappedEmbeddedList);
    }

    default HalEntityWrapper<EntityT, EmbeddedT> toEntityWrapper(@NonNull EntityT entityToWrap,
                                                                 @NonNull Class<?> embeddedTypeAsNameOrigin,
                                                                 @NonNull List<EmbeddedT> embeddedList,
                                                                 ServerWebExchange exchange) {
        var wrappedEmbeddedList = wrapEmbeddedElementsInList(embeddedList, exchange);
        return HalEntityWrapper.wrap(entityToWrap)
                .withLinks(buildLinksForEntity(entityToWrap, exchange))
                .withEmbeddedList(embeddedTypeAsNameOrigin, wrappedEmbeddedList);
    }

    private List<HalEmbeddedWrapper<EmbeddedT>> wrapEmbeddedElementsInList(@NonNull List<EmbeddedT> embeddedEntities,
                                                                           ServerWebExchange exchange) {
        return embeddedEntities.stream()
                .map(embedded -> HalEmbeddedWrapper.wrap(embedded)
                        .withLinks(buildLinksForEmbedded(embedded, exchange)))
                .toList();
    }

}
