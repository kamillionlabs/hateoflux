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
 * @since 08.07.2024
 */

package de.kamillionlabs.hateoflux.assembler;

import de.kamillionlabs.hateoflux.model.hal.HalEntityWrapper;
import de.kamillionlabs.hateoflux.model.hal.HalListWrapper;
import de.kamillionlabs.hateoflux.model.hal.HalPageInfo;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;

/**
 * Interface for managing the transformation of standalone entities into HAL-compliant representations,
 * supplemented with hypermedia links. This interface facilitates the direct enhancement of entities with the necessary
 * fields and structure to comply with HAL standards, enabling entities to become HAL-compliant.
 *
 * <p> Core functionalities include:
 * <ul>
 *     <li>Directly enhancing entities to meet HAL structure requirements.</li>
 *     <li>Appending hypermedia links to entities to support navigability and resource interaction in a HAL-based
 *     API.</li>
 *     <li>Supporting pagination when wrapping lists of entities to provide structured navigation across large
 *     datasets.</li>
 * </ul>
 * <p>
 * This interface abstracts the tasks associated with modifying entities to fit HAL specifications, streamlining the
 * creation of HAL-compliant entity representations.
 *
 * <p>See also:
 * <ul>
 *    <li>{@link EmbeddingHalWrapperAssembler} - for imperative (non-reactive) handling of entities <b>with</b>
 *    embedded entities.</li>
 *    <li>{@link ReactiveEmbeddingHalWrapperAssembler} - for reactive <b>and</b> imperative handling of entities
 *    <b>with</b> embedded entities.</li>
 *    <li>{@link ReactiveFlatHalWrapperAssembler} - for reactive <b>and</b> imperative handling of standalone
 *    entities <b>without</b> embedded entities.</li>
 * </ul>
 *
 * @author Younes El Ouarti
 */
public non-sealed interface FlatHalWrapperAssembler<EntityT> extends
        SealedEntityLinkAssemblerModule<EntityT>,
        SealedEntityListAssemblerModule<EntityT, Void> {


    /**
     * Wraps a list of entities into a {@link HalListWrapper}, enhancing them with hypermedia links as defined by the
     * assembler.
     *
     * @param entitiesToWrap
     *         the list of entities to be wrapped
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a {@link HalListWrapper} that includes the wrapped entities enhanced with hypermedia links
     */
    default HalListWrapper<EntityT, Void> wrapInListWrapper(@NonNull List<EntityT> entitiesToWrap,
                                                            ServerWebExchange exchange) {
        return wrapInListWrapper(entitiesToWrap, null, exchange);
    }

    /**
     * Wraps a list of entities into a {@link HalListWrapper} with pagination details, enhancing them with hypermedia
     * links as defined by the assembler.
     *
     * @param entitiesToWrap
     *         the list of entities to be wrapped
     * @param totalElements
     *         the total number of elements across all pages
     * @param pageSize
     *         the number of items per page
     * @param offset
     *         the starting offset of the page, if specified
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a {@link HalListWrapper} that includes the wrapped entities enhanced with hypermedia links, along with
     * pagination information
     */
    default HalListWrapper<EntityT, Void> wrapInListWrapper(@NonNull List<EntityT> entitiesToWrap,
                                                            long totalElements,
                                                            int pageSize,
                                                            @Nullable Long offset,
                                                            ServerWebExchange exchange) {
        HalPageInfo pageInfo = HalPageInfo.assemble(entitiesToWrap, totalElements, pageSize, offset);
        return wrapInListWrapper(entitiesToWrap, pageInfo, exchange);
    }

    /**
     * Wraps a list of entities into a {@link HalListWrapper}, optionally including pagination information, and enhances
     * them with hypermedia links as defined by the assembler.
     *
     * @param entitiesToWrap
     *         the list of entities to be wrapped
     * @param pageInfo
     *         optional pagination information to include in the wrapper
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a {@link HalListWrapper} that includes the wrapped entities enhanced with hypermedia links, and
     * optionally pagination details
     */
    default HalListWrapper<EntityT, Void> wrapInListWrapper(@NonNull List<EntityT> entitiesToWrap,
                                                            @Nullable HalPageInfo pageInfo,
                                                            ServerWebExchange exchange) {
        List<HalEntityWrapper<EntityT, Void>> listOfWrappedEntities =
                entitiesToWrap.stream()
                        .map(entity -> wrapInEntityWrapper(entity, exchange))
                        .toList();

        HalListWrapper<EntityT, Void> result = HalListWrapper.wrap(listOfWrappedEntities)
                .withLinks(buildLinksForEntityList(exchange));

        if (pageInfo == null) {
            return result;
        } else {
            return result.withPageInfo(pageInfo);
        }
    }

    /**
     * Wraps a single entity in a {@link HalEntityWrapper} and enhances it with hypermedia links as defined by the
     * assembler.
     *
     * @param entityToWrap
     *         the entity to wrap
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a {@link HalEntityWrapper} that includes the wrapped entity enhanced with hypermedia links
     */
    default HalEntityWrapper<EntityT, Void> wrapInEntityWrapper(@NonNull EntityT entityToWrap,
                                                                ServerWebExchange exchange) {
        return HalEntityWrapper.wrap(entityToWrap)
                .withLinks(buildLinksForEntity(entityToWrap, exchange));
    }

}
