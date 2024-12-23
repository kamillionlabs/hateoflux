/*
 * Copyright (c)  2024 kamillion labs contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.kamillionlabs.hateoflux.http;

import de.kamillionlabs.hateoflux.model.hal.HalListWrapper;
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
 * A reactive response representation for multiple HAL resources, encapsulating a {@link Flux} of
 * {@link HalResourceWrapper} along with an HTTP status and optional HTTP headers.
 *
 * <p>This class extends {@link HttpHeadersModule} to provide fluent methods for adding HTTP headers
 * and implements {@link ReactiveResponseEntity} to allow for automatic serialization by the
 * {@code ReactiveResponseEntityHandlerResultHandler}.</p>
 *
 * <p>The core functionality of this class is to encapsulate multiple HAL resources, represented as a
 * {@link Flux} of {@link HalResourceWrapper}, and to transform them into a standard {@link ResponseEntity}
 * via the {@link #toResponseEntity()} method. This enables seamless integration of multiple HAL-based hypermedia
 * resources into the reactive response handling pipeline.</p>
 *
 * <p>The HTTP status is defined as an {@link HttpStatus} that defaults to {@link #DEFAULT_STATUS} if not provided.
 * Headers can be optionally provided or constructed using this builder's methods.</p>
 *
 * <p>Convenience factory methods such as {@link #ok(Flux)}, {@link #created(Flux)}, and others
 * produce commonly used response configurations with specific HTTP statuses.</p>
 *
 * <p>
 * <strong>Example usage:</strong>
 * <pre>{@code
 * Flux<HalResourceWrapper<MyResource, MyEmbedded>> body = ...;
 * HalMultiResourceResponse<MyResource, MyEmbedded> response = HalMultiResourceResponse.ok(body)
 *     .withContentType("application/hal+json")
 *     .withETag("\"123456\"");
 * }</pre>
 *
 *
 * <p>
 * <strong>Usage Guidelines:</strong>
 * </p>
 * <ul>
 *   <li><strong>{@link HalResourceResponse}</strong>: Use when your API endpoint returns a <b>single</b>
 *   {@link HalResourceWrapper}/li>
 *   <li><strong>{@link HalMultiResourceResponse}</strong>: Use when your endpoint returns <b>multiple</b>
 *   {@link HalResourceWrapper}</li>
 *   <li><strong>{@link HalMultiResourceResponse}</strong>: Use when your endpoint returns a single
 *   {@link HalListWrapper}</li>
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
public class HalMultiResourceResponse<ResourceT, EmbeddedT>
        extends HttpHeadersModule<HalMultiResourceResponse<ResourceT, EmbeddedT>>
        implements ReactiveResponseEntity {

    private final Flux<HalResourceWrapper<ResourceT, EmbeddedT>> body;
    private final HttpStatus status;

    /**
     * Constructs a new {@link HalMultiResourceResponse} with the given body, HTTP status, and headers.
     *
     * @param body
     *         a {@link Flux} of {@link HalResourceWrapper} representing the multiple HAL resources
     * @param httpStatus
     *         the {@link HttpStatus} to be associated with the response; defaults to {@link #DEFAULT_STATUS} if
     *         {@code null}
     * @param headers
     *         an optional set of HTTP headers; may be {@code null}
     */
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

    /**
     * Creates a {@link HalMultiResourceResponse} with the given HAL resource body and HTTP status.
     *
     * @param body
     *         a {@link Flux} of {@link HalResourceWrapper} representing the multiple HAL resources; must not be
     *         {@code null}
     * @param httpStatus
     *         the {@link HttpStatus} to associate with the response; must not be {@code null}
     * @param <ResourceT>
     *         the resource type
     * @param <EmbeddedT>
     *         the embedded resource type
     * @return the created {@link HalMultiResourceResponse}
     *
     * @throws IllegalArgumentException
     *         if {@code body} or {@code httpStatus} is {@code null}
     */
    public static <ResourceT, EmbeddedT> HalMultiResourceResponse<ResourceT, EmbeddedT> of(
            @NonNull Flux<HalResourceWrapper<ResourceT, EmbeddedT>> body,
            @NonNull HttpStatus httpStatus) {
        return new HalMultiResourceResponse<>(body, httpStatus, null);
    }

    /**
     * Creates a {@link HalMultiResourceResponse} with an empty body and the given HTTP status.
     *
     * @param httpStatus
     *         the {@link HttpStatus} to associate with the response; must not be {@code null}
     * @param <ResourceT>
     *         the resource type
     * @param <EmbeddedT>
     *         the embedded resource type
     * @return the created {@link HalMultiResourceResponse}
     *
     * @throws IllegalArgumentException
     *         if {@code httpStatus} is {@code null}
     */
    public static <ResourceT, EmbeddedT> HalMultiResourceResponse<ResourceT, EmbeddedT> of(
            @NonNull HttpStatus httpStatus) {
        return new HalMultiResourceResponse<>(Flux.empty(), httpStatus, null);
    }

    /**
     * Creates a {@link HalMultiResourceResponse} with the given HAL resource body and {@link HttpStatus#OK}.
     *
     * @param body
     *         the {@link Flux} of {@link HalResourceWrapper} representing the multiple HAL resources; must not be
     *         {@code null}
     * @param <ResourceT>
     *         the resource type
     * @param <EmbeddedT>
     *         the embedded resource type
     * @return a {@link HalMultiResourceResponse} with {@code OK} status and the given body
     *
     * @throws IllegalArgumentException
     *         if {@code body} is {@code null}
     */
    public static <ResourceT, EmbeddedT> HalMultiResourceResponse<ResourceT, EmbeddedT> ok(
            @NonNull Flux<HalResourceWrapper<ResourceT, EmbeddedT>> body) {
        Assert.notNull(body, valueNotAllowedToBeNull("Body"));
        return new HalMultiResourceResponse<>(body, HttpStatus.OK, null);
    }

    /**
     * Creates a {@link HalMultiResourceResponse} with {@link HttpStatus#OK}.
     *
     * @param <ResourceT>
     *         the resource type
     * @param <EmbeddedT>
     *         the embedded resource type
     * @return a {@link HalMultiResourceResponse} with {@code OK} status
     */
    public static <ResourceT, EmbeddedT> HalMultiResourceResponse<ResourceT, EmbeddedT> ok() {
        return new HalMultiResourceResponse<>(Flux.empty(), HttpStatus.OK, null);
    }

    /**
     * Creates a {@link HalMultiResourceResponse} with the given HAL resource body and {@link HttpStatus#CREATED}.
     *
     * @param body
     *         the {@link Flux} of {@link HalResourceWrapper} representing the multiple HAL resources; must not be
     *         {@code null}
     * @param <ResourceT>
     *         the resource type
     * @param <EmbeddedT>
     *         the embedded resource type
     * @return a {@link HalMultiResourceResponse} with {@code CREATED} status and the given body
     *
     * @throws IllegalArgumentException
     *         if {@code body} is {@code null}
     */
    public static <ResourceT, EmbeddedT> HalMultiResourceResponse<ResourceT, EmbeddedT> created(
            @NonNull Flux<HalResourceWrapper<ResourceT, EmbeddedT>> body) {
        Assert.notNull(body, valueNotAllowedToBeNull("Body"));
        return new HalMultiResourceResponse<>(body, HttpStatus.CREATED, null);
    }

    /**
     * Creates a {@link HalMultiResourceResponse} with {@link HttpStatus#CREATED}.
     *
     * @param <ResourceT>
     *         the resource type
     * @param <EmbeddedT>
     *         the embedded resource type
     * @return a {@link HalMultiResourceResponse} with {@code CREATED} status
     */
    public static <ResourceT, EmbeddedT> HalMultiResourceResponse<ResourceT, EmbeddedT> created() {
        return new HalMultiResourceResponse<>(Flux.empty(), HttpStatus.CREATED, null);
    }

    /**
     * Creates a {@link HalMultiResourceResponse} with the given HAL resource body and {@link HttpStatus#ACCEPTED}.
     *
     * @param body
     *         the {@link Flux} of {@link HalResourceWrapper} representing the multiple HAL resources; must not be
     *         {@code null}
     * @param <ResourceT>
     *         the resource type
     * @param <EmbeddedT>
     *         the embedded resource type
     * @return a {@link HalMultiResourceResponse} with {@code ACCEPTED} status and the given body
     *
     * @throws IllegalArgumentException
     *         if {@code body} is {@code null}
     */
    public static <ResourceT, EmbeddedT> HalMultiResourceResponse<ResourceT, EmbeddedT> accepted(
            @NonNull Flux<HalResourceWrapper<ResourceT, EmbeddedT>> body) {
        Assert.notNull(body, valueNotAllowedToBeNull("Body"));
        return new HalMultiResourceResponse<>(body, HttpStatus.ACCEPTED, null);
    }

    /**
     * Creates a {@link HalMultiResourceResponse} with {@link HttpStatus#ACCEPTED}.
     *
     * @param <ResourceT>
     *         the resource type
     * @param <EmbeddedT>
     *         the embedded resource type
     * @return a {@link HalMultiResourceResponse} with {@code ACCEPTED} status
     */
    public static <ResourceT, EmbeddedT> HalMultiResourceResponse<ResourceT, EmbeddedT> accepted() {
        return new HalMultiResourceResponse<>(Flux.empty(), HttpStatus.ACCEPTED, null);
    }

    /**
     * Creates a {@link HalMultiResourceResponse} with no body and {@link HttpStatus#NO_CONTENT}.
     *
     * @param <ResourceT>
     *         the resource type
     * @param <EmbeddedT>
     *         the embedded resource type
     * @return a {@link HalMultiResourceResponse} with {@code NO_CONTENT} status
     */
    public static <ResourceT, EmbeddedT> HalMultiResourceResponse<ResourceT, EmbeddedT> noContent() {
        return new HalMultiResourceResponse<>(Flux.empty(), HttpStatus.NO_CONTENT, null);
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
}
