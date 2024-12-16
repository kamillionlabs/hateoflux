package de.kamillionlabs.hateoflux.http;

import de.kamillionlabs.hateoflux.model.hal.HalResourceWrapper;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;

import java.net.URI;
import java.util.function.Function;

/**
 * Equivalent of a specialized {@link ResponseEntity} and a concrete implementation of {@link OldHalResponse} that is
 * able
 * to hold an instance of {@link HalResourceWrapper}.
 *
 * @param <ResourceT>
 *         the type of the primary resource
 * @param <EmbeddedT>
 *         the type of embedded resources
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class OldHalResourceResponse<ResourceT, EmbeddedT> extends OldHalResponse<HalResourceWrapper<ResourceT,
        EmbeddedT>> {

    /**
     * Creates a new {@link OldHalResourceResponse} with the given status code.
     *
     * @param status
     *         the HTTP status code
     * @throws IllegalArgumentException
     *         if status is null
     */
    public OldHalResourceResponse(@NonNull HttpStatusCode status) {
        super(status);
    }

    /**
     * Creates a new {@link OldHalResourceResponse} with the given body and status code.
     *
     * @param body
     *         the HAL resource wrapper that holds the response content
     * @param status
     *         the HTTP status code
     * @throws IllegalArgumentException
     *         if status is null
     */
    public OldHalResourceResponse(HalResourceWrapper<ResourceT, EmbeddedT> body, @NonNull HttpStatusCode status) {
        super(body, status);
    }

    /**
     * Creates a new {@link OldHalResourceResponse} with the given headers and status code.
     *
     * @param headers
     *         the HTTP response headers
     * @param status
     *         the HTTP status code
     * @throws IllegalArgumentException
     *         if status is null
     */
    public OldHalResourceResponse(MultiValueMap<String, String> headers, @NonNull HttpStatusCode status) {
        super(headers, status);
    }

    /**
     * Creates a new {@link OldHalResourceResponse} with the given body, headers, and raw status code.
     *
     * @param body
     *         the HAL resource wrapper that holds the response content
     * @param headers
     *         the HTTP response headers
     * @param rawStatus
     *         the HTTP status code as an integer
     */
    public OldHalResourceResponse(HalResourceWrapper<ResourceT, EmbeddedT> body, MultiValueMap<String, String> headers,
                                  int rawStatus) {
        super(body, headers, rawStatus);
    }

    /**
     * Creates a new {@link OldHalResourceResponse} with the given body, headers, and status code.
     *
     * @param body
     *         the HAL resource wrapper that holds the response content
     * @param headers
     *         the HTTP response headers
     * @param statusCode
     *         the HTTP status code
     * @throws IllegalArgumentException
     *         if status code is null
     */
    public OldHalResourceResponse(HalResourceWrapper<ResourceT, EmbeddedT> body, MultiValueMap<String, String> headers,
                                  @NonNull HttpStatusCode statusCode) {
        super(body, headers, statusCode);
    }

    /**
     * Creates a {@link OldHalResourceResponse} with {@link HttpStatus#OK} and the given body.
     *
     * @param <ResourceT>
     *         resource type
     * @param <EmbeddedT>
     *         embedded type
     * @param body
     *         the HAL resource wrapper
     * @return the created response
     */
    public static <ResourceT, EmbeddedT> OldHalResourceResponse<ResourceT, EmbeddedT> ok(
            HalResourceWrapper<ResourceT, EmbeddedT> body) {
        return new OldHalResourceResponse<>(body, createHeaders(), HttpStatus.OK);
    }

    /**
     * Creates a {@link OldHalResourceResponse} with {@link HttpStatus#CREATED} and the given body.
     *
     * @param <ResourceT>
     *         resource type
     * @param <EmbeddedT>
     *         embedded type
     * @param body
     *         the HAL resource wrapper
     * @return the created response
     */
    public static <ResourceT, EmbeddedT> OldHalResourceResponse<ResourceT, EmbeddedT> created(
            HalResourceWrapper<ResourceT, EmbeddedT> body) {
        return new OldHalResourceResponse<>(body, createHeaders(), HttpStatus.CREATED);
    }

    /**
     * Creates a {@link OldHalResourceResponse} with {@link HttpStatus#ACCEPTED} and the given body.
     *
     * @param <ResourceT>
     *         resource type
     * @param <EmbeddedT>
     *         embedded type
     * @param body
     *         the HAL resource wrapper
     * @return the created response
     */
    public static <ResourceT, EmbeddedT> OldHalResourceResponse<ResourceT, EmbeddedT> accepted(
            HalResourceWrapper<ResourceT, EmbeddedT> body) {
        return new OldHalResourceResponse<>(body, createHeaders(), HttpStatus.ACCEPTED);
    }

    /**
     * Creates a {@link OldHalResourceResponse} with {@link HttpStatus#NO_CONTENT}.
     *
     * @param <ResourceT>
     *         resource type
     * @param <EmbeddedT>
     *         embedded type
     * @return the created response
     */
    public static <ResourceT, EmbeddedT> OldHalResourceResponse<ResourceT, EmbeddedT> noContent() {
        return new OldHalResourceResponse<>(createHeaders(), HttpStatus.NO_CONTENT);
    }

    /**
     * Creates a {@link OldHalResourceResponse} with {@link HttpStatus#BAD_REQUEST}.
     *
     * @param <ResourceT>
     *         resource type
     * @param <EmbeddedT>
     *         embedded type
     * @return the created response
     */
    public static <ResourceT, EmbeddedT> OldHalResourceResponse<ResourceT, EmbeddedT> badRequest() {
        return new OldHalResourceResponse<>(createHeaders(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Creates a {@link OldHalResourceResponse} with {@link HttpStatus#NOT_FOUND}.
     *
     * @param <ResourceT>
     *         resource type
     * @param <EmbeddedT>
     *         embedded type
     * @return the created response
     */
    public static <ResourceT, EmbeddedT> OldHalResourceResponse<ResourceT, EmbeddedT> notFound() {
        return new OldHalResourceResponse<>(createHeaders(), HttpStatus.NOT_FOUND);
    }

    /**
     * Creates a {@link OldHalResourceResponse} with {@link HttpStatus#FORBIDDEN}.
     *
     * @param <ResourceT>
     *         resource type
     * @param <EmbeddedT>
     *         embedded type
     * @return the created response
     */
    public static <ResourceT, EmbeddedT> OldHalResourceResponse<ResourceT, EmbeddedT> forbidden() {
        return new OldHalResourceResponse<>(createHeaders(), HttpStatus.FORBIDDEN);
    }

    /**
     * Creates a {@link OldHalResourceResponse} with {@link HttpStatus#UNAUTHORIZED}.
     *
     * @param <ResourceT>
     *         resource type
     * @param <EmbeddedT>
     *         embedded type
     * @return the created response
     */
    public static <ResourceT, EmbeddedT> OldHalResourceResponse<ResourceT, EmbeddedT> unauthorized() {
        return new OldHalResourceResponse<>(createHeaders(), HttpStatus.UNAUTHORIZED);
    }

    /**
     * Sets the Content-Type header for this response.
     *
     * @param mediaType
     *         the media type to set
     * @return a new response instance with the updated Content-Type header
     *
     * @throws IllegalArgumentException
     *         if mediaType is null
     */
    public OldHalResourceResponse<ResourceT, EmbeddedT> contentType(@NonNull MediaType mediaType) {
        return new OldHalResourceResponse<>(this.halWrapper, withContentType(mediaType), this.httpStatusCode);
    }

    /**
     * Sets the Location header for this response.
     *
     * @param location
     *         the location URI
     * @return a new response instance with the updated Location header
     *
     * @throws IllegalArgumentException
     *         if location is null
     */
    public OldHalResourceResponse<ResourceT, EmbeddedT> location(@NonNull URI location) {
        return new OldHalResourceResponse<>(this.halWrapper, withLocation(location), this.httpStatusCode);
    }

    /**
     * Sets the ETag header for this response.
     *
     * @param etag
     *         the ETag value
     * @return a new response instance with the updated ETag header
     *
     * @throws IllegalArgumentException
     *         if etag is null or empty
     */
    public OldHalResourceResponse<ResourceT, EmbeddedT> eTag(@NonNull String etag) {
        return new OldHalResourceResponse<>(this.halWrapper, withETag(etag), this.httpStatusCode);
    }

    /**
     * Transforms this response's body using the provided mapping function while preserving
     * the HTTP status and headers.
     *
     * @param <NewResourceT>
     *         the new resource type
     * @param <NewEmbeddedT>
     *         the new embedded type
     * @param mapper
     *         the function to transform the body
     * @return a new response with the transformed body
     *
     * @throws IllegalArgumentException
     *         if mapper is null
     */
    public <NewResourceT, NewEmbeddedT> OldHalResourceResponse<NewResourceT, NewEmbeddedT> map(
            @NonNull Function<HalResourceWrapper<ResourceT, EmbeddedT>,
                    HalResourceWrapper<NewResourceT, NewEmbeddedT>> mapper) {
        Assert.notNull(mapper, "Mapper function must not be null");
        return new OldHalResourceResponse<>(
                this.halWrapper != null ? mapper.apply(this.halWrapper) : null,
                this.headers,
                this.httpStatusCode
        );
    }
}
