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
 * Assembler module that builds links for an resource.
 *
 * @param <ResourceT>
 *         the type of the object being wrapped, which contains the main data
 * @author Younes El Ouarti
 */
public sealed interface SealedResourceLinkAssemblerModule<ResourceT>
        permits SealedNonReactiveFlatHalWrapperAssembler, SealedNonReactiveEmbeddingHalWrapperAssembler {

    /**
     * Main method for building all links for a given resource, including a self-link and other contextual links.
     * It aggregates results from {@link #buildSelfLinkForResource} and {@link #buildOtherLinksForResource}.
     *
     * @param resourceToWrap
     *         the resource for which links are constructed
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a list of {@link Link} objects representing hypermedia links for the resource
     */
    default List<Link> buildLinksForResource(ResourceT resourceToWrap, ServerWebExchange exchange) {
        List<Link> links = new ArrayList<>();
        links.add(buildSelfLinkForResource(resourceToWrap, exchange).withSelfRel());
        links.addAll(buildOtherLinksForResource(resourceToWrap, exchange));
        return links;
    }

    /**
     * Creates a self-link for a given resource, representing a URI that clients can use to access the resource
     * directly.
     *
     * @param resourceToWrap
     *         the resource for which a self-link is created
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a {@link Link} object representing the self-link for the resource
     */
    Link buildSelfLinkForResource(ResourceT resourceToWrap, ServerWebExchange exchange);

    /**
     * Provides additional contextual links for a given resource, beyond the self-link.
     *
     * @param resourceToWrap
     *         the resource for which additional links are generated
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a list of {@link Link} objects representing additional hypermedia links for the resource
     */
    default List<Link> buildOtherLinksForResource(ResourceT resourceToWrap, ServerWebExchange exchange) {
        return List.of();
    }

}
