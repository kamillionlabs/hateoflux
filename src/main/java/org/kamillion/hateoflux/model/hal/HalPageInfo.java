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
 * @since 07.07.2024
 */

package org.kamillion.hateoflux.model.hal;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import org.springframework.lang.Nullable;

import java.util.List;

/**
 * @author Younes El Ouarti
 */
@Builder
@Jacksonized
public record HalPageInfo(Integer size, Long totalElements, Integer totalPages, Integer number) {

    public static HalPageInfo of(Integer size, Long totalElements, Integer totalPages, Integer number) {
        return new HalPageInfo(size, totalElements, totalPages, number);
    }

    public static HalPageInfo assemble(List<?> entities, long totalElements, int pageSize) {
        return assemble(entities.size(), totalElements, pageSize, null);
    }

    public static HalPageInfo assemble(List<?> entities, long totalElements, int pageSize, @Nullable Long offset) {
        return assemble(entities.size(), totalElements, pageSize, offset);
    }


    public static HalPageInfo assemble(int size, long totalElements, int pageSize, @Nullable Long offset) {
        long offsetEffective = offset == null ? 0L : offset;

        int totalPages = (int) Math.ceil((double) totalElements / pageSize);
        int number = (int) (offsetEffective / pageSize);

        return new HalPageInfo(size, totalElements, totalPages, number);
    }

}
