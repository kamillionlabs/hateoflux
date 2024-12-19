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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import org.springframework.lang.Nullable;

/**
 * Represents pagination details in a hypermedia-driven format. This record provides necessary information to handle
 * paging of large datasets.
 *
 * @param size
 *         the requested/max number of elements in a single page
 * @param totalElements
 *         the total number of elements across all pages
 * @param totalPages
 *         the total number of pages
 * @param number
 *         the current page number, typically zero-based
 */
@Builder
@Jacksonized
@Schema(
        name = "HalPageInfo",
        description = "Pagination details for navigating through large datasets."
)
public record HalPageInfo(
        @Schema(
                description = "The requested or maximum number of elements in a single page.",
                example = "20"
        )
        Integer size,
        @Schema(
                description = "The total number of elements across all pages.",
                example = "1000"
        )
        Long totalElements,

        @Schema(
                description = "The total number of pages available based on the page size and total elements.",
                example = "50"
        )
        Integer totalPages,

        @Schema(
                description = "The current page number, typically zero-based.",
                example = "0"
        )
        Integer number) {

    /**
     * Creates a {@link HalPageInfo} instance using provided individual parameters.
     *
     * @param size
     *         the requested/max number of elements in a single page
     * @param totalElements
     *         the total number of elements across all pages
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
     * Computes pagination information based on page size, total number of elements, and an optional offset.
     *
     * @param size
     *         the requested/max number of elements in a single page
     * @param totalElements
     *         the total number of elements across all pages
     * @param number
     *         current page number
     * @return a new instance of {@code HalPageInfo}
     */
    public static HalPageInfo assembleWithPageNumber(int size, long totalElements, int number) {
        int totalPages = (int) Math.ceil((double) totalElements / size);
        return new HalPageInfo(size, totalElements, totalPages, number);
    }

    /**
     * Computes pagination information based on page size, total number of elements, and an optional offset.
     *
     * @param size
     *         the requested/max number of elements in a single page
     * @param totalElements
     *         the total number of elements across all pages
     * @param offset
     *         the offset from which to start pagination, can be null
     * @return a new instance of {@code HalPageInfo}
     */
    public static HalPageInfo assembleWithOffset(int size, long totalElements, @Nullable Long offset) {
        long offsetEffective = offset == null ? 0L : offset;

        int totalPages = (int) Math.ceil((double) totalElements / size);
        int number = (int) (offsetEffective / size);

        return new HalPageInfo(size, totalElements, totalPages, number);
    }

}
