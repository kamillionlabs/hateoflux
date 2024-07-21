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
 * @since 08.07.2024
 */

package org.kamillion.hateoflux.assembler;

import org.kamillion.hateoflux.model.hal.HalEntityWrapper;
import org.kamillion.hateoflux.model.hal.HalListWrapper;
import org.kamillion.hateoflux.model.hal.HalPageInfo;
import org.kamillion.hateoflux.model.link.Link;
import org.springframework.lang.Nullable;
import org.springframework.web.server.ServerWebExchange;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Younes El Ouarti
 */
public interface FlatHalWrapperAssembler<EntityT> {

    default HalListWrapper<EntityT, Void> toEmptyListWrapper(Class<?> listItemTypeAsNameOrigin,
                                                             ServerWebExchange exchange) {
        HalListWrapper<EntityT, Void> emptyWrapper = HalListWrapper.empty(listItemTypeAsNameOrigin);
        return emptyWrapper
                .withLinks(buildLinksForEntityList(null, exchange));
    }

    default HalListWrapper<EntityT, Void> toEmptyListWrapper(String listName, ServerWebExchange exchange) {
        HalListWrapper<EntityT, Void> emptyWrapper = HalListWrapper.empty(listName);
        return emptyWrapper
                .withLinks(buildLinksForEntityList(null, exchange));
    }

    default HalListWrapper<EntityT, Void> toListWrapper(List<EntityT> entitiesToWrap,
                                                        ServerWebExchange exchange) {
        return toListWrapper(entitiesToWrap, null, exchange);
    }

    default HalListWrapper<EntityT, Void> toListWrapper(List<EntityT> entitiesToWrap,
                                                        @Nullable HalPageInfo pageInfo,
                                                        ServerWebExchange exchange) {
        List<HalEntityWrapper<EntityT, Void>> listOfWrappedEntities =
                entitiesToWrap.stream()
                        .map(entity -> toEntityWrapper(entity, exchange))
                        .toList();

        HalListWrapper<EntityT, Void> result = HalListWrapper.wrap(listOfWrappedEntities)
                .withLinks(buildLinksForEntityList(entitiesToWrap, exchange));

        if (pageInfo == null) {
            return result;
        } else {
            return result.withPageInfo(pageInfo);
        }
    }

    default HalEntityWrapper<EntityT, Void> toEntityWrapper(EntityT entityToWrap,
                                                            ServerWebExchange exchange) {
        return HalEntityWrapper.wrap(entityToWrap)
                .withLinks(buildLinksForEntity(entityToWrap, exchange));
    }

    default List<Link> buildLinksForEntityList(List<EntityT> entityToWrap,
                                               ServerWebExchange exchange) {
        List<Link> links = new ArrayList<>();
        links.add(buildSelfLinkForEntityList(entityToWrap, exchange));
        links.addAll(buildOtherLinksForEntityList(entityToWrap, exchange));
        return links;
    }

    default List<Link> buildOtherLinksForEntityList(List<EntityT> entityToWrap,
                                                    ServerWebExchange exchange) {
        return List.of();
    }

    Link buildSelfLinkForEntityList(List<EntityT> entitiesToWrap,
                                    ServerWebExchange exchange);

    default List<Link> buildLinksForEntity(EntityT entityToWrap, ServerWebExchange exchange) {
        List<Link> links = new ArrayList<>();
        links.add(buildSelfLinkForEntity(entityToWrap, exchange));
        links.addAll(buildOtherLinksForEntity(entityToWrap, exchange));
        return links;
    }

    Link buildSelfLinkForEntity(EntityT entityToWrap, ServerWebExchange exchange);

    default List<Link> buildOtherLinksForEntity(EntityT entityToWrap, ServerWebExchange exchange) {
        return List.of();
    }
}
