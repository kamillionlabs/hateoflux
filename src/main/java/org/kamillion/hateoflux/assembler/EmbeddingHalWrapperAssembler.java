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

import org.kamillion.hateoflux.model.Pair;
import org.kamillion.hateoflux.model.hal.HalEmbeddedWrapper;
import org.kamillion.hateoflux.model.hal.HalEntityWrapper;
import org.kamillion.hateoflux.model.hal.HalListWrapper;
import org.kamillion.hateoflux.model.link.Link;
import org.springframework.web.server.ServerWebExchange;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Younes El Ouarti
 */
public interface EmbeddingHalWrapperAssembler<EntityT, EmbeddedT> {


    default HalListWrapper<EntityT, EmbeddedT> toListWrapper(List<Pair<EntityT, EmbeddedT>> entitiesToWrap,
                                                             ServerWebExchange exchange) {
        List<HalEntityWrapper<EntityT, EmbeddedT>> listOfWrappedEntityWithEmbedded =
                entitiesToWrap.stream()
                        .map(pair -> {
                            EntityT entity = pair.left();
                            EmbeddedT embedded = pair.right();
                            return toWrapper(entity, embedded, exchange);
                        }).toList();

        return HalListWrapper.wrap(listOfWrappedEntityWithEmbedded)
                .withLinks(buildLinksForEntityList(entitiesToWrap, exchange));
    }


    default HalEntityWrapper<EntityT, EmbeddedT> toWrapper(EntityT entityToWrap,
                                                           EmbeddedT embedded,
                                                           ServerWebExchange exchange) {
        return HalEntityWrapper.wrap(entityToWrap)
                .withLinks(buildLinksForEntity(entityToWrap, exchange))
                .withEmbeddedEntity(
                        HalEmbeddedWrapper.wrap(embedded)
                                .withLinks(buildLinksForEmbedded(embedded, exchange))
                );
    }

    default HalEntityWrapper<EntityT, EmbeddedT> toWrapper(EntityT entityToWrap,
                                                           List<EmbeddedT> embeddedList,
                                                           ServerWebExchange exchange) {
        var wrappedEmbeddedList = wrapEmbeddedElementsInList(embeddedList, exchange);
        return HalEntityWrapper.wrap(entityToWrap)
                .withLinks(buildLinksForEntity(entityToWrap, exchange))
                .withNonEmptyEmbeddedList(wrappedEmbeddedList);
    }

    default HalEntityWrapper<EntityT, EmbeddedT> toWrapper(EntityT entityToWrap,
                                                           String embeddedListName,
                                                           List<EmbeddedT> embeddedList,
                                                           ServerWebExchange exchange) {
        var wrappedEmbeddedList = wrapEmbeddedElementsInList(embeddedList, exchange);
        return HalEntityWrapper.wrap(entityToWrap)
                .withLinks(buildLinksForEntity(entityToWrap, exchange))
                .withEmbeddedList(embeddedListName, wrappedEmbeddedList);
    }

    default HalEntityWrapper<EntityT, EmbeddedT> toWrapper(EntityT entityToWrap,
                                                           Class<?> embeddedTypeAsNameOrigin,
                                                           List<EmbeddedT> embeddedList,
                                                           ServerWebExchange exchange) {
        var wrappedEmbeddedList = wrapEmbeddedElementsInList(embeddedList, exchange);
        return HalEntityWrapper.wrap(entityToWrap)
                .withLinks(buildLinksForEntity(entityToWrap, exchange))
                .withEmbeddedList(embeddedTypeAsNameOrigin, wrappedEmbeddedList);
    }

    private List<HalEmbeddedWrapper<EmbeddedT>> wrapEmbeddedElementsInList(List<EmbeddedT> embeddedEntities,
                                                                           ServerWebExchange exchange) {
        return embeddedEntities.stream()
                .map(embedded -> HalEmbeddedWrapper.wrap(embedded)
                        .withLinks(buildLinksForEmbedded(embedded, exchange)))
                .toList();
    }

    //-------

    default List<Link> buildLinksForEntityList(List<Pair<EntityT, EmbeddedT>> entityToWrap,
                                               ServerWebExchange exchange) {
        List<Link> links = new ArrayList<>();
        links.add(buildSelfLinkForEntityList(entityToWrap, exchange));
        links.addAll(buildOtherLinksForEntityList(entityToWrap, exchange));
        return links;
    }

    default List<Link> buildOtherLinksForEntityList(List<Pair<EntityT, EmbeddedT>> entityToWrap,
                                                    ServerWebExchange exchange) {
        return List.of();
    }

    Link buildSelfLinkForEntityList(List<Pair<EntityT, EmbeddedT>> entityToWrap,
                                    ServerWebExchange exchange);

    //-------

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

    //-------

    default List<Link> buildLinksForEmbedded(EmbeddedT embedded, ServerWebExchange exchange) {
        List<Link> links = new ArrayList<>();
        links.add(buildSelfLinkForEmbedded(embedded, exchange));
        links.addAll(buildOtherLinksForEmbedded(embedded, exchange));
        return links;
    }

    Link buildSelfLinkForEmbedded(EmbeddedT embedded, ServerWebExchange exchange);

    default List<Link> buildOtherLinksForEmbedded(EmbeddedT embedded, ServerWebExchange exchange) {
        return List.of();
    }
}
