package de.kamillionlabs.hateoflux.http;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.context.annotation.Bean;
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

    @Bean
    public ReactiveResponseEntityHandlerResultHandler reactiveResponseEntityHandlerResultHandler(
            ResponseEntityResultHandler responseEntityHandler) {
        return new ReactiveResponseEntityHandlerResultHandler(responseEntityHandler);
    }
}
