package de.kamillionlabs.hateoflux.http;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

/**
 * Represents a reactive response entity that can be converted into a standard {@link ResponseEntity}.
 *
 * <p>Implementations of this interface enable custom response types to be serialized automatically
 * by the {@link ReactiveResponseEntityHandlerResultHandler}. This facilitates seamless integration
 * of custom reactive responses within the application's response handling pipeline.</p>
 *
 * @author Younes El Ouarti
 */
public interface ReactiveResponseEntity {

    /**
     * The default HTTP status to be used when no specific status is provided. This constant is set to
     * {@link HttpStatus#OK} (200), indicating a successful request.
     */
    HttpStatus DEFAULT_STATUS = HttpStatus.OK;

    /**
     * Converts this reactive response entity into a standard {@link ResponseEntity}.
     *
     * <p>This method encapsulates the logic required to transform the custom reactive response
     * into a format that can be understood and processed by Spring's response handling mechanisms.</p>
     *
     * @return a {@link Mono} emitting the corresponding {@link ResponseEntity}
     */
    Mono<ResponseEntity<?>> toResponseEntity();

}
