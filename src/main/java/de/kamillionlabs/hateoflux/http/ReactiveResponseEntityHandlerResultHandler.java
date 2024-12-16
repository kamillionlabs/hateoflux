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

public class ReactiveResponseEntityHandlerResultHandler implements HandlerResultHandler, Ordered {

    int ORDER = -99;

    private final ResponseEntityResultHandler responseEntityHandler;

    private final MethodParameter responseEntityReturnType;

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
                result.getHandler(), //Controller method that returns the ReactiveResponseEntity which triggered all
                // this
                responseEntity,
                //we have to fake the return value type because the real one is a HalResponse
                this.responseEntityReturnType
        );

        return responseEntityHandler.handleResult(exchange, delegateResult);
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
