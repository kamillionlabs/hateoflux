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
 * @since 13.07.2024
 */

package de.kamillionlabs.hateoflux.assembler;

import de.kamillionlabs.hateoflux.model.hal.*;
import de.kamillionlabs.hateoflux.utility.PairList;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;

/**
 * Interface for managing the transformation of entities and their associated embedded entities into HAL-compliant
 * representations, supplemented with hypermedia links. This interface facilitates the direct enhancement of entities
 * with the necessary fields and structure to comply with HAL standards, enabling entities to become HAL-compliant.
 *
 * <p> Core functionalities include:
 * <ul>
 *     <li>Directly enhancing main entities and their embedded entities to meet HAL structure requirements.</li>
 *     <li>Appending hypermedia links to entities to support navigability and resource interaction in a HAL-based
 *     API.</li>
 *     <li>Enabling custom naming and linking definitions for collections of embedded entities through
 *     implementation.</li>
 *     <li>Supporting pagination when wrapping lists of entities to provide structured navigation across large
 *     datasets.</li>
 * </ul>
 * <p>
 * This interface abstracts the tasks associated with modifying entities to fit HAL specifications, streamlining the
 * creation of HAL-compliant entity representations.
 *
 * <p>See also:
 * <ul>
 *    <li>{@link FlatHalWrapperAssembler} - for imperative (non-reactive) handling of entities <b>without</b>
 *    embedded entities.</li>
 *    <li>{@link ReactiveEmbeddingHalWrapperAssembler} - for reactive <b>and</b> imperative handling of entities
 *    <b>with</b> embedded entities.</li>
 *    <li>{@link ReactiveFlatHalWrapperAssembler} - for reactive <b>and</b> imperative handling of standalone
 *    entities <b>without</b> embedded entities.</li>
 * </ul>
 *
 * @param <EntityT>
 *         the type of the object being wrapped, which contains the main data
 * @param <EmbeddedT>
 *         the type of the object representing additional embedded resources related to the main data, if any
 * @author Younes El Ouarti
 */
