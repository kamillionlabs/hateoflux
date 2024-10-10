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
 * @since 18.08.2024
 */

package de.kamillionlabs.hateoflux.utility;

/**
 * Utility class providing standard message templates for validation errors.
 *
 * @author Younes El Ouarti
 */
public class ValidationMessageTemplates {

    private ValidationMessageTemplates() {
    }

    /**
     * Returns a message indicating that a value is not allowed to be empty.
     *
     * @param valueName
     *         the name of the value
     * @return the formatted message
     */
    public static String valueNotAllowedToBeEmpty(String valueName) {
        return String.format("%s is not allowed to be empty", valueName);
    }

    /**
     * Returns a message indicating that a value is not allowed to be null.
     *
     * @param valueName
     *         the name of the value
     * @return the formatted message
     */
    public static String valueNotAllowedToBeNull(String valueName) {
        return String.format("%s is not allowed to be null", valueName);
    }

    /**
     * Returns a message indicating that a value is not allowed to be of a certain type.
     *
     * @param valueName
     *         the name of the value
     * @param type
     *         the disallowed type
     * @return the formatted message
     */
    public static String valueIsNotAllowedToBeOfType(String valueName, String type) {
        return String.format("%s is not allowed to be of type %s", valueName, type);
    }

    /**
     * Returns a message indicating that a required value was non-existing.
     *
     * @param valueName
     *         the name of the required value
     * @return the formatted message
     */
    public static String requiredValueWasNonExisting(String valueName) {
        return String.format("Attempted to retrieve a required, but non-existing %s", valueName);
    }

}
