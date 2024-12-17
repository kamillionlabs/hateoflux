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
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;

/**
 * A custom {@link HandlerResultHandler} that enables the serialization of {@link ReactiveResponseEntity}
 * instances by delegating their handling to a standard {@link ResponseEntityResultHandler}.
 *
 * <p>This handler checks whether the return value of a controller method is an instance of
 * {@link ReactiveResponseEntity} (or a publisher wrapping one). If so, it converts the
 * {@link ReactiveResponseEntity} into a {@link ResponseEntity} and delegates to the provided
 * {@link ResponseEntityResultHandler} to handle the actual response rendering.
 *
 * <p>This ensures that custom reactive response entity types can be seamlessly integrated into the existing Spring
 * WebFlux response pipeline without requiring manual conversion by the controller developer.
 *
 * <p>If a publisher of {@link ReactiveResponseEntity} is encountered, an error is returned, as nesting a reactive type
 * within another publisher is not supported by this handler.</p>
 *
 * @author Younes El Ouarti
 * @see ReactiveResponseEntity
 * @see ResponseEntityResultHandler
 */
public class ReactiveResponseEntityHandlerResultHandler implements HandlerResultHandler, Ordered {

    int ORDER = -99;

    private final ResponseEntityResultHandler responseEntityHandler;

    private final MethodParameter responseEntityReturnType;

    /**
     * Constructs a new {@link ReactiveResponseEntityHandlerResultHandler} that delegates to the provided
     * {@link ResponseEntityResultHandler} after converting {@link ReactiveResponseEntity} objects to standard
     * {@link ResponseEntity} instances.
     *
     * @param responseEntityHandler
     *         the {@link ResponseEntityResultHandler} used to handle standard {@link ResponseEntity} instances
     * @throws IllegalStateException
     *         if reflection fails to find the dummy method used to create a {@link MethodParameter} for
     *         {@link ResponseEntity}
     */
    public ReactiveResponseEntityHandlerResultHandler(ResponseEntityResultHandler responseEntityHandler) {
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
     * Determines whether this handler should process the given {@link HandlerResult}.
     *
     * <p>This handler supports {@link ReactiveResponseEntity} only. {@code Publisher<ReactiveResponseEntity>} are also
     * caught, but will result in a {@link IllegalStateException} later during {@link #handleResult}:
     *
     * @param result
     *         the {@link HandlerResult} to inspect
     * @return {@code true} if the result is a {@link ReactiveResponseEntity} or a publisher of one;
     * {@code false} otherwise
     */
    @Override
    public boolean supports(HandlerResult result) {
        ResolvableType returnType = result.getReturnType();
        Class<?> rawClassOfReturnType = returnType.toClass();

        // Direct ReactiveResponseEntity?
        if (ReactiveResponseEntity.class.isAssignableFrom(rawClassOfReturnType)) {
            return true;
        }

        // Check if it's a Publisher<ReactiveResponseEntity>
        if (Publisher.class.isAssignableFrom(rawClassOfReturnType)) {
            ResolvableType genericType = returnType.getGeneric(0);
            Class<?> genericClass = genericType.toClass();
            if (ReactiveResponseEntity.class.isAssignableFrom(genericClass)) {
                //we will handle it even though this is not allowed. We want to throw a Mono.error()
                // instead of an exception here
                return true;
            }
        }

        return false;
    }

    /**
     * Handles the given {@link HandlerResult} by converting any {@link ReactiveResponseEntity} to a standard
     * {@link ResponseEntity} and delegating to the configured {@link ResponseEntityResultHandler}.
     *
     * <p>If the return value is a direct {@link ReactiveResponseEntity}, it is converted and handled.
     * If it's a {@code Publisher<ReactiveResponseEntity>}, an error is returned.
     *
     * @param exchange
     *         the current server exchange
     * @param result
     *         the handler result containing the controller method's return value
     * @return a {@link Mono} that completes when the response handling is finished or with an error
     * if the return value is not supported
     */
    @Override
    public Mono<Void> handleResult(ServerWebExchange exchange, HandlerResult result) {
        Object returnValue = result.getReturnValue();

        if (returnValue instanceof ReactiveResponseEntity reactiveResponseEntity) {
            return handleReactiveResponseEntity(exchange, result, reactiveResponseEntity);
        }

        if (returnValue instanceof Publisher<?>) {
            ResolvableType returnType = result.getReturnType();
            Class<?> rawClassOfReturnType = returnType.toClass();
            ResolvableType genericType = returnType.getGeneric(0);
            Class<?> genericClass = genericType.toClass();
            return Mono.error(new IllegalStateException("ReactiveResponseEntity are not allowed to be wrapped in a " +
                    "publisher," +
                    " as they are already reactive (was " + rawClassOfReturnType + "<" + genericClass + ">)"));
        }

        return Mono.error(new IllegalStateException(
                "HalResponseHandlerResultHandler cannot handle result: " + result.getReturnValue()));
    }

    private Mono<Void> handleReactiveResponseEntity(ServerWebExchange exchange, HandlerResult result,
                                                    ReactiveResponseEntity reactiveResponseEntity) {
        // Unwrap to ResponseEntity
        Mono<ResponseEntity<?>> responseEntity = reactiveResponseEntity.toResponseEntity();

        HandlerResult delegateResult = new HandlerResult(
                //Controller method that returns the ReactiveResponseEntity which triggered all this
                result.getHandler(),
                //Unwrapped ResponsEntity from the custom Responses
                responseEntity,
                //We have to fake the return value type because the real one is a descendant of ReactiveResponseEntity
                this.responseEntityReturnType
        );

        return responseEntityHandler.handleResult(exchange, delegateResult);
    }


    /**
     * Returns the order value of this handler. A lower value means higher priority.
     *
     * @return -99, indicating this handler should run before standard Spring handlers
     */
    @Override
    public int getOrder() {
        return ORDER;
    }

    /**
     * Internal helper class used solely to obtain a {@link MethodParameter} instance
     * for {@link ResponseEntity} return type information.
     */
    private static class ResponseEntityTypeProvider {
        /**
         * A dummy method used to retrieve a {@link Method} representing a {@link ResponseEntity} return type.
         * This method is never actually called, it's just used for reflection to obtain type information.
         *
         * @return null (this method is never executed)
         */
        public ResponseEntity<?> returnResponseEntity() {
            return null;
        }
    }
}
