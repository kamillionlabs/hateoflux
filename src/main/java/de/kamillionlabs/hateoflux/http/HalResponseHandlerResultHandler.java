package de.kamillionlabs.hateoflux.http;

import org.reactivestreams.Publisher;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.ResolvableType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.HandlerResult;
import org.springframework.web.reactive.HandlerResultHandler;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityResultHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;

/**
 * A specialized {@link HandlerResultHandler} implementation that processes HAL (Hypertext Application Language)
 * responses
 * in a Spring WebFlux application. This handler is responsible for converting {@link OldHalResponse} objects and their
 * publisher variants into appropriate {@link ResponseEntity} instances that can be handled by Spring's standard
 * response handling mechanism.
 *
 * <p>The handler supports both direct {@link OldHalResponse} objects and reactive types ({@link Publisher},
 * {@link Mono}, {@link Flux}) that contain {@link OldHalResponse} objects. It delegates the actual response
 * handling to Spring's {@link ResponseEntityResultHandler} after performing the necessary conversions.
 *
 * <p>This handler is ordered with a priority of -99 to ensure it runs before standard Spring handlers
 * (which typically have order 0) while still allowing for custom handlers to take precedence if needed.
 *
 * @author Younes El Ouarti
 * @see HandlerResultHandler
 * @see ResponseEntityResultHandler
 * @see OldHalResponse
 */
public class HalResponseHandlerResultHandler implements HandlerResultHandler, Ordered {

    /**
     * The order value for this handler. Set to -99 to run before standard Spring handlers (order 0)
     * while still allowing custom handlers to take precedence if needed.
     *
     * @see Ordered
     */
    int ORDER = -99;

    private final ResponseEntityResultHandler responseEntityHandler;

    private final MethodParameter responseEntityReturnType;

    /**
     * Constructs a new {@link HalResponseHandlerResultHandler} with the specified {@link ResponseEntityResultHandler}.
     *
     * @param responseEntityHandler
     *         the Spring response entity handler to delegate to after conversion
     */
    public HalResponseHandlerResultHandler(ResponseEntityResultHandler responseEntityHandler) {
        this.responseEntityHandler = responseEntityHandler;
        try {
            Method methodWithResponseEntityAsReturnType = ResponseEntityTypeProvider.class
                    .getMethod("returnResponseEntity");
            int returnTypeIndex = -1;
            this.responseEntityReturnType = new MethodParameter(methodWithResponseEntityAsReturnType, returnTypeIndex);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Failed to find dummy method for ResponseEntity return type", e);
        }
    }

    /**
     * Determines whether this handler can process the given result.
     *
     * <p>This method returns true if:
     * <ul>
     *   <li>The result is a direct {@link OldHalResponse} instance</li>
     *   <li>The result is a {@link Publisher} (including {@link Mono} or {@link Flux}) containing
     *   {@link OldHalResponse} instances</li>
     * </ul>
     *
     * @param result
     *         the handler result to check
     * @return {@code true} if this handler can process the result; {@code false} otherwise
     */
    @Override
    public boolean supports(HandlerResult result) {
        ResolvableType returnType = result.getReturnType();
        Class<?> rawClassOfReturnType = returnType.toClass();

        // Direct HalResponse?
        if (OldHalResponse.class.isAssignableFrom(rawClassOfReturnType)) {
            return true;
        }

        // Check if it's a Publisher<HalResponse>
        if (Publisher.class.isAssignableFrom(rawClassOfReturnType)) {
            ResolvableType genericType = returnType.getGeneric(0);
            Class<?> genericClass = genericType.toClass();
            return OldHalResponse.class.isAssignableFrom(genericClass);
        }

        return false;
    }

