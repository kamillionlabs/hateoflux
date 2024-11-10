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
import de.kamillionlabs.hateoflux.utility.SortCriteria;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;

/**
 * Interface for managing the transformation of resources and their associated embedded resources into HAL-compliant
 * representations, supplemented with hypermedia links. This interface facilitates the direct enhancement of resources
 * with the necessary fields and structure to comply with HAL standards, enabling resources to become HAL-compliant.
 *
 * <p> Core functionalities include:
 * <ul>
 *     <li>Directly enhancing main resources and their embedded resources to meet HAL structure requirements.</li>
 *     <li>Appending hypermedia links to resources to support navigability and resource interaction in a HAL-based
 *     API.</li>
 *     <li>Enabling custom naming and linking definitions for collections of embedded resources through
 *     implementation.</li>
 *     <li>Supporting pagination when wrapping lists of resources to provide structured navigation across large
 *     datasets.</li>
 * </ul>
 *
 * @param <ResourceT>
 *         the type of the object being wrapped, which contains the main data
 * @param <EmbeddedT>
 *         the type of the object representing additional embedded resources related to the main data, if any
 * @author Younes El Ouarti
 */
public sealed interface SealedNonReactiveEmbeddingHalWrapperAssembler<ResourceT, EmbeddedT> extends
        SealedResourceLinkAssemblerModule<ResourceT>,
        SealedResourceListAssemblerModule<ResourceT, EmbeddedT>,
        SealedEmbeddedLinkAssemblerModule<EmbeddedT>
        permits EmbeddingHalWrapperAssembler {

    /**
     * Wraps a list of main resources with their corresponding embedded resources in a {@link HalListWrapper},
     * optionally
     * including pagination information, appending hypermedia links as defined by the assembler.
     *
     * @param resourcesToWrap
     *         the list of main resources and their corresponding embedded resources to wrap
     * @param pageInfo
     *         optional pagination information to include in the wrapper
     * @param sortCriteria
     *         sort criteria (property and direction) of the page
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a {@link HalListWrapper} enriched with hypermedia links and optional pagination details
     *
     * @see #wrapInListWrapper(PairList, long, int, Long, List, ServerWebExchange)
     * @see #wrapInListWrapper(PairList, ServerWebExchange)
     */
    default HalListWrapper<ResourceT, EmbeddedT> wrapInListWrapper(@NonNull PairList<ResourceT, EmbeddedT> resourcesToWrap,
                                                                   @Nullable HalPageInfo pageInfo,
                                                                   @Nullable List<SortCriteria> sortCriteria,
                                                                   ServerWebExchange exchange) {
        List<HalResourceWrapper<ResourceT, EmbeddedT>> listOfWrappedResourcesWithEmbedded =
                resourcesToWrap.stream()
                        .map(pair -> {
                            ResourceT resource = pair.left();
                            EmbeddedT embedded = pair.right();
                            return wrapInResourceWrapper(resource, embedded, exchange);
                        }).toList();

        HalListWrapper<ResourceT, EmbeddedT> result;

        if (listOfWrappedResourcesWithEmbedded.isEmpty()) {
            result = HalListWrapper.empty(getResourceTClass());
        } else {
            result = HalListWrapper.wrap(listOfWrappedResourcesWithEmbedded);
        }
        result.withLinks(buildLinksForResourceList(pageInfo, sortCriteria, exchange));

        if (pageInfo == null) {
            return result;
        } else {
            return result.withPageInfo(pageInfo);
        }
    }

    /**
     * Wraps a list of main resources with their corresponding embedded resources in a {@link HalListWrapper}, appending
     * hypermedia links as defined by the assembler.
     *
     * @param resourcesToWrap
     *         the list of main resources and their corresponding embedded resources to wrap
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a {@link HalListWrapper} equipped with hypermedia links for each resource and the list as a whole
     *
     * @see #wrapInListWrapper(PairList, long, int, Long, List, ServerWebExchange)
     * @see #wrapInListWrapper(PairList, HalPageInfo, List, ServerWebExchange)
     */
    default HalListWrapper<ResourceT, EmbeddedT> wrapInListWrapper(@NonNull PairList<ResourceT, EmbeddedT> resourcesToWrap,
                                                                   ServerWebExchange exchange) {
        return wrapInListWrapper(resourcesToWrap, null, null, exchange);
    }

    /**
     * Wraps a list of main resources with their corresponding embedded resources in a {@link HalListWrapper} with
     * pagination information, appending hypermedia links as defined by the assembler.
     *
     * @param resourcesToWrap
     *         the list of main resources and their corresponding embedded resources to wrap
     * @param totalElements
     *         the total number of elements across all pages
     * @param pageSize
     *         the requested/max number of elements in a single page
     * @param offset
     *         the starting offset of the page, if specified
     * @param sortCriteria
     *         sort criteria (property and direction) of the page
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a {@link HalListWrapper} with hypermedia links and pagination information
     *
     * @see #wrapInListWrapper(PairList, HalPageInfo, List, ServerWebExchange)
     * @see #wrapInListWrapper(PairList, ServerWebExchange)
     */
    default HalListWrapper<ResourceT, EmbeddedT> wrapInListWrapper(@NonNull PairList<ResourceT, EmbeddedT> resourcesToWrap,
                                                                   long totalElements,
                                                                   int pageSize,
                                                                   @Nullable Long offset,
                                                                   List<SortCriteria> sortCriteria,
                                                                   ServerWebExchange exchange) {
        HalPageInfo pageInfo = HalPageInfo.assembleWithOffset(pageSize, totalElements, offset);
        return wrapInListWrapper(resourcesToWrap, pageInfo, sortCriteria, exchange);
    }


    /**
     * Wraps a single main resource and its corresponding embedded resource in a {@link HalResourceWrapper}, appending
     * hypermedia links as defined by the assembler.
     *
     * @param resourceToWrap
     *         the main resource to wrap
     * @param embedded
     *         the embedded resource associated with the main resource
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a {@link HalResourceWrapper} with hypermedia links for the resource and its embedded counterpart
     *
     * @see #wrapInResourceWrapper(Object, List, ServerWebExchange)
     */
    default HalResourceWrapper<ResourceT, EmbeddedT> wrapInResourceWrapper(@NonNull ResourceT resourceToWrap,
                                                                           @NonNull EmbeddedT embedded,
                                                                           ServerWebExchange exchange) {
        return HalResourceWrapper.wrap(resourceToWrap)
                .withLinks(buildLinksForResource(resourceToWrap, exchange))
                .withEmbeddedResource(
                        HalEmbeddedWrapper.wrap(embedded)
                                .withLinks(buildLinksForEmbedded(embedded, exchange))
                );
    }

    /**
     * Wraps a single main resource and a non-empty list of its embedded resources in a {@link HalResourceWrapper},
     * appending
     * hypermedia links as defined by the assembler.
     *
     * @param resourceToWrap
     *         the main resource to wrap
     * @param embeddedList
     *         the non-empty list of embedded resources associated with the main resource; the list name is derived from
     *         the embedded resource's class name (see also {@link Relation})
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a {@link HalResourceWrapper} that includes the main resource and its embedded resources, all enhanced
     * with
     * hypermedia links
     *
     * @throws IllegalArgumentException
     *         if the embedded list is null or empty
     * @see #wrapInResourceWrapper(Object, Object, ServerWebExchange)
     * @see #wrapInResourceWrapper(Object, String, List, ServerWebExchange)
     * @see #wrapInResourceWrapper(Object, Class, List, ServerWebExchange)
     */
    default HalResourceWrapper<ResourceT, EmbeddedT> wrapInResourceWrapper(@NonNull ResourceT resourceToWrap,
                                                                           @NonNull List<EmbeddedT> embeddedList,
                                                                           ServerWebExchange exchange) {
        var wrappedEmbeddedList = wrapEmbeddedElementsInList(embeddedList, exchange);
        return HalResourceWrapper.wrap(resourceToWrap)
                .withLinks(buildLinksForResource(resourceToWrap, exchange))
                .withNonEmptyEmbeddedList(wrappedEmbeddedList);
    }

    /**
     * Wraps a single main resource and its list of embedded resources, identified by a directly provided list name, in
     * a
     * {@link HalResourceWrapper}, appending hypermedia links as defined by the assembler. The list is allowed to be
     * empty.
     *
     * @param resourceToWrap
     *         the main resource to wrap
     * @param embeddedListName
     *         the explicitly provided name for the list of embedded resources
     * @param embeddedList
     *         the list of embedded resources associated with the main resource, which may be empty
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a {@link HalResourceWrapper} that includes the main resource and its embedded resources, all enhanced
     * with
     * hypermedia links
     *
     * @see #wrapInResourceWrapper(Object, Object, ServerWebExchange)
     * @see #wrapInResourceWrapper(Object, Class, List, ServerWebExchange)
     * @see #wrapInResourceWrapper(Object, List, ServerWebExchange)
     */
    default HalResourceWrapper<ResourceT, EmbeddedT> wrapInResourceWrapper(@NonNull ResourceT resourceToWrap,
                                                                           @NonNull String embeddedListName,
                                                                           @NonNull List<EmbeddedT> embeddedList,
                                                                           ServerWebExchange exchange) {
        var wrappedEmbeddedList = wrapEmbeddedElementsInList(embeddedList, exchange);
        return HalResourceWrapper.wrap(resourceToWrap)
                .withLinks(buildLinksForResource(resourceToWrap, exchange))
                .withEmbeddedList(embeddedListName, wrappedEmbeddedList);
    }

    /**
     * Wraps a single main resource and its list of embedded resources, with the list name derived from the specified
     * class
     * {@code embeddedTypeAsNameOrigin}, in a {@link HalResourceWrapper}, appending hypermedia links as defined by the
     * assembler. The list may be empty.
     *
     * @param resourceToWrap
     *         the main resource to wrap
     * @param embeddedTypeAsNameOrigin
     *         the class from which the list name is derived (see also {@link Relation})
     * @param embeddedList
     *         the list of embedded resources associated with the main resource, which may be empty
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a {@link HalResourceWrapper} that includes the main resource and its derived named embedded resources,
     * all
     * enhanced with hypermedia links
     *
     * @see #wrapInResourceWrapper(Object, Object, ServerWebExchange)
     * @see #wrapInResourceWrapper(Object, String, List, ServerWebExchange)
     * @see #wrapInResourceWrapper(Object, List, ServerWebExchange)
     */
    default HalResourceWrapper<ResourceT, EmbeddedT> wrapInResourceWrapper(@NonNull ResourceT resourceToWrap,
                                                                           @NonNull Class<?> embeddedTypeAsNameOrigin,
                                                                           @NonNull List<EmbeddedT> embeddedList,
                                                                           ServerWebExchange exchange) {
        var wrappedEmbeddedList = wrapEmbeddedElementsInList(embeddedList, exchange);
        return HalResourceWrapper.wrap(resourceToWrap)
                .withLinks(buildLinksForResource(resourceToWrap, exchange))
                .withEmbeddedList(embeddedTypeAsNameOrigin, wrappedEmbeddedList);
    }

    private List<HalEmbeddedWrapper<EmbeddedT>> wrapEmbeddedElementsInList(@NonNull List<EmbeddedT> embeddedResource,
                                                                           ServerWebExchange exchange) {
        return embeddedResource.stream()
                .map(embedded -> HalEmbeddedWrapper.wrap(embedded)
                        .withLinks(buildLinksForEmbedded(embedded, exchange)))
                .toList();
    }

}
