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
 * @since 18.08.2024
 */

package org.kamillion.hateoflux.utility;

/**
 * @author Younes El Ouarti
 */
public class MessageTemplates {

    public static String valueNotAllowedToBeEmpty(String valueName) {
        return String.format("%s is not allowed to be empty", valueName);
    }

    public static String valueNotAllowedToBeNull(String valueName) {
        return String.format("%s is not allowed to be null", valueName);
    }

    public static String valueIsNotAllowedToBeOfType(String valueName, String type) {
        return String.format("%s is not allowed to be of type %s", valueName, type);
    }

    public static String requiredValueWasNonExisting(String valueName) {
        return String.format("Attempted to retrieve a required, but non existing %s", valueName);
    }

}
