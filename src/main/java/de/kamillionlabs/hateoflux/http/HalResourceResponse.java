package de.kamillionlabs.hateoflux.http;

import de.kamillionlabs.hateoflux.model.hal.HalResourceWrapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static de.kamillionlabs.hateoflux.utility.ValidationMessageTemplates.valueNotAllowedToBeNull;

/**
 * @author Younes El Ouarti
 */
public class HalResourceResponse<ResourceT, EmbeddedT>
        extends HttpHeadersModule<HalResourceResponse<ResourceT, EmbeddedT>>
        implements ReactiveResponseEntity {

    private final Mono<HalResourceWrapper<ResourceT, EmbeddedT>> body;
    private final Mono<HttpStatus> status;

    public HalResourceResponse(Mono<HalResourceWrapper<ResourceT, EmbeddedT>> body,
                               Mono<HttpStatus> httpStatus,
                               MultiValueMap<String, String> headers) {
        this.status = httpStatus.defaultIfEmpty(DEFAULT_STATUS);
        this.body = body;
        this.headers = Optional.ofNullable(headers)
                .map(HttpHeaders::new)
                .orElse(new HttpHeaders());
    }

    @Override
    public Mono<ResponseEntity<?>> toResponseEntity() {
        Mono<ResponseEntity<HalResourceWrapper<ResourceT, EmbeddedT>>> reactiveResponseEntity =
                body.zipWith(status, (b, s) -> new ResponseEntity<>(b, headers, s))
                        .switchIfEmpty(status.map(s -> new ResponseEntity<>(headers, s)));
        return reactiveResponseEntity
                .map(response -> response); //implicit casting to "?"
    }

    // Static Factory Method

    public static <ResourceT, EmbeddedT> HalResourceResponse<ResourceT, EmbeddedT> of(
            @NonNull Mono<HalResourceWrapper<ResourceT, EmbeddedT>> body,
            @NonNull Mono<HttpStatus> httpStatus) {
        return new HalResourceResponse<>(body, httpStatus, null);
    }

    public static <ResourceT, EmbeddedT> HalResourceResponse<ResourceT, EmbeddedT> of(
            @NonNull Mono<HttpStatus> httpStatus) {
        return new HalResourceResponse<>(Mono.empty(), httpStatus, null);
    }

    public static <ResourceT, EmbeddedT> HalResourceResponse<ResourceT, EmbeddedT> of(
            @NonNull HttpStatus httpStatus) {
        return new HalResourceResponse<>(Mono.empty(), Mono.just(httpStatus), null);
    }

    /**
     * Creates a {@link HalResourceResponse} with {@link HttpStatus#OK} and the given body.
     *
     * @param <ResourceT>
     *         resource type
     * @param <EmbeddedT>
     *         embedded type
     * @param body
     *         the HAL wrapper
     * @return the created response
     */
    public static <ResourceT, EmbeddedT> HalResourceResponse<ResourceT, EmbeddedT> ok(
            @NonNull Mono<HalResourceWrapper<ResourceT, EmbeddedT>> body) {
        Assert.notNull(body, valueNotAllowedToBeNull("Body"));
        return new HalResourceResponse<>(body, Mono.just(HttpStatus.OK), null);
    }

    /**
     * Creates a {@link HalResourceResponse} with {@link HttpStatus#CREATED} and the given body.
     *
     * @param <ResourceT>
     *         resource type
     * @param <EmbeddedT>
     *         embedded type
     * @param body
     *         the HAL wrapper
     * @return the created response
     */
    public static <ResourceT, EmbeddedT> HalResourceResponse<ResourceT, EmbeddedT> created(
            @NonNull Mono<HalResourceWrapper<ResourceT, EmbeddedT>> body) {
        Assert.notNull(body, valueNotAllowedToBeNull("Body"));
        return new HalResourceResponse<>(body, Mono.just(HttpStatus.CREATED), null);
    }

    /**
     * Creates a {@link HalResourceResponse} with {@link HttpStatus#ACCEPTED} and the given body.
     *
     * @param <ResourceT>
     *         resource type
     * @param <EmbeddedT>
     *         embedded type
     * @param body
     *         the HAL wrapper
     * @return the created response
     */
    public static <ResourceT, EmbeddedT> HalResourceResponse<ResourceT, EmbeddedT> accepted(
            @NonNull Mono<HalResourceWrapper<ResourceT, EmbeddedT>> body) {
        Assert.notNull(body, valueNotAllowedToBeNull("Body"));
        return new HalResourceResponse<>(body, Mono.just(HttpStatus.ACCEPTED), null);
    }

    /**
     * Creates a {@link HalResourceResponse} with {@link HttpStatus#NO_CONTENT}.
     *
     * @param <ResourceT>
     *         resource type
     * @param <EmbeddedT>
     *         embedded type
     * @return the created response
     */
    public static <ResourceT, EmbeddedT> HalResourceResponse<ResourceT, EmbeddedT> noContent() {
        return new HalResourceResponse<>(Mono.empty(), Mono.just(HttpStatus.NO_CONTENT), null);
    }

    /**
     * Creates a {@link HalResourceResponse} with {@link HttpStatus#BAD_REQUEST}.
     *
     * @param <ResourceT>
     *         resource type
     * @param <EmbeddedT>
     *         embedded type
     * @return the created response
     */
    public static <ResourceT, EmbeddedT> HalResourceResponse<ResourceT, EmbeddedT> badRequest() {
        return new HalResourceResponse<>(Mono.empty(), Mono.just(HttpStatus.BAD_REQUEST), null);
    }

    /**
     * Creates a {@link HalResourceResponse} with {@link HttpStatus#NOT_FOUND}.
     *
     * @param <ResourceT>
     *         resource type
     * @param <EmbeddedT>
     *         embedded type
     * @return the created response
     */
    public static <ResourceT, EmbeddedT> HalResourceResponse<ResourceT, EmbeddedT> notFound() {
        return new HalResourceResponse<>(Mono.empty(), Mono.just(HttpStatus.NOT_FOUND), null);
    }

    /**
     * Creates a {@link HalResourceResponse} with {@link HttpStatus#FORBIDDEN}.
     *
     * @param <ResourceT>
     *         resource type
     * @param <EmbeddedT>
     *         embedded type
     * @return the created response
     */
    public static <ResourceT, EmbeddedT> HalResourceResponse<ResourceT, EmbeddedT> forbidden() {
        return new HalResourceResponse<>(Mono.empty(), Mono.just(HttpStatus.FORBIDDEN), null);
    }

    /**
     * Creates a {@link HalResourceResponse} with {@link HttpStatus#UNAUTHORIZED}.
     *
     * @param <ResourceT>
     *         resource type
     * @param <EmbeddedT>
     *         embedded type
     * @return the created response
     */
    public static <ResourceT, EmbeddedT> HalResourceResponse<ResourceT, EmbeddedT> unauthorized() {
        return new HalResourceResponse<>(Mono.empty(), Mono.just(HttpStatus.UNAUTHORIZED), null);
    }
}
