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
 * Represents an immutable wrapper class for encapsulating embedded resources in a hypermedia-driven format, adhering to
 * HAL standards. This class complements the {@link HalResourceWrapper} and {@link HalListWrapper} by focusing
 * specifically on the management of embedded resources associated with the main resource.
 * <p>
 * The {@link HalEmbeddedWrapper} is a final class and is not intended for extension. It is designed to hold an
 * instance of {@code EmbeddedT}, representing embedded resources that provide additional context or related data to
 * the
 * primary resource. This wrapper ensures that embedded resources are correctly serialized within their designated
 * namespace in the output structure.
 * <p>
 * Instantiation of this class is usually not required by hand, as the many embed method {@link HalResourceWrapper} and
 * {@link HalListWrapper} do this automatically (e.g.
 * {@link HalResourceWrapper#withEmbeddedResource(HalEmbeddedWrapper)}.
 *
 * @param <EmbeddedT>
 *         the type of the objects being wrapped, which represent embedded resources related to the main data
 * @author Younes El Ouarti
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@JsonInclude(NON_NULL)
public final class HalEmbeddedWrapper<EmbeddedT> extends HalWrapper<HalEmbeddedWrapper<EmbeddedT>> {

    @JsonUnwrapped
    private final EmbeddedT embeddedResource;


    private HalEmbeddedWrapper(EmbeddedT embeddedResource) {
        super();
        this.embeddedResource = embeddedResource;
    }

    /**
     * Wrapper for any given resource that is desired to be put as an embedded resource in either a
     * {@link HalListWrapper}
     * or a {@link HalResourceWrapper}, ensuring it conforms to HAL standards. When serialized, the resource is placed
     * into
     * the {@code _embedded} node.
     * <p>
     * To comply with HAL, {@code EmbeddedT} must not be a collection or iterable. This enforcement helps maintain
     * the rule that collections of {@link HalEmbeddedWrapper} are created, rather than a single wrapper containing
     * multiple resources.
     * <p>
     * <b>Hint:</b><br>
     * Be aware that manual wrapping of an embedded resource is not required. When using a {@code withEmbeddedXYZ()}
     * method from {@link HalResourceWrapper} or {@link HalListWrapper}, the wrapping of the embedded resource is
     * automatically handled by these methods.
     *
     * @param <EmbeddedT>
     *         the type of the resource to be wrapped
     * @param resourceToWrap
     *         the object to wrap
     * @return a new instance containing the wrapped resource
     *
     * @throws IllegalArgumentException
     *         if {@code resourceToWrap} is null or an iterable
     */

    public static <EmbeddedT> HalEmbeddedWrapper<EmbeddedT> wrap(@NonNull EmbeddedT resourceToWrap) {
        Assert.notNull(resourceToWrap, valueNotAllowedToBeNull("Resource to embed"));
        Assert.isTrue(!(resourceToWrap instanceof Iterable<?>), valueIsNotAllowedToBeOfType("Resource to embed",
                "collection/iterable"));
        return new HalEmbeddedWrapper<>(resourceToWrap);
    }

}
