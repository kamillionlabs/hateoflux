package de.kamillionlabs.hateoflux.http;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

/**
 * @author Younes El Ouarti
 */
public interface ReactiveResponseEntity {

    HttpStatus DEFAULT_STATUS = HttpStatus.OK;

    Mono<ResponseEntity<?>> toResponseEntity();

}
