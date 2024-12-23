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
 */
package de.kamillionlabs.hateoflux.http;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityResultHandler;

/**
 * Auto-configuration class for enabling serialization of {@link ReactiveResponseEntity} instances. This config adds the
 * {@link ReactiveResponseEntityHandlerResultHandler} to enable the serialization of {@link ReactiveResponseEntity}
 *
 * @author Younes El Ouarti
 */
@AutoConfiguration
@ImportAutoConfiguration(WebFluxAutoConfiguration.class)
public class ReactiveResponseEntityConfig implements WebFluxConfigurer {

    /**
     * Creates and registers a {@link ReactiveResponseEntityHandlerResultHandler} bean within the Spring application
     * context.
     *
     * <p>The {@code ReactiveResponseEntityHandlerResultHandler} is responsible for handling and serializing
     * {@link ReactiveResponseEntity} instances in reactive web responses. It leverages the provided
     * {@link ResponseEntityResultHandler} to delegate the processing of standard {@link ResponseEntity} objects,
     * ensuring consistency and integration with existing response handling mechanisms.
     *
     * @param responseEntityHandler
     *         the existing {@link ResponseEntityResultHandler} bean provided by Spring WebFlux for handling standard
     *         {@link ResponseEntity} instances
     * @return a new instance of {@link ReactiveResponseEntityHandlerResultHandler} configured with the provided
     * {@code responseEntityHandler}
     */
    @Bean
    public ReactiveResponseEntityHandlerResultHandler reactiveResponseEntityHandlerResultHandler(
            ResponseEntityResultHandler responseEntityHandler) {
        return new ReactiveResponseEntityHandlerResultHandler(responseEntityHandler);
    }
}
