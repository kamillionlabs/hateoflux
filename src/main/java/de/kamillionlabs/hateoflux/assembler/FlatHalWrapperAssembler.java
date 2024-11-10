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
 * @since 17.08.2024
 */

package de.kamillionlabs.hateoflux.assembler;

import de.kamillionlabs.hateoflux.model.hal.HalListWrapper;
import de.kamillionlabs.hateoflux.model.hal.HalResourceWrapper;
import de.kamillionlabs.hateoflux.utility.SortCriteria;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Interface for managing the transformation of standalone resources into HAL-compliant representations,
 * supplemented with hypermedia links in a reactive programming context. This interface is tailored for reactive
 * environments, facilitating the enhancement of resource streams with the necessary fields and structure to comply
 * with HAL standards, enabling reactive streams of resources to become HAL-compliant.
 * <p>
 * While the interface's main focus is the transformation of reactive streams, it also comes equipped with the means to
 * transform in an imperative manner, i.e., with direct objects and, for example, lists.
 *
 * <p> Core functionalities include:
 * <ul>
 *     <li>Enhancing streams of resources to meet HAL structure requirements reactively.</li>
 *     <li>Appending hypermedia links to resources within the stream to support navigability and resource interaction
 *     in a HAL-based API reactively.</li>
 *     <li>Supporting pagination and backpressure in reactive streams when wrapping resources to provide structured
 *     navigation across large datasets reactively.</li>
 * </ul>
 *
 * @param <ResourceT>
 *         the type of the object being wrapped, which contains the main data
 * @author Younes El Ouarti
 * @see EmbeddingHalWrapperAssembler
 */
public non-sealed interface FlatHalWrapperAssembler<ResourceT> extends SealedNonReactiveFlatHalWrapperAssembler<ResourceT> {

    /**
     * Wraps the provided resources in a single {@link HalListWrapper}.
     *
     * @param resourcesToWrap
     *         resources to wrap
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return wrapped resources
     *
     * @see #wrapInListWrapper(Flux, Mono, int, Long, List, ServerWebExchange)
     */

    default Mono<HalListWrapper<ResourceT, Void>> wrapInListWrapper(@NonNull Flux<ResourceT> resourcesToWrap,
                                                                    ServerWebExchange exchange) {
        return resourcesToWrap.collectList()
                .map(resourcesToWrapValue -> wrapInListWrapper(resourcesToWrapValue, exchange));
    }

    /**
     * Wraps the provided resources in a single {@link HalListWrapper} with paging information.
     *
     * @param resourcesToWrap
     *         resources to wrap
     * @param totalElements
     *         the total number of elements across all pages
     * @param pageSize
     *         the number of items per page
     * @param offset
     *         the starting offset of the page, if specified
     * @param sortCriteria
     *         sort criteria (property and direction) of the page
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return wrapped resources
     *
     * @see #wrapInListWrapper(Flux, ServerWebExchange)
     */
    default Mono<HalListWrapper<ResourceT, Void>> wrapInListWrapper(@NonNull Flux<ResourceT> resourcesToWrap,
                                                                    @NonNull Mono<Long> totalElements,
                                                                    int pageSize,
                                                                    @Nullable Long offset,
                                                                    List<SortCriteria> sortCriteria,
                                                                    ServerWebExchange exchange) {
        Mono<List<ResourceT>> resourcesAsListMono = resourcesToWrap.collectList();
        return Mono.zip(resourcesAsListMono, totalElements,
                (resourcesValue, totalElementsValue) -> wrapInListWrapper(resourcesValue, totalElementsValue,
                        pageSize, offset, sortCriteria, exchange));
    }

    /**
     * Wraps the provided resource in a {@link HalResourceWrapper}
     *
     * @param resourceToWrap
     *         resource to wrap
     * @param exchange
     *         provides the context of the current web exchange, such as the base URL
     * @return wrapped resource
     */
    default Mono<HalResourceWrapper<ResourceT, Void>> wrapInResourceWrapper(@NonNull Mono<ResourceT> resourceToWrap,
                                                                            ServerWebExchange exchange) {

        return resourceToWrap.map(e -> wrapInResourceWrapper(e, exchange));
    }

}
