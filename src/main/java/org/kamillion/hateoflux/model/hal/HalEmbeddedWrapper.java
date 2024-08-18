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
 * @since 14.07.2024
 */

package org.kamillion.hateoflux.model.hal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
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

    public static <EmbeddedT> HalEmbeddedWrapper<EmbeddedT> wrap(@NonNull EmbeddedT entityToWrap) {
        Assert.notNull(entityToWrap, "Entity to embed is not allowed to be null");
        Assert.isTrue(!(entityToWrap instanceof Iterable<?>), "Entity to embed is not allowed to be a " +
                "collection/iterable");
        return new HalEmbeddedWrapper<>(entityToWrap);
    }

}
