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

import de.kamillionlabs.hateoflux.model.link.Link;
import org.springframework.web.server.ServerWebExchange;

import java.util.ArrayList;
import java.util.List;

/**
 * Assembler module that builds links for an embedded entity.
 *
 * @param <EmbeddedT>
 *         the type of the object representing additional embedded resources related to the main data
 * @author Younes El Ouarti
 */
public sealed interface SealedEmbeddedLinkAssemblerModule<EmbeddedT> permits EmbeddingHalWrapperAssembler {

    /**
     * Main method for building all links for a given embedded entity, including a self-link and other contextual links.
     * It aggregates results from {@link #buildSelfLinkForEmbedded} and {@link #buildOtherLinksForEmbedded}.
     *
     * @param embedded
     *         the embedded entity for which links are constructed
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a list of {@link Link} objects representing hypermedia links for the embedded entity
     */
    default List<Link> buildLinksForEmbedded(EmbeddedT embedded, ServerWebExchange exchange) {
        List<Link> links = new ArrayList<>();
        links.add(buildSelfLinkForEmbedded(embedded, exchange).withSelfRel());
        links.addAll(buildOtherLinksForEmbedded(embedded, exchange));
        return links;
    }

    /**
     * Creates a self-link for a given embedded entity, representing a URI that clients can use to access the entity
     * directly.
     *
     * @param embedded
     *         the embedded entity for which a self-link is created
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a {@link Link} object representing the self-link for the embedded entity
     */
    Link buildSelfLinkForEmbedded(EmbeddedT embedded, ServerWebExchange exchange);

    /**
     * Provides additional contextual links for a given embedded entity, beyond the self-link.
     *
     * @param embedded
     *         the embedded entity for which additional links are generated
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a list of {@link Link} objects representing additional hypermedia links for the embedded entity
     */
    default List<Link> buildOtherLinksForEmbedded(EmbeddedT embedded, ServerWebExchange exchange) {
        return List.of();
    }

}
