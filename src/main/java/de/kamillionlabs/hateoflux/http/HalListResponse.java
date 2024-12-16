package de.kamillionlabs.hateoflux.http;

import de.kamillionlabs.hateoflux.model.hal.HalListWrapper;
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
public class HalListResponse<ResourceT, EmbeddedT>
        extends HttpHeadersModule<HalListResponse<ResourceT, EmbeddedT>>
        implements ReactiveResponseEntity {

    private final Mono<HalListWrapper<ResourceT, EmbeddedT>> body;
    private final Mono<HttpStatus> status;

    private HalListResponse(Mono<HalListWrapper<ResourceT, EmbeddedT>> body,
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
        Mono<ResponseEntity<HalListWrapper<ResourceT, EmbeddedT>>> reactiveResponseEntity =
                body.zipWith(status, (b, s) -> new ResponseEntity<>(b, headers, s))
                        .switchIfEmpty(status.map(s -> new ResponseEntity<>(headers, s)));
        return reactiveResponseEntity
                .map(response -> response); //implicit casting to "?"
    }

    // Static Factory Method

    public static <ResourceT, EmbeddedT> HalListResponse<ResourceT, EmbeddedT> of(
            @NonNull Mono<HalListWrapper<ResourceT, EmbeddedT>> body,
            @NonNull Mono<HttpStatus> httpStatus) {
        return new HalListResponse<>(body, httpStatus, null);
    }

    public static <ResourceT, EmbeddedT> HalListResponse<ResourceT, EmbeddedT> of(
            @NonNull Mono<HttpStatus> httpStatus) {
        return new HalListResponse<>(Mono.empty(), httpStatus, null);
    }

    public static <ResourceT, EmbeddedT> HalListResponse<ResourceT, EmbeddedT> of(
            @NonNull HttpStatus httpStatus) {
        return new HalListResponse<>(Mono.empty(), Mono.just(httpStatus), null);
    }

    /**
     * Creates a {@link HalListResponse} with {@link HttpStatus#OK} and the given body.
     *
     * @param <ResourceT>
     *         resource type
     * @param <EmbeddedT>
     *         embedded type
     * @param body
     *         the HAL wrapper
     * @return the created response
     */
    public static <ResourceT, EmbeddedT> HalListResponse<ResourceT, EmbeddedT> ok(
            @NonNull Mono<HalListWrapper<ResourceT, EmbeddedT>> body) {
        Assert.notNull(body, valueNotAllowedToBeNull("Body"));
        return new HalListResponse<>(body, Mono.just(HttpStatus.OK), null);
    }

    /**
     * Creates a {@link HalListResponse} with {@link HttpStatus#CREATED} and the given body.
     *
     * @param <ResourceT>
     *         resource type
     * @param <EmbeddedT>
     *         embedded type
     * @param body
     *         the HAL wrapper
     * @return the created response
     */
    public static <ResourceT, EmbeddedT> HalListResponse<ResourceT, EmbeddedT> created(
            @NonNull Mono<HalListWrapper<ResourceT, EmbeddedT>> body) {
        Assert.notNull(body, valueNotAllowedToBeNull("Body"));
        return new HalListResponse<>(body, Mono.just(HttpStatus.CREATED), null);
    }

    /**
     * Creates a {@link HalListResponse} with {@link HttpStatus#ACCEPTED} and the given body.
     *
     * @param <ResourceT>
     *         resource type
     * @param <EmbeddedT>
     *         embedded type
     * @param body
     *         the HAL wrapper
     * @return the created response
     */
    public static <ResourceT, EmbeddedT> HalListResponse<ResourceT, EmbeddedT> accepted(
            @NonNull Mono<HalListWrapper<ResourceT, EmbeddedT>> body) {
        Assert.notNull(body, valueNotAllowedToBeNull("Body"));
        return new HalListResponse<>(body, Mono.just(HttpStatus.ACCEPTED), null);
    }

    /**
     * Creates a {@link HalListResponse} with {@link HttpStatus#NO_CONTENT}.
     *
     * @param <ResourceT>
     *         resource type
     * @param <EmbeddedT>
     *         embedded type
     * @return the created response
     */
    public static <ResourceT, EmbeddedT> HalListResponse<ResourceT, EmbeddedT> noContent() {
        return new HalListResponse<>(Mono.empty(), Mono.just(HttpStatus.NO_CONTENT), null);
    }

    /**
     * Creates a {@link HalListResponse} with {@link HttpStatus#BAD_REQUEST}.
     *
     * @param <ResourceT>
     *         resource type
     * @param <EmbeddedT>
     *         embedded type
     * @return the created response
     */
    public static <ResourceT, EmbeddedT> HalListResponse<ResourceT, EmbeddedT> badRequest() {
        return new HalListResponse<>(Mono.empty(), Mono.just(HttpStatus.BAD_REQUEST), null);
    }

    /**
     * Creates a {@link HalListResponse} with {@link HttpStatus#NOT_FOUND}.
     *
     * @param <ResourceT>
     *         resource type
     * @param <EmbeddedT>
     *         embedded type
     * @return the created response
     */
    public static <ResourceT, EmbeddedT> HalListResponse<ResourceT, EmbeddedT> notFound() {
        return new HalListResponse<>(Mono.empty(), Mono.just(HttpStatus.NOT_FOUND), null);
    }

    /**
     * Creates a {@link HalListResponse} with {@link HttpStatus#FORBIDDEN}.
     *
     * @param <ResourceT>
     *         resource type
     * @param <EmbeddedT>
     *         embedded type
     * @return the created response
     */
    public static <ResourceT, EmbeddedT> HalListResponse<ResourceT, EmbeddedT> forbidden() {
        return new HalListResponse<>(Mono.empty(), Mono.just(HttpStatus.FORBIDDEN), null);
    }

    /**
     * Creates a {@link HalListResponse} with {@link HttpStatus#UNAUTHORIZED}.
     *
     * @param <ResourceT>
     *         resource type
     * @param <EmbeddedT>
     *         embedded type
     * @return the created response
     */
    public static <ResourceT, EmbeddedT> HalListResponse<ResourceT, EmbeddedT> unauthorized() {
        return new HalListResponse<>(Mono.empty(), Mono.just(HttpStatus.UNAUTHORIZED), null);
    }
}
