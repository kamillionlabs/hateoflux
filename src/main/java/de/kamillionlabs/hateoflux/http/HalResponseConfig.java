package de.kamillionlabs.hateoflux.http;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityResultHandler;

/**
 * Auto-configuration class for setting up {@link OldHalResponse} handling in a Spring WebFlux application. This
 * configuration automatically integrates with Spring's WebFlux infrastructure by importing
 * {@link WebFluxAutoConfiguration}  and implementing {@link WebFluxConfigurer}.
 *
 * @see HalResponseHandlerResultHandler
 * @see WebFluxAutoConfiguration
 * @see WebFluxConfigurer
 */
@AutoConfiguration
@ImportAutoConfiguration(WebFluxAutoConfiguration.class)
public class HalResponseConfig implements WebFluxConfigurer {

    /**
     * Creates a {@link HalResponseHandlerResultHandler} bean that enables proper serialization of
     * {@link OldHalResponse}
     * instances into {@link ResponseEntity} objects that Spring's response handling mechanism can process.
     *
     * @param responseEntityHandler
     *         the {@link ResponseEntityResultHandler} to delegate to after converting the {@link OldHalResponse}
     * @return a new {@link HalResponseHandlerResultHandler} instance
     */
    @Bean
    public HalResponseHandlerResultHandler halResponseHandlerResultHandler(
            ResponseEntityResultHandler responseEntityHandler) {
        return new HalResponseHandlerResultHandler(responseEntityHandler);
    }

    @Bean
    public ReactiveResponseEntityHandlerResultHandler reactiveResponseEntityHandlerResultHandler(
            ResponseEntityResultHandler responseEntityHandler) {
        return new ReactiveResponseEntityHandlerResultHandler(responseEntityHandler);
    }
}
