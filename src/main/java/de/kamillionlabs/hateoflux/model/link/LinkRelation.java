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
 * @since 24.05.2024
 */

package de.kamillionlabs.hateoflux.model.link;

import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * Represents a link relation type as defined in RFC 8288.
 * <p>
 * A link relation can be a standard IANA registered relation or a custom one.
 *
 * @author Younes El Ouarti
 */
@EqualsAndHashCode
@Value
@Schema(
        name = "LinkRelation",
        description = "Represents a link relation type as defined in RFC 8288. It can be a standard IANA registered " +
                "relation or a custom one.",
        type = "string"
)
public class LinkRelation {

    /**
     * The relationship type as a string (e.g., "self", "next").
     */
    @JsonValue
    String relation;

    private LinkRelation(String relation) {
        this.relation = relation;
    }

    /**
     * Creates a new {@link LinkRelation} from the given relation name.
     *
     * @param relation
     *         the link relation name
     * @return a new {@link LinkRelation} instance
     */
    public static LinkRelation of(String relation) {
        return new LinkRelation(relation);
    }

    /**
     * Creates a new {@link LinkRelation} from a given {@link IanaRelation}.
     *
     * @param ianaRelation
     *         the IANA registered link relation
     * @return a new {@link LinkRelation} instance
     */
    public static LinkRelation of(IanaRelation ianaRelation) {
        return new LinkRelation(ianaRelation.getName());
    }

}