    /**
     * Handles the result by converting {@link OldHalResponse} instances to {@link ResponseEntity} instances and
     * delegating
     * to the {@link ResponseEntityResultHandler}.
     *
     * <p>This method handles:
     * <ul>
     *   <li>Direct {@link OldHalResponse} instances by converting them to {@link ResponseEntity}</li>
     *   <li>{@link Publisher} instances containing {@link OldHalResponse} objects by converting the stream
     *       to contain {@link ResponseEntity} objects</li>
     * </ul>
     *
     * @param exchange
     *         the current server exchange
     * @param result
     *         the handler result to process
     * @return a {@code Mono<@link Void>} that completes when handling is complete
     *
     * @throws IllegalStateException
     *         if the handler cannot process the given result type
     */
    @Override
    public Mono<Void> handleResult(ServerWebExchange exchange, HandlerResult result) {
        Object returnValue = result.getReturnValue();

        if (returnValue instanceof OldHalResponse<?> halResponse) {
            return handleHalResponse(exchange, result, halResponse);
        }

        if (returnValue instanceof Publisher<?> publisher) {
            return handlePublisher(exchange, result, publisher);
        }

        return Mono.error(new IllegalStateException(
                "HalResponseHandlerResultHandler cannot handle result: " + result.getReturnValue()));
    }

    private Mono<Void> handleHalResponse(ServerWebExchange exchange, HandlerResult result,
                                         OldHalResponse<?> halResponse) {
        // Unwrap to ResponseEntity
        ResponseEntity<?> responseEntity = halResponse.toResponseEntity();

        HandlerResult delegateResult = new HandlerResult(
                result.getHandler(), //Controller method that returns the HalResponse which triggered all this
                responseEntity,
                //we have to fake the return value type because the real one is a HalResponse
                this.responseEntityReturnType
        );

        return responseEntityHandler.handleResult(exchange, delegateResult);
    }

    private Mono<Void> handlePublisher(ServerWebExchange exchange, HandlerResult result, Publisher<?> publisher) {
        ResolvableType publisherReturnType = result.getReturnType();
        ResolvableType genericTypeInPublisher = publisherReturnType.getGeneric(0);

        if (OldHalResponse.class.isAssignableFrom(genericTypeInPublisher.toClass())) {
            Publisher<ResponseEntity<?>> responseEntityPublisher = Flux.from(publisher)
                    .cast(OldHalResponse.class)
                    .map(OldHalResponse::toResponseEntity);

            Object concreteResponseEntityPublisher;
            Class<?> publisherClass = publisherReturnType.toClass();
            // Create concrete publisher
            if (Mono.class.isAssignableFrom(publisherClass)) {
                concreteResponseEntityPublisher = Mono.from(responseEntityPublisher);
            } else {
                concreteResponseEntityPublisher = Flux.from(responseEntityPublisher);
            }

            // Act as if the "Publisher<HalResponse> method(...)" was "actually Publisher<ResponseEntity> method(...)"
            //                          ^^^^^^^^^^^                                       ^^^^^^^^^^^^^^
            HandlerResult delegateResult = new HandlerResult(
                    // Controller method that returns the Publisher<HalResponse> which triggered all this
                    result.getHandler(),
                    // Mono<ResponseEntity> or Flux<ResponseEntity>
                    concreteResponseEntityPublisher,
                    // We have to fake the return value type because the real one is a HalResponse
                    this.responseEntityReturnType
            );

            return responseEntityHandler.handleResult(exchange, delegateResult);
        }

        return Mono.error(new IllegalStateException(
                "HalResponseHandlerResultHandler cannot handle publisher of type: " + publisher));
    }

    /**
     * Returns the order value of this handler.
     *
     * @return -99, indicating this handler should run before standard Spring handlers
     */
    @Override
    public int getOrder() {
        return ORDER;
    }

    /**
     * Internal helper class used to obtain a {@link MethodParameter} instance for {@link ResponseEntity} return type.
     * This is used for type resolution when delegating to the {@link ResponseEntityResultHandler}.
     */
    private static class ResponseEntityTypeProvider {
        /**
         * Dummy method used to obtain {@link ResponseEntity} return type information.
         *
         * @return null (this method is never actually called)
         */
        public ResponseEntity<?> returnResponseEntity() {
            return null;
        }
    }
}