public non-sealed interface EmbeddingHalWrapperAssembler<EntityT, EmbeddedT> extends
        SealedEntityLinkAssemblerModule<EntityT>,
        SealedEntityListAssemblerModule<EntityT, EmbeddedT>,
        SealedEmbeddedLinkAssemblerModule<EmbeddedT> {


    /**
     * Wraps a list of main entities with their corresponding embedded entities in a {@link HalListWrapper}, optionally
     * including pagination information, appending hypermedia links as defined by the assembler.
     *
     * @param entitiesToWrap
     *         the list of main entities and their corresponding embedded entities to wrap
     * @param pageInfo
     *         optional pagination information to include in the wrapper
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a {@link HalListWrapper} enriched with hypermedia links and optional pagination details
     *
     * @see #wrapInListWrapper(PairList, long, int, Long, ServerWebExchange)
     * @see #wrapInListWrapper(PairList, ServerWebExchange)
     */
    default HalListWrapper<EntityT, EmbeddedT> wrapInListWrapper(@NonNull PairList<EntityT, EmbeddedT> entitiesToWrap,
                                                                 @Nullable HalPageInfo pageInfo,
                                                                 ServerWebExchange exchange) {
        List<HalEntityWrapper<EntityT, EmbeddedT>> listOfWrappedEntitiesWithEmbedded =
                entitiesToWrap.stream()
                        .map(pair -> {
                            EntityT entity = pair.left();
                            EmbeddedT embedded = pair.right();
                            return wrapInEntityWrapper(entity, embedded, exchange);
                        }).toList();

        HalListWrapper<EntityT, EmbeddedT> result = HalListWrapper.wrap(listOfWrappedEntitiesWithEmbedded)
                .withLinks(buildLinksForEntityList(exchange));

        if (pageInfo == null) {
            return result;
        } else {
            return result.withPageInfo(pageInfo);
        }
    }

    /**
     * Wraps a list of main entities with their corresponding embedded entities in a {@link HalListWrapper}, appending
     * hypermedia links as defined by the assembler.
     *
     * @param entitiesToWrap
     *         the list of main entities and their corresponding embedded entities to wrap
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a {@link HalListWrapper} equipped with hypermedia links for each entity and the list as a whole
     *
     * @see #wrapInListWrapper(PairList, long, int, Long, ServerWebExchange)
     * @see #wrapInListWrapper(PairList, HalPageInfo, ServerWebExchange)
     */
    default HalListWrapper<EntityT, EmbeddedT> wrapInListWrapper(@NonNull PairList<EntityT, EmbeddedT> entitiesToWrap,
                                                                 ServerWebExchange exchange) {
        return wrapInListWrapper(entitiesToWrap, null, exchange);
    }

    /**
     * Wraps a list of main entities with their corresponding embedded entities in a {@link HalListWrapper} with
     * pagination information, appending hypermedia links as defined by the assembler.
     *
     * @param entitiesToWrap
     *         the list of main entities and their corresponding embedded entities to wrap
     * @param totalElements
     *         the total number of elements across all pages
     * @param pageSize
     *         the number of items per page
     * @param offset
     *         the starting offset of the page, if specified
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a {@link HalListWrapper} with hypermedia links and pagination information
     *
     * @see #wrapInListWrapper(PairList, HalPageInfo, ServerWebExchange)
     * @see #wrapInListWrapper(PairList, ServerWebExchange)
     */
    default HalListWrapper<EntityT, EmbeddedT> wrapInListWrapper(@NonNull PairList<EntityT, EmbeddedT> entitiesToWrap,
                                                                 long totalElements,
                                                                 int pageSize,
                                                                 @Nullable Long offset,
                                                                 ServerWebExchange exchange) {
        HalPageInfo pageInfo = HalPageInfo.assemble(entitiesToWrap, totalElements, pageSize, offset);
        return wrapInListWrapper(entitiesToWrap, pageInfo, exchange);
    }


    /**
     * Wraps a single main entity and its corresponding embedded entity in a {@link HalEntityWrapper}, appending
     * hypermedia links as defined by the assembler.
     *
     * @param entityToWrap
     *         the main entity to wrap
     * @param embedded
     *         the embedded entity associated with the main entity
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a {@link HalEntityWrapper} with hypermedia links for the entity and its embedded counterpart
     *
     * @see #wrapInEntityWrapper(Object, List, ServerWebExchange)
     */
    default HalEntityWrapper<EntityT, EmbeddedT> wrapInEntityWrapper(@NonNull EntityT entityToWrap,
                                                                     @NonNull EmbeddedT embedded,
                                                                     ServerWebExchange exchange) {
        return HalEntityWrapper.wrap(entityToWrap)
                .withLinks(buildLinksForEntity(entityToWrap, exchange))
                .withEmbeddedEntity(
                        HalEmbeddedWrapper.wrap(embedded)
                                .withLinks(buildLinksForEmbedded(embedded, exchange))
                );
    }

    /**
     * Wraps a single main entity and a non-empty list of its embedded entities in a {@link HalEntityWrapper}, appending
     * hypermedia links as defined by the assembler.
     *
     * @param entityToWrap
     *         the main entity to wrap
     * @param embeddedList
     *         the non-empty list of embedded entities associated with the main entity; the list name is derived from
     *         the embedded entity's class name (see also {@link Relation})
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a {@link HalEntityWrapper} that includes the main entity and its embedded entities, all enhanced with
     * hypermedia links
     *
     * @throws IllegalArgumentException
     *         if the embedded list is null or empty
     * @see #wrapInEntityWrapper(Object, Object, ServerWebExchange)
     * @see #wrapInEntityWrapper(Object, String, List, ServerWebExchange)
     * @see #wrapInEntityWrapper(Object, Class, List, ServerWebExchange)
     */
    default HalEntityWrapper<EntityT, EmbeddedT> wrapInEntityWrapper(@NonNull EntityT entityToWrap,
                                                                     @NonNull List<EmbeddedT> embeddedList,
                                                                     ServerWebExchange exchange) {
        var wrappedEmbeddedList = wrapEmbeddedElementsInList(embeddedList, exchange);
        return HalEntityWrapper.wrap(entityToWrap)
                .withLinks(buildLinksForEntity(entityToWrap, exchange))
                .withNonEmptyEmbeddedList(wrappedEmbeddedList);
    }

    /**
     * Wraps a single main entity and its list of embedded entities, identified by a directly provided list name, in a
     * {@link HalEntityWrapper}, appending hypermedia links as defined by the assembler. The list is allowed to be
     * empty.
     *
     * @param entityToWrap
     *         the main entity to wrap
     * @param embeddedListName
     *         the explicitly provided name for the list of embedded entities
     * @param embeddedList
     *         the list of embedded entities associated with the main entity, which may be empty
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a {@link HalEntityWrapper} that includes the main entity and its embedded entities, all enhanced with
     * hypermedia links
     *
     * @see #wrapInEntityWrapper(Object, Object, ServerWebExchange)
     * @see #wrapInEntityWrapper(Object, Class, List, ServerWebExchange)
     * @see #wrapInEntityWrapper(Object, List, ServerWebExchange)
     */
    default HalEntityWrapper<EntityT, EmbeddedT> wrapInEntityWrapper(@NonNull EntityT entityToWrap,
                                                                     @NonNull String embeddedListName,
                                                                     @NonNull List<EmbeddedT> embeddedList,
                                                                     ServerWebExchange exchange) {
        var wrappedEmbeddedList = wrapEmbeddedElementsInList(embeddedList, exchange);
        return HalEntityWrapper.wrap(entityToWrap)
                .withLinks(buildLinksForEntity(entityToWrap, exchange))
                .withEmbeddedList(embeddedListName, wrappedEmbeddedList);
    }

    /**
     * Wraps a single main entity and its list of embedded entities, with the list name derived from the specified class
     * {@code embeddedTypeAsNameOrigin}, in a {@link HalEntityWrapper}, appending hypermedia links as defined by the
     * assembler. The list may be empty.
     *
     * @param entityToWrap
     *         the main entity to wrap
     * @param embeddedTypeAsNameOrigin
     *         the class from which the list name is derived (see also {@link Relation})
     * @param embeddedList
     *         the list of embedded entities associated with the main entity, which may be empty
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a {@link HalEntityWrapper} that includes the main entity and its derived named embedded entities, all
     * enhanced with hypermedia links
     *
     * @see #wrapInEntityWrapper(Object, Object, ServerWebExchange)
     * @see #wrapInEntityWrapper(Object, String, List, ServerWebExchange)
     * @see #wrapInEntityWrapper(Object, List, ServerWebExchange)
     */
    default HalEntityWrapper<EntityT, EmbeddedT> wrapInEntityWrapper(@NonNull EntityT entityToWrap,
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
