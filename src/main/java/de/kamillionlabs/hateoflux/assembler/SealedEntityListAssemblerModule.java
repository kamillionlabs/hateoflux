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
 * @since 12.08.2024
 */

package de.kamillionlabs.hateoflux.assembler;

import de.kamillionlabs.hateoflux.model.hal.HalListWrapper;
import de.kamillionlabs.hateoflux.model.hal.Relation;
import de.kamillionlabs.hateoflux.model.link.Link;
import org.springframework.lang.NonNull;
import org.springframework.web.server.ServerWebExchange;

import java.util.ArrayList;
import java.util.List;

/**
 * Assembler module that has utility functions and builds links for a list of entities.
 *
 * @author Younes El Ouarti
 */
public sealed interface SealedEntityListAssemblerModule<EntityT, EmbeddedT> permits
        FlatHalWrapperAssembler, EmbeddingHalWrapperAssembler {


    /**
     * Creates an empty {@link HalListWrapper} including hypermedia links applicable to the entire list.
     *
     * @param listItemTypeAsNameOrigin
     *         the class of the entity from which the list name is derived, typically pluralized to represent both
     *         entity and embedded entity types (also see {@link Relation})
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return an initialized {@link HalListWrapper} with relevant hypermedia links for the entire list
     */
    default HalListWrapper<EntityT, EmbeddedT> createEmptyListWrapper(@NonNull Class<?> listItemTypeAsNameOrigin,
                                                                      ServerWebExchange exchange) {
        HalListWrapper<EntityT, EmbeddedT> emptyWrapper = HalListWrapper.empty(listItemTypeAsNameOrigin);
        return emptyWrapper.withLinks(buildLinksForEntityList(exchange));
    }

    /**
     * Creates an empty {@link HalListWrapper} including hypermedia links applicable to the entire list.
     *
     * @param listName
     *         the given name for the list
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return an initialized {@link HalListWrapper} with relevant hypermedia links for the entire list
     */
    default HalListWrapper<EntityT, EmbeddedT> createEmptyListWrapper(@NonNull String listName,
                                                                      ServerWebExchange exchange) {
        HalListWrapper<EntityT, EmbeddedT> emptyWrapper = HalListWrapper.empty(listName);
        return emptyWrapper.withLinks(buildLinksForEntityList(exchange));
    }

    /**
     * Main method for building all links for a list of entities and embedded entities, including a self-link and other
     * contextual links applicable to the entire list. It aggregates results from {@link #buildSelfLinkForEntityList}
     * and {@link #buildOtherLinksForEntityList}.
     *
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a list of {@link Link} objects representing hypermedia links for the entire list of entity and embedded
     * entity types
     */
    default List<Link> buildLinksForEntityList(ServerWebExchange exchange) {
        List<Link> links = new ArrayList<>();
        links.add(buildSelfLinkForEntityList(exchange));
        links.addAll(buildOtherLinksForEntityList(exchange));
        return links;
    }

    /**
     * Provides additional contextual links for a list of entities and embedded entities, beyond the self-link,
     * applicable to the entire list.
     *
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a list of {@link Link} objects for the entire list of entity and embedded entity types
     */
    default List<Link> buildOtherLinksForEntityList(ServerWebExchange exchange) {
        return List.of();
    }

    /**
     * Creates a self-link for a list of entities and embedded entities, representing a URI that clients can use to
     * access the list directly, applicable to the entire list.
     *
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a {@link Link} object representing the self-link for the entire list of entity and embedded entity types
     */
    Link buildSelfLinkForEntityList(ServerWebExchange exchange);

}
