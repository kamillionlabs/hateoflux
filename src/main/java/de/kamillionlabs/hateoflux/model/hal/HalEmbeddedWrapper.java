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
 * @since 14.07.2024
 */

package de.kamillionlabs.hateoflux.model.hal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static de.kamillionlabs.hateoflux.utility.ValidationMessageTemplates.valueIsNotAllowedToBeOfType;
import static de.kamillionlabs.hateoflux.utility.ValidationMessageTemplates.valueNotAllowedToBeNull;

/**
 * Represents an immutable wrapper class for encapsulating embedded entities in a hypermedia-driven format, adhering to
 * HAL standards. This class complements the {@link HalEntityWrapper} and {@link HalListWrapper} by focusing
 * specifically on the management of embedded entities associated with the main entity.
 * <p>
 * The {@link HalEmbeddedWrapper} is a final class and is not intended for extension. It is designed to hold an
 * instance of {@code EmbeddedT}, representing embedded entities that provide additional context or related data to the
 * primary entity. This wrapper ensures that embedded entities are correctly serialized within their designated
 * namespace in the output structure.
 * <p>
 * Instantiation of this class is usually not required by hand, as the many embed method {@link HalEntityWrapper} and
 * {@link HalListWrapper} do this automatically (e.g. {@link HalEntityWrapper#withEmbeddedEntity(HalEmbeddedWrapper)}.
 *
 * @param <EmbeddedT>
 *         the type of the objects being wrapped, which represent embedded entities related to the main data
 * @author Younes El Ouarti
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@JsonInclude(NON_NULL)
public final class HalEmbeddedWrapper<EmbeddedT> extends HalWrapper<HalEmbeddedWrapper<EmbeddedT>> {

    @JsonUnwrapped
    private final EmbeddedT embeddedEntity;


    private HalEmbeddedWrapper(EmbeddedT embeddedEntity) {
        super();
        this.embeddedEntity = embeddedEntity;
    }

    /**
     * Wrapper for any given entity that is desired to be put as an embedded entity in a {@link HalListWrapper} or
     * {@link HalEntityWrapper}, to make it conform to HAL standards.When serialized, the entity is put into the
     * {@code _embedded} node.
     * <p>
     * In order to comply with HAL, {@code EmbeddedT} is not allowed to be a collection/iterable. This helps enforce the
     * rule that collections of {@link HalEmbeddedWrapper} are created instead of a single wrapper that contains
     * multiple entities.
     *
     * <p>
     * <b>Hint</b><br>
     * Be aware, that the manual wrapping of an embedded entity is not required. If a {@code withEmbeddedXYZ()} of
     * {@link HalEntityWrapper} or {@link HalListWrapper} is used, the wrapping of the embedded entity is done
     * automatically with those methods.
     *
     * @param <EmbeddedT>
     *         the type of the entity to be wrapped
     * @param entityToWrap
     *         the object to wrap
     * @return a new instance containing the wrapped entity
     *
     * @throws IllegalArgumentException
     *         if {@code entityToWrap} is null or an iterable
     */

    /**
     * Wrapper for any given entity that is desired to be put as an embedded entity in either a {@link HalListWrapper}
     * or a {@link HalEntityWrapper}, ensuring it conforms to HAL standards. When serialized, the entity is placed into
     * the {@code _embedded} node.
     * <p>
     * To comply with HAL, {@code EmbeddedT} must not be a collection or iterable. This enforcement helps maintain
     * the rule that collections of {@link HalEmbeddedWrapper} are created, rather than a single wrapper containing
     * multiple entities.
     * <p>
     * <b>Hint:</b><br>
     * Be aware that manual wrapping of an embedded entity is not required. When using a {@code withEmbeddedXYZ()}
     * method from {@link HalEntityWrapper} or {@link HalListWrapper}, the wrapping of the embedded entity is
     * automatically handled by these methods.
     *
     * @param <EmbeddedT>
     *         the type of the entity to be wrapped
     * @param entityToWrap
     *         the object to wrap
     * @return a new instance containing the wrapped entity
     *
     * @throws IllegalArgumentException
     *         if {@code entityToWrap} is null or an iterable
     */

    public static <EmbeddedT> HalEmbeddedWrapper<EmbeddedT> wrap(@NonNull EmbeddedT entityToWrap) {
        Assert.notNull(entityToWrap, valueNotAllowedToBeNull("Entity to embed"));
        Assert.isTrue(!(entityToWrap instanceof Iterable<?>), valueIsNotAllowedToBeOfType("Entity to embed",
                "collection/iterable"));
        return new HalEmbeddedWrapper<>(entityToWrap);
    }

}
