/*
 * Copyright (c)  2024 kamillion labs contributors
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
 * @since 28.10.2024
 */

package de.kamillionlabs.hateoflux.utility;

/**
 * Represents sorting criteria for a specific property in a collection of data, allowing results
 * to be ordered by the specified property and direction.
 *
 * <p>The {@code SortCriteria} record encapsulates the sorting behavior for a given property, which includes:
 * <ul>
 *     <li>The property to sort by (e.g., a field name in an entity).</li>
 *     <li>The sorting direction (e.g., ascending or descending), represented by {@link SortDirection}.</li>
 * </ul>
 *
 * <p>This record is typically used to specify sorting preferences when retrieving paginated data.
 *
 * @param property
 *         the name of the property to sort by. Must not be null.
 * @param direction
 *         the {@link SortDirection} indicating whether to sort in ascending or descending order.
 * @author Younes El Ouarti
 */
public record SortCriteria(String property, SortDirection direction) {

    /**
     * Creates a new {@code SortCriteria} instance with the specified property and direction.
     *
     * @param property
     *         the name of the property to sort by. Must not be null.
     * @param direction
     *         the {@link SortDirection} indicating whether to sort in ascending or descending order.
     * @return a new {@code SortCriteria} instance configured with the given property and direction.
     */
    public static SortCriteria by(String property, SortDirection direction) {
        return new SortCriteria(property, direction);
    }
}
