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
 * @since 05.06.2024
 */

package de.kamillionlabs.hateoflux.linkbuilder;

import org.reactivestreams.Publisher;

/**
 * Functional interface for method references to a controller method in the context of URI generation. This interface
 * enables type-safe referencing of specific methods within a controller when constructing links with
 * {@link SpringControllerLinkBuilder}.
 * <p>
 * It facilitates the {@link SpringControllerLinkBuilder#linkTo(Class, ControllerMethodReference)}  method in accepting
 * controller method references, which allows dynamic construction of URIs based on the actual method signatures and
 * runtime values of their parameters.
 *
 * @param <ControllerT>
 *         the type of the controller that contains the method being referenced
 * @author Younes El Ouarti
 */
@FunctionalInterface
public interface ControllerMethodReference<ControllerT> {
    /**
     * Method to invoke
     *
     * @param controller
     *         Controller class of which the method should be invoked
     * @return the publisher is unused and can be safely ignored
     *
     * @see SpringControllerLinkBuilder#linkTo(Class, ControllerMethodReference)
     */
    Publisher<?> invoke(ControllerT controller);
}
