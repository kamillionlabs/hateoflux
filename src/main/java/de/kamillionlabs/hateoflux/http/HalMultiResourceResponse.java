package de.kamillionlabs.hateoflux.http;

import de.kamillionlabs.hateoflux.model.hal.HalResourceWrapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static de.kamillionlabs.hateoflux.utility.ValidationMessageTemplates.valueNotAllowedToBeNull;

/**
 * @author Younes El Ouarti
 */
public class HalMultiResourceResponse<ResourceT, EmbeddedT>
        extends HttpHeadersModule<HalMultiResourceResponse<ResourceT, EmbeddedT>>
        implements ReactiveResponseEntity {

    private final Flux<HalResourceWrapper<ResourceT, EmbeddedT>> body;
    private final HttpStatus status;

    public HalMultiResourceResponse(Flux<HalResourceWrapper<ResourceT, EmbeddedT>> body,
                                    HttpStatus httpStatus,
                                    MultiValueMap<String, String> headers) {
        this.status = httpStatus == null ? DEFAULT_STATUS : httpStatus;
        this.body = body;
        this.headers = Optional.ofNullable(headers)
                .map(HttpHeaders::new)
                .orElse(new HttpHeaders());

    }

    @Override
    public Mono<ResponseEntity<?>> toResponseEntity() {
        Mono<ResponseEntity<Flux<HalResourceWrapper<ResourceT, EmbeddedT>>>> reactiveResponseEntity =
                Mono.just(new ResponseEntity<>(body, headers, status));
        return reactiveResponseEntity
                .map(response -> response); //implicit casting to "?";
    }

    // Static Factory Method
    public static <ResourceT, EmbeddedT> HalMultiResourceResponse<ResourceT, EmbeddedT> of(
            @NonNull Flux<HalResourceWrapper<ResourceT, EmbeddedT>> body,
            @NonNull HttpStatus httpStatus) {
        return new HalMultiResourceResponse<>(body, httpStatus, null);
    }

    public static <ResourceT, EmbeddedT> HalMultiResourceResponse<ResourceT, EmbeddedT> of(
            @NonNull HttpStatus httpStatus) {
        return new HalMultiResourceResponse<>(Flux.empty(), httpStatus, null);
    }

    /**
     * Creates a {@link HalMultiResourceResponse} with {@link HttpStatus#OK} and the given body.
     *
     * @param <ResourceT>
     *         resource type
     * @param <EmbeddedT>
     *         embedded type
     * @param body
     *         the HAL wrapper
     * @return the created response
     */
    public static <ResourceT, EmbeddedT> HalMultiResourceResponse<ResourceT, EmbeddedT> ok(
            @NonNull Flux<HalResourceWrapper<ResourceT, EmbeddedT>> body) {
        Assert.notNull(body, valueNotAllowedToBeNull("Body"));
        return new HalMultiResourceResponse<>(body, HttpStatus.OK, null);
    }

    /**
     * Creates a {@link HalMultiResourceResponse} with {@link HttpStatus#CREATED} and the given body.
     *
     * @param <ResourceT>
     *         resource type
     * @param <EmbeddedT>
     *         embedded type
     * @param body
     *         the HAL wrapper
     * @return the created response
     */
    public static <ResourceT, EmbeddedT> HalMultiResourceResponse<ResourceT, EmbeddedT> created(
            @NonNull Flux<HalResourceWrapper<ResourceT, EmbeddedT>> body) {
        Assert.notNull(body, valueNotAllowedToBeNull("Body"));
        return new HalMultiResourceResponse<>(body, HttpStatus.CREATED, null);
    }

    /**
     * Creates a {@link HalMultiResourceResponse} with {@link HttpStatus#ACCEPTED} and the given body.
     *
     * @param <ResourceT>
     *         resource type
     * @param <EmbeddedT>
     *         embedded type
     * @param body
     *         the HAL wrapper
     * @return the created response
     */
    public static <ResourceT, EmbeddedT> HalMultiResourceResponse<ResourceT, EmbeddedT> accepted(
            @NonNull Flux<HalResourceWrapper<ResourceT, EmbeddedT>> body) {
        Assert.notNull(body, valueNotAllowedToBeNull("Body"));
        return new HalMultiResourceResponse<>(body, HttpStatus.ACCEPTED, null);
    }

    /**
     * Creates a {@link HalMultiResourceResponse} with {@link HttpStatus#NO_CONTENT}.
     *
     * @param <ResourceT>
     *         resource type
     * @param <EmbeddedT>
     *         embedded type
     * @return the created response
     */
    public static <ResourceT, EmbeddedT> HalMultiResourceResponse<ResourceT, EmbeddedT> noContent() {
        return new HalMultiResourceResponse<>(Flux.empty(), HttpStatus.NO_CONTENT, null);
    }

    /**
     * Creates a {@link HalMultiResourceResponse} with {@link HttpStatus#BAD_REQUEST}.
     *
     * @param <ResourceT>
     *         resource type
     * @param <EmbeddedT>
     *         embedded type
     * @return the created response
     */
    public static <ResourceT, EmbeddedT> HalMultiResourceResponse<ResourceT, EmbeddedT> badRequest() {
        return new HalMultiResourceResponse<>(Flux.empty(), HttpStatus.BAD_REQUEST, null);
    }

    /**
     * Creates a {@link HalMultiResourceResponse} with {@link HttpStatus#NOT_FOUND}.
     *
     * @param <ResourceT>
     *         resource type
     * @param <EmbeddedT>
     *         embedded type
     * @return the created response
     */
    public static <ResourceT, EmbeddedT> HalMultiResourceResponse<ResourceT, EmbeddedT> notFound() {
        return new HalMultiResourceResponse<>(Flux.empty(), HttpStatus.NOT_FOUND, null);
    }

    /**
     * Creates a {@link HalMultiResourceResponse} with {@link HttpStatus#FORBIDDEN}.
     *
     * @param <ResourceT>
     *         resource type
     * @param <EmbeddedT>
     *         embedded type
     * @return the created response
     */
    public static <ResourceT, EmbeddedT> HalMultiResourceResponse<ResourceT, EmbeddedT> forbidden() {
        return new HalMultiResourceResponse<>(Flux.empty(), HttpStatus.FORBIDDEN, null);
    }

    /**
     * Creates a {@link HalMultiResourceResponse} with {@link HttpStatus#UNAUTHORIZED}.
     *
     * @param <ResourceT>
     *         resource type
     * @param <EmbeddedT>
     *         embedded type
     * @return the created response
     */
    public static <ResourceT, EmbeddedT> HalMultiResourceResponse<ResourceT, EmbeddedT> unauthorized() {
        return new HalMultiResourceResponse<>(Flux.empty(), HttpStatus.UNAUTHORIZED, null);
    }
}
