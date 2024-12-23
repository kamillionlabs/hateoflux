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

import lombok.Getter;

/**
 * Enum representing the direction of sorting to be applied to a property, either ascending or descending.
 *
 * <p>The {@code SortDirection} enum provides two constants:
 * <ul>
 *     <li>{@link #ASCENDING} - indicates that sorting should be done in ascending order.</li>
 *     <li>{@link #DESCENDING} - indicates that sorting should be done in descending order.</li>
 * </ul>
 * Each constant is associated with an abbreviation, which can be used as a shorthand (e.g., "asc" for ascending).
 *
 * <p>This enum is typically used in conjunction with the {@link SortCriteria} class to specify sorting preferences.
 *
 * @author Younes El Ouarti
 */
@Getter
public enum SortDirection {

    /**
     * Sort direction representing ascending order.
     */
    ASCENDING("asc"),

    /**
     * Sort direction representing descending order.
     */
    DESCENDING("desc");

    private final String abbreviation;

    /**
     * Constructs a {@code SortDirection} with the specified abbreviation.
     *
     * @param abbreviation
     *         the abbreviation for the sort direction (e.g., "asc" or "desc").
     */
    SortDirection(String abbreviation) {
        this.abbreviation = abbreviation;
    }
}
