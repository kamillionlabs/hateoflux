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
 * @since 12.08.2024
 */

package org.kamillion.hateoflux.assembler;

import org.kamillion.hateoflux.model.link.Link;
import org.springframework.web.server.ServerWebExchange;

import java.util.ArrayList;
import java.util.List;

/**
 * Assembler module that builds links for an entity.
 *
 * @author Younes El Ouarti
 */
public sealed interface SealedEntityLinkAssemblerModule<EntityT>
        permits FlatHalWrapperAssembler, EmbeddingHalWrapperAssembler {

    /**
     * Main method for building all links for a given entity, including a self-link and other contextual links.
     * It aggregates results from {@link #buildSelfLinkForEntity} and {@link #buildOtherLinksForEntity}.
     *
     * @param entityToWrap
     *         the entity for which links are constructed
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a list of {@link Link} objects representing hypermedia links for the entity
     */
    default List<Link> buildLinksForEntity(EntityT entityToWrap, ServerWebExchange exchange) {
        List<Link> links = new ArrayList<>();
        links.add(buildSelfLinkForEntity(entityToWrap, exchange));
        links.addAll(buildOtherLinksForEntity(entityToWrap, exchange));
        return links;
    }

    /**
     * Creates a self-link for a given entity, representing a URI that clients can use to access the entity directly.
     *
     * @param entityToWrap
     *         the entity for which a self-link is created
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a {@link Link} object representing the self-link for the entity
     */
    Link buildSelfLinkForEntity(EntityT entityToWrap, ServerWebExchange exchange);

    /**
     * Provides additional contextual links for a given entity, beyond the self-link.
     *
     * @param entityToWrap
     *         the entity for which additional links are generated
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a list of {@link Link} objects representing additional hypermedia links for the entity
     */
    default List<Link> buildOtherLinksForEntity(EntityT entityToWrap, ServerWebExchange exchange) {
        return List.of();
    }

}
