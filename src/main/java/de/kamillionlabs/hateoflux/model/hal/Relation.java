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
 * @since 23.06.2024
 */

package de.kamillionlabs.hateoflux.model.hal;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Identical to Spring's Hateoas {@code @Relation} annotation. It configures the name/relation to be used when embedding
 * objects in HAL representations of {@link HalResourceWrapper} and {@link HalListWrapper}.
 *
 * @author Younes El Ouarti
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Relation {

    /**
     * Defines the relation to be used when referring to a single resource. Alias for {@link #itemRelation()}.
     *
     * @return the relation name for a single resource
     */
    @AliasFor("itemRelation")
    String value() default "";

    /**
     * Defines the relation to be used when referring to a single resource. Alias of {@link #value()}.
     *
     * @return the relation name for a single resource
     */
    @AliasFor("value")
    String itemRelation() default "";

    /**
     * Defines the relation to be used when referring to a collection of resources.
     *
     * @return the relation name for a collection of resources
     */
    String collectionRelation() default "";
}
