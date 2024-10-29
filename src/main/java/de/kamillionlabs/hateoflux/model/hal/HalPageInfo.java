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
 * @since 07.07.2024
 */

package de.kamillionlabs.hateoflux.model.hal;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * Represents pagination details in a hypermedia-driven format. This record provides necessary information to handle
 * paging of large datasets, including the size of the current page, the total number of elements across all pages, the
 * total number of pages, and the current page number.
 *
 * @param size
 *         the number of elements in the current page
 * @param totalElements
 *         the total number of elements across all pages
 * @param totalPages
 *         the total number of pages
 * @param number
 *         the current page number, typically zero-based
 */
@Builder
@Jacksonized
public record HalPageInfo(Integer size, Long totalElements, Integer totalPages, Integer number) {

    /**
     * Creates a {@link HalPageInfo} instance using provided individual parameters.
     *
     * @param size
     *         the size of the page
     * @param totalElements
     *         the total number of elements
     * @param totalPages
     *         the total number of pages calculated from total elements and page size
     * @param number
     *         the current page number
     * @return a new instance of {@link HalPageInfo}
     */
    public static HalPageInfo of(Integer size, Long totalElements, Integer totalPages, Integer number) {
        return new HalPageInfo(size, totalElements, totalPages, number);
    }

    /**
     * Computes pagination information based on a list of resources, total number of elements, and a given page size.
     * This method assumes the initial page (offset is null).
     *
     * @param resourcesForCurrentPageSizeCalculation
     *         the list of resources from which to calculate the current page size
     * @param totalElements
     *         the total number of elements
     * @param pageSize
     *         the size of each page
     * @return a new instance of {@link HalPageInfo}
     */
    public static HalPageInfo assemble(@NonNull List<?> resourcesForCurrentPageSizeCalculation, long totalElements,
                                       int pageSize) {
        return assemble(resourcesForCurrentPageSizeCalculation.size(), totalElements, pageSize, null);
    }

    /**
     * Computes pagination information based on a list of resources, total number of elements, page size, and an
     * optional
     * offset marking the start of pagination.
     *
     * @param resourcesForCurrentPageSizeCalculation
     *         the list of resources from which to calculate the current page size
     * @param totalElements
     *         the total number of elements
     * @param pageSize
     *         the size of each page
     * @param offset
     *         the offset from which to start pagination, can be null
     * @return a new instance of {@link HalPageInfo}
     */
    public static HalPageInfo assemble(@NonNull List<?> resourcesForCurrentPageSizeCalculation, long totalElements,
                                       int pageSize,
                                       @Nullable Long offset) {
        return assemble(resourcesForCurrentPageSizeCalculation.size(), totalElements, pageSize, offset);
    }


    /**
     * Computes pagination information based on size, total number of elements, page size, and an optional offset.
     *
     * @param size
     *         the current page size calculated from resources list
     * @param totalElements
     *         the total number of elements
     * @param pageSize
     *         the size of each page
     * @param offset
     *         the offset from which to start pagination, can be null
     * @return a new instance of {@code HalPageInfo}
     */
    public static HalPageInfo assemble(int size, long totalElements, int pageSize, @Nullable Long offset) {
        long offsetEffective = offset == null ? 0L : offset;

        int totalPages = (int) Math.ceil((double) totalElements / pageSize);
        int number = (int) (offsetEffective / pageSize);

        return new HalPageInfo(size, totalElements, totalPages, number);
    }

}
