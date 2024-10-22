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

import de.kamillionlabs.hateoflux.model.hal.HalListWrapper;
import de.kamillionlabs.hateoflux.model.hal.HalPageInfo;
import de.kamillionlabs.hateoflux.model.hal.HalResourceWrapper;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.server.ServerWebExchange;

import java.util.List;

/**
 * Interface for managing the transformation of standalone resources into HAL-compliant representations,
 * supplemented with hypermedia links. This interface facilitates the direct enhancement of resources with the necessary
 * fields and structure to comply with HAL standards, enabling resources to become HAL-compliant.
 *
 * <p> Core functionalities include:
 * <ul>
 *     <li>Directly enhancing resources to meet HAL structure requirements.</li>
 *     <li>Appending hypermedia links to resources to support navigability and resource interaction in a HAL-based
 *     API.</li>
 *     <li>Supporting pagination when wrapping lists of resources to provide structured navigation across large
 *     datasets.</li>
 * </ul>
 * <p>
 * This interface abstracts the tasks associated with modifying resources to fit HAL specifications, streamlining the
 * creation of HAL-compliant resource representations.
 *
 * <p>See also:
 * <ul>
 *    <li>{@link EmbeddingHalWrapperAssembler} - for imperative (non-reactive) handling of resources <b>with</b>
 *    embedded resources.</li>
 *    <li>{@link ReactiveEmbeddingHalWrapperAssembler} - for reactive <b>and</b> imperative handling of resources
 *    <b>with</b> embedded resources.</li>
 *    <li>{@link ReactiveFlatHalWrapperAssembler} - for reactive <b>and</b> imperative handling of standalone
 *    resources <b>without</b> embedded resources.</li>
 * </ul>
 *
 * @param <ResourceT>
 *         the type of the object being wrapped, which contains the main data
 * @author Younes El Ouarti
 */
public non-sealed interface FlatHalWrapperAssembler<ResourceT> extends
        SealedResourceLinkAssemblerModule<ResourceT>,
        SealedResourceListAssemblerModule<ResourceT, Void> {


    /**
     * Wraps a list of resources into a {@link HalListWrapper}, enhancing them with hypermedia links as defined by the
     * assembler.
     *
     * @param resourcesToWrap
     *         the list of resources to be wrapped
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a {@link HalListWrapper} that includes the wrapped resources enhanced with hypermedia links
     *
     * @see #wrapInListWrapper(List, long, int, Long, ServerWebExchange)
     * @see #wrapInListWrapper(List, HalPageInfo, ServerWebExchange)
     */
    default HalListWrapper<ResourceT, Void> wrapInListWrapper(@NonNull List<ResourceT> resourcesToWrap,
                                                              ServerWebExchange exchange) {
        return wrapInListWrapper(resourcesToWrap, null, exchange);
    }

    /**
     * Wraps a list of resources into a {@link HalListWrapper} with pagination details, enhancing them with hypermedia
     * links as defined by the assembler.
     *
     * @param resourcesToWrap
     *         the list of resources to be wrapped
     * @param totalElements
     *         the total number of elements across all pages
     * @param pageSize
     *         the number of items per page
     * @param offset
     *         the starting offset of the page, if specified
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a {@link HalListWrapper} that includes the wrapped resources enhanced with hypermedia links, along with
     * pagination information
     *
     * @see #wrapInListWrapper(List, HalPageInfo, ServerWebExchange)
     * @see #wrapInListWrapper(List, ServerWebExchange)
     */
    default HalListWrapper<ResourceT, Void> wrapInListWrapper(@NonNull List<ResourceT> resourcesToWrap,
                                                              long totalElements,
                                                              int pageSize,
                                                              @Nullable Long offset,
                                                              ServerWebExchange exchange) {
        HalPageInfo pageInfo = HalPageInfo.assemble(resourcesToWrap, totalElements, pageSize, offset);
        return wrapInListWrapper(resourcesToWrap, pageInfo, exchange);
    }

    /**
     * Wraps a list of resources into a {@link HalListWrapper}, optionally including pagination information, and
     * enhances
     * them with hypermedia links as defined by the assembler.
     *
     * @param resourcesToWrap
     *         the list of resources to be wrapped
     * @param pageInfo
     *         optional pagination information to include in the wrapper
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a {@link HalListWrapper} that includes the wrapped resources enhanced with hypermedia links, and
     * optionally pagination details
     *
     * @see #wrapInListWrapper(List, long, int, Long, ServerWebExchange)
     * @see #wrapInListWrapper(List, ServerWebExchange)
     */
    default HalListWrapper<ResourceT, Void> wrapInListWrapper(@NonNull List<ResourceT> resourcesToWrap,
                                                              @Nullable HalPageInfo pageInfo,
                                                              ServerWebExchange exchange) {
        List<HalResourceWrapper<ResourceT, Void>> listOfWrappedResources =
                resourcesToWrap.stream()
                        .map(resource -> wrapInResourceWrapper(resource, exchange))
                        .toList();

        HalListWrapper<ResourceT, Void> result = HalListWrapper.wrap(listOfWrappedResources)
                .withLinks(buildLinksForResourceList(exchange));

        if (pageInfo == null) {
            return result;
        } else {
            return result.withPageInfo(pageInfo);
        }
    }

    /**
     * Wraps a single resource in a {@link HalResourceWrapper} and enhances it with hypermedia links as defined by the
     * assembler.
     *
     * @param resourceToWrap
     *         the resource to wrap
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return a {@link HalResourceWrapper} that includes the wrapped resource enhanced with hypermedia links
     */
    default HalResourceWrapper<ResourceT, Void> wrapInResourceWrapper(@NonNull ResourceT resourceToWrap,
                                                                      ServerWebExchange exchange) {
        return HalResourceWrapper.wrap(resourceToWrap)
                .withLinks(buildLinksForResource(resourceToWrap, exchange));
    }

}
