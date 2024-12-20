package de.kamillionlabs.hateoflux.http;

import de.kamillionlabs.hateoflux.model.hal.HalListWrapper;
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
 * A reactive response representation for HAL resources, wrapping a {@link HalListWrapper}
 * along with an HTTP status and optional HTTP headers.
 *
 * <p>This class extends {@link HttpHeadersModule} to provide fluent methods for adding HTTP headers,
 * and implements {@link ReactiveResponseEntity} to allow for automatic serialization by the
 * {@link ReactiveResponseEntityHandlerResultHandler}.</p>
 *
 * <p>The core functionality of this class is to encapsulate a reactive HAL resource, represented as a
 * {@link Mono} of {@link HalListWrapper}, and to transform it into a standard {@link ResponseEntity}
 * via the {@link #toResponseEntity()} method. This enables seamless integration of HAL-based hypermedia
 * resources into the reactive response handling pipeline.</p>
 *
 * <p>The HTTP status is also reactive, defined as a {@link Mono<HttpStatus>} that defaults to
 * {@link #DEFAULT_STATUS}. Headers can be optionally provided or constructed using this builder's methods.</p>
 *
 * <p>Convenience factory methods such as {@link #ok(Mono)}, {@link #created(Mono)}, and others
 * produce commonly used response configurations with specific HTTP statuses.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * Mono<HalListResponse<MyResource, MyEmbedded>> body = ...;
 * HalListResponse<MyResource, MyEmbedded> response = HalListResponse.ok(body)
 *     .withContentType("application/hal+json")
 *     .withETag("\"123456\"");
 * }</pre>
 *
 * <p>
 * <strong>Usage Guidelines:</strong>
 * </p>
 * <ul>
 *   <li><strong>{@link HalResourceResponse}</strong>: Use when your API endpoint returns a <b>single</b>
 *   {@link HalResourceWrapper}/li>
 *   <li><strong>{@link HalMultiResourceResponse}</strong>: Use when your endpoint returns <b>multiple</b>
 *   {@link HalResourceWrapper}</li>
 *   <li><strong>{@link HalListResponse}</strong>: Use when your endpoint returns a single {@link HalListWrapper}</li>
 * </ul>
 *
 * @param <ResourceT>
 *         the type of the resource represented by the HAL wrapper
 * @param <EmbeddedT>
 *         the type of the embedded resources represented by the HAL wrapper
 * @author Younes El Ouarti
 * @see ReactiveResponseEntity
 * @see ResponseEntity
 * @see HttpHeadersModule
 */
public class HalListResponse<ResourceT, EmbeddedT>
        extends HttpHeadersModule<HalListResponse<ResourceT, EmbeddedT>>
        implements ReactiveResponseEntity {

    private final Mono<HalListWrapper<ResourceT, EmbeddedT>> body;
    private final Mono<HttpStatus> status;

    /**
     * Constructs a new {@link HalListResponse} with the given body, HTTP status, and headers.
     *
     * @param body
     *         a {@link Mono} of {@link HalListResponse} representing the HAL resource body
     * @param httpStatus
     *         a {@link Mono} of {@link HttpStatus} to be associated with the response; defaults to
     *         {@link #DEFAULT_STATUS} if empty
     * @param headers
     *         an optional set of HTTP headers
     */
    public HalListResponse(Mono<HalListWrapper<ResourceT, EmbeddedT>> body,
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

    /**
     * Creates a {@link HalListResponse} with the given HAL resource body and HTTP status provided as a
     * {@link Mono}.
     *
     * @param body
     *         a {@link Mono} of {@link HalListWrapper} representing the HAL resource body
     * @param httpStatus
     *         a {@link Mono} of {@link HttpStatus}
     * @param <ResourceT>
     *         the resource type
     * @param <EmbeddedT>
     *         the embedded resource type
     * @return the created {@link HalListResponse}
     */
    public static <ResourceT, EmbeddedT> HalListResponse<ResourceT, EmbeddedT> of(
            @NonNull Mono<HalListWrapper<ResourceT, EmbeddedT>> body,
            @NonNull Mono<HttpStatus> httpStatus) {
        return new HalListResponse<>(body, httpStatus, null);
    }

    /**
     * Creates a {@link HalListResponse} with an empty body and the given HTTP status provided as a {@link Mono}.
     *
     * @param httpStatus
     *         a {@link Mono} of {@link HttpStatus}
     * @param <ResourceT>
     *         the resource type
     * @param <EmbeddedT>
     *         the embedded resource type
     * @return the created {@link HalListResponse}
     */
    public static <ResourceT, EmbeddedT> HalListResponse<ResourceT, EmbeddedT> of(
            @NonNull Mono<HttpStatus> httpStatus) {
        return new HalListResponse<>(Mono.empty(), httpStatus, null);
    }

    /**
     * Creates a {@link HalListResponse} with an empty body and the given {@link HttpStatus}.
     *
     * @param httpStatus
     *         the {@link HttpStatus} to use
     * @param <ResourceT>
     *         the resource type
     * @param <EmbeddedT>
     *         the embedded resource type
     * @return the created {@link HalListResponse}
     */
    public static <ResourceT, EmbeddedT> HalListResponse<ResourceT, EmbeddedT> of(
            @NonNull HttpStatus httpStatus) {
        return new HalListResponse<>(Mono.empty(), Mono.just(httpStatus), null);
    }

    /**
     * Creates a {@link HalListResponse} with a body and {@link HttpStatus#OK}.
     *
     * @param body
     *         the {@link Mono} of {@link HalListWrapper} representing the body; must not be null
     * @param <ResourceT>
     *         the resource type
     * @param <EmbeddedT>
     *         the embedded resource type
     * @return a {@link HalListResponse} with {@code OK} status and the given body
     *
     * @throws IllegalArgumentException
     *         if {@code body} is null
     */
    public static <ResourceT, EmbeddedT> HalListResponse<ResourceT, EmbeddedT> ok(
            @NonNull Mono<HalListWrapper<ResourceT, EmbeddedT>> body) {
        Assert.notNull(body, valueNotAllowedToBeNull("Body"));
        return new HalListResponse<>(body, Mono.just(HttpStatus.OK), null);
    }

    /**
     * Creates a {@link HalListResponse} with {@link HttpStatus#OK}.
     *
     * @param <ResourceT>
     *         the resource type
     * @param <EmbeddedT>
     *         the embedded resource type
     * @return a {@link HalListResponse} with {@code OK} status
     */
    public static <ResourceT, EmbeddedT> HalListResponse<ResourceT, EmbeddedT> ok() {
        return new HalListResponse<>(Mono.empty(), Mono.just(HttpStatus.OK), null);
    }

    /**
     * Creates a {@link HalListResponse} with a body and {@link HttpStatus#CREATED}.
     *
     * @param body
     *         the {@link Mono} of {@link HalListWrapper} representing the body; must not be null
     * @param <ResourceT>
     *         the resource type
     * @param <EmbeddedT>
     *         the embedded resource type
     * @return a {@link HalListResponse} with {@code CREATED} status and the given body
     *
     * @throws IllegalArgumentException
     *         if {@code body} is null
     */
    public static <ResourceT, EmbeddedT> HalListResponse<ResourceT, EmbeddedT> created(
            @NonNull Mono<HalListWrapper<ResourceT, EmbeddedT>> body) {
        Assert.notNull(body, valueNotAllowedToBeNull("Body"));
        return new HalListResponse<>(body, Mono.just(HttpStatus.CREATED), null);
    }

    /**
     * Creates a {@link HalListResponse} with {@link HttpStatus#CREATED}.
     *
     * @param <ResourceT>
     *         the resource type
     * @param <EmbeddedT>
     *         the embedded resource type
     * @return a {@link HalListResponse} with {@code CREATED} status
     */
    public static <ResourceT, EmbeddedT> HalListResponse<ResourceT, EmbeddedT> created() {
        return new HalListResponse<>(Mono.empty(), Mono.just(HttpStatus.CREATED), null);
    }

    /**
     * Creates a {@link HalListResponse} with a body and {@link HttpStatus#ACCEPTED}.
     *
     * @param body
     *         the {@link Mono} of {@link HalListWrapper} representing the body; must not be null
     * @param <ResourceT>
     *         the resource type
     * @param <EmbeddedT>
     *         the embedded resource type
     * @return a {@link HalListResponse} with {@code ACCEPTED} status and the given body
     *
     * @throws IllegalArgumentException
     *         if {@code body} is null
     */
    public static <ResourceT, EmbeddedT> HalListResponse<ResourceT, EmbeddedT> accepted(
            @NonNull Mono<HalListWrapper<ResourceT, EmbeddedT>> body) {
        Assert.notNull(body, valueNotAllowedToBeNull("Body"));
        return new HalListResponse<>(body, Mono.just(HttpStatus.ACCEPTED), null);
    }

    /**
     * Creates a {@link HalListResponse} with {@link HttpStatus#ACCEPTED}.
     *
     * @param <ResourceT>
     *         the resource type
     * @param <EmbeddedT>
     *         the embedded resource type
     * @return a {@link HalListResponse} with {@code ACCEPTED} status
     */
    public static <ResourceT, EmbeddedT> HalListResponse<ResourceT, EmbeddedT> accepted() {
        return new HalListResponse<>(Mono.empty(), Mono.just(HttpStatus.ACCEPTED), null);
    }

    /**
     * Creates a {@link HalListResponse} with no body and {@link HttpStatus#NO_CONTENT}.
     *
     * @param <ResourceT>
     *         the resource type
     * @param <EmbeddedT>
     *         the embedded resource type
     * @return a {@link HalListResponse} with {@code NO_CONTENT} status
     */
    public static <ResourceT, EmbeddedT> HalListResponse<ResourceT, EmbeddedT> noContent() {
        return new HalListResponse<>(Mono.empty(), Mono.just(HttpStatus.NO_CONTENT), null);
    }

    /**
     * Creates a {@link HalListResponse} with no body and {@link HttpStatus#NOT_FOUND}.
     *
     * @param <ResourceT>
     *         the resource type
     * @param <EmbeddedT>
     *         the embedded resource type
     * @return a {@link HalListResponse} with {@code NOT_FOUND} status
     */
    public static <ResourceT, EmbeddedT> HalListResponse<ResourceT, EmbeddedT> notFound() {
        return new HalListResponse<>(Mono.empty(), Mono.just(HttpStatus.NOT_FOUND), null);
    }
}
