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
import de.kamillionlabs.hateoflux.model.hal.HalPageInfo;
import de.kamillionlabs.hateoflux.model.hal.Relation;
import de.kamillionlabs.hateoflux.model.link.Link;
import de.kamillionlabs.hateoflux.utility.SortCriteria;
import org.springframework.lang.NonNull;
import org.springframework.web.server.ServerWebExchange;

import java.util.ArrayList;
import java.util.List;

/**
 * Assembler module that has utility functions and builds links for a list of resources.
 *
 * @param <ResourceT>
 *         the type of the object being wrapped, which contains the main data
 * @param <EmbeddedT>
 *         the type of the object representing additional embedded resources related to the main data, if any
 * @author Younes El Ouarti
 */
public sealed interface SealedResourceListAssemblerModule<ResourceT, EmbeddedT> permits
        FlatHalWrapperAssembler, EmbeddingHalWrapperAssembler {


    /**
     * Creates an empty {@link HalListWrapper} including hypermedia links applicable to the entire list.
     *
     * @param listItemTypeAsNameOrigin
     *         the class of the resource from which the list name is derived, typically pluralized to represent both
     *         resource and embedded resource types (also see {@link Relation})
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return an initialized {@link HalListWrapper} with relevant hypermedia links for the entire list
     *
     * @see #createEmptyListWrapper(String, ServerWebExchange)
     */
    default HalListWrapper<ResourceT, EmbeddedT> createEmptyListWrapper(@NonNull Class<?> listItemTypeAsNameOrigin,
                                                                        ServerWebExchange exchange) {
        HalListWrapper<ResourceT, EmbeddedT> emptyWrapper = HalListWrapper.empty(listItemTypeAsNameOrigin);
        return emptyWrapper.withLinks(buildLinksForResourceList(exchange));
    }

    /**
     * Creates a paged empty {@link HalListWrapper} including hypermedia links applicable to the entire list.
     *
     * @param listItemTypeAsNameOrigin
     *         the class of the resource from which the list name is derived, typically pluralized to represent both
     *         resource and embedded resource types (also see {@link Relation})
     * @param pageSize
     *         the requested/max number of elements in a single page
     * @param pageNumber
     *         the current page number
     * @param sortCriteria
     *         sort criteria (property and direction) of the page
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return an initialized {@link HalListWrapper} with relevant hypermedia links for the entire list
     *
     * @see #createEmptyListWrapper(String, ServerWebExchange)
     */
    default HalListWrapper<ResourceT, EmbeddedT> createEmptyListWrapper(@NonNull Class<?> listItemTypeAsNameOrigin,
                                                                        int pageSize,
                                                                        int pageNumber,
                                                                        List<SortCriteria> sortCriteria,
                                                                        ServerWebExchange exchange) {
        HalListWrapper<ResourceT, EmbeddedT> emptyWrapper = HalListWrapper.empty(listItemTypeAsNameOrigin);
        HalPageInfo halPageInfo = HalPageInfo.assembleWithPageNumber(pageSize, 0, pageNumber);
        return emptyWrapper.withLinks(buildLinksForResourceList(halPageInfo, sortCriteria, exchange))
                .withPageInfo(halPageInfo);
    }

    /**
     * Creates an empty {@link HalListWrapper} including hypermedia links applicable to the entire list.
     *
     * @param listName
     *         the given name for the list
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return an initialized {@link HalListWrapper} with relevant hypermedia links for the entire list
     *
     * @see #createEmptyListWrapper(Class, ServerWebExchange)
     */
    default HalListWrapper<ResourceT, EmbeddedT> createEmptyListWrapper(@NonNull String listName,
                                                                        ServerWebExchange exchange) {
        HalListWrapper<ResourceT, EmbeddedT> emptyWrapper = HalListWrapper.empty(listName);
        return emptyWrapper.withLinks(buildLinksForResourceList(exchange));
    }

    /**
     * Creates an empty {@link HalListWrapper} including hypermedia links applicable to the entire list.
     *
     * @param listName
     *         the given name for the list
     * @param pageSize
     *         the requested/max number of elements in a single page
     * @param pageNumber
     *         the current page number
     * @param sortCriteria
     *         sort criteria (property and direction) of the page
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return an initialized {@link HalListWrapper} with relevant hypermedia links for the entire list
     *
     * @see #createEmptyListWrapper(Class, ServerWebExchange)
     */
    default HalListWrapper<ResourceT, EmbeddedT> createEmptyListWrapper(@NonNull String listName,
                                                                        int pageSize,
                                                                        int pageNumber,
                                                                        List<SortCriteria> sortCriteria,
                                                                        ServerWebExchange exchange) {
        HalListWrapper<ResourceT, EmbeddedT> emptyWrapper = HalListWrapper.empty(listName);
        HalPageInfo halPageInfo = HalPageInfo.assembleWithPageNumber(pageSize, 0, pageNumber);
        return emptyWrapper.withLinks(buildLinksForResourceList(halPageInfo, sortCriteria, exchange));
    }

    /**
     * Main method for building all links for a list of resources and embedded resources, including a self-link and
     * other  contextual links applicable to the entire list. It aggregates results from
     * {@link #buildSelfLinkForResourceList} and {@link #buildOtherLinksForResourceList}.
     *
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a list of {@link Link} objects representing hypermedia links for the entire list of resource and embedded
     * resource types
     */
    default List<Link> buildLinksForResourceList(ServerWebExchange exchange) {
        List<Link> links = new ArrayList<>();
        links.add(buildSelfLinkForResourceList(exchange).withSelfRel());
        links.addAll(buildOtherLinksForResourceList(exchange));
        return links;
    }

    /**
     * Main method for building all links for a paged list of resources and embedded resources, including
     * navigational links and other contextual links applicable to the entire list. It aggregates results from
     * {@link #buildSelfLinkForResourceList} and {@link #buildOtherLinksForResourceList}.
     *
     * @param pageInfo
     *         pagination information about the request page
     * @param sortCriteria
     *         sort criteria (property and direction) of the page
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a list of {@link Link} objects representing hypermedia links for the entire list of resource and embedded
     * resource types
     */
    default List<Link> buildLinksForResourceList(HalPageInfo pageInfo,
                                                 List<SortCriteria> sortCriteria,
                                                 ServerWebExchange exchange) {
        List<Link> links = new ArrayList<>();
        Link baseLink = buildSelfLinkForResourceList(exchange).withSelfRel();
        List<Link> navigationLinks;
        if (sortCriteria == null) {
            navigationLinks = baseLink.deriveNavigationLinks(pageInfo);
        } else {
            navigationLinks = baseLink.deriveNavigationLinks(pageInfo, sortCriteria);
        }
        links.addAll(navigationLinks);
        links.addAll(buildOtherLinksForResourceList(exchange));
        return links;
    }

    /**
     * Provides additional contextual links for a list of resources and embedded resources, beyond the self-link,
     * applicable to the entire list.
     *
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a list of {@link Link} objects for the entire list of resource and embedded resource types
     */
    default List<Link> buildOtherLinksForResourceList(ServerWebExchange exchange) {
        return List.of();
    }

    /**
     * Creates a self-link for a list of resources and embedded resources, representing a URI that clients can use to
     * access the list directly, applicable to the entire list.
     *
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a {@link Link} object representing the self-link for the entire list of resource and embedded resource
     * types
     */
    Link buildSelfLinkForResourceList(ServerWebExchange exchange);

}
