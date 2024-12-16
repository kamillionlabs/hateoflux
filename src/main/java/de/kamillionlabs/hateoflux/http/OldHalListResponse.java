package de.kamillionlabs.hateoflux.http;

import de.kamillionlabs.hateoflux.model.hal.HalListWrapper;
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

import static de.kamillionlabs.hateoflux.utility.ValidationMessageTemplates.valueNotAllowedToBeEmpty;
import static de.kamillionlabs.hateoflux.utility.ValidationMessageTemplates.valueNotAllowedToBeNull;

/**
 * Equivalent of a specialized {@link ResponseEntity} and a concrete implementation of {@link OldHalResponse} that is
 * able
 * to hold an instance of {@link HalListWrapper}.
 *
 * @param <ResourceT>
 *         the type of the primary resource
 * @param <EmbeddedT>
 *         the type of embedded resources
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class OldHalListResponse<ResourceT, EmbeddedT> extends OldHalResponse<HalListWrapper<ResourceT, EmbeddedT>> {

    /**
     * Creates a new {@link OldHalListResponse} with the given status code.
     *
     * @param status
     *         the HTTP status code
     * @throws IllegalArgumentException
     *         if status is null
     */
    public OldHalListResponse(@NonNull HttpStatusCode status) {
        super(status);
    }

    /**
     * Creates a new {@link OldHalListResponse} with the given body and status code.
     *
     * @param body
     *         the HAL list wrapper that holds the response content
     * @param status
     *         the HTTP status code
     * @throws IllegalArgumentException
     *         if status is null
     */
    public OldHalListResponse(HalListWrapper<ResourceT, EmbeddedT> body, @NonNull HttpStatusCode status) {
        super(body, status);
    }

    /**
     * Creates a new {@link OldHalListResponse} with the given headers and status code.
     *
     * @param headers
     *         the HTTP response headers
     * @param status
     *         the HTTP status code
     * @throws IllegalArgumentException
     *         if status is null
     */
    public OldHalListResponse(MultiValueMap<String, String> headers, @NonNull HttpStatusCode status) {
        super(headers, status);
    }

    /**
     * Creates a new {@link OldHalListResponse} with the given body, headers, and raw status code.
     *
     * @param body
     *         the HAL list wrapper that holds the response content
     * @param headers
     *         the HTTP response headers
     * @param rawStatus
     *         the HTTP status code as an integer
     * @throws IllegalArgumentException
     *         if the status code is not a valid HTTP status
     */
    public OldHalListResponse(HalListWrapper<ResourceT, EmbeddedT> body, MultiValueMap<String, String> headers,
                              int rawStatus) {
        super(body, headers, rawStatus);
    }

    /**
     * Creates a new {@link OldHalListResponse} with the given body, headers, and status code.
     *
     * @param body
     *         the HAL list wrapper that holds the response content
     * @param headers
     *         the HTTP response headers
     * @param statusCode
     *         the HTTP status code
     * @throws IllegalArgumentException
     *         if status code is null
     */
    public OldHalListResponse(HalListWrapper<ResourceT, EmbeddedT> body, MultiValueMap<String, String> headers,
                              @NonNull HttpStatusCode statusCode) {
        super(body, headers, statusCode);
    }

    /**
     * Creates a {@link OldHalListResponse} with {@link HttpStatus#OK} and the given body.
     *
     * @param <ResourceT>
     *         resource type
     * @param <EmbeddedT>
     *         embedded type
     * @param body
     *         the HAL list wrapper
     * @return the created response
     */
    public static <ResourceT, EmbeddedT> OldHalListResponse<ResourceT, EmbeddedT> ok(
            @NonNull HalListWrapper<ResourceT, EmbeddedT> body) {
        Assert.notNull(body, valueNotAllowedToBeNull("Body"));
        return new OldHalListResponse<>(body, createHeaders(), HttpStatus.OK);
    }

    /**
     * Creates a {@link OldHalListResponse} with {@link HttpStatus#CREATED} and the given body.
     *
     * @param <ResourceT>
     *         resource type
     * @param <EmbeddedT>
     *         embedded type
     * @param body
     *         the HAL list wrapper
     * @return the created response
     */
    public static <ResourceT, EmbeddedT> OldHalListResponse<ResourceT, EmbeddedT> created(
            @NonNull HalListWrapper<ResourceT, EmbeddedT> body) {
        Assert.notNull(body, valueNotAllowedToBeNull("Body"));
        return new OldHalListResponse<>(body, createHeaders(), HttpStatus.CREATED);
    }

    /**
     * Creates a {@link OldHalListResponse} with {@link HttpStatus#ACCEPTED} and the given body.
     *
     * @param <ResourceT>
     *         resource type
     * @param <EmbeddedT>
     *         embedded type
     * @param body
     *         the HAL list wrapper
     * @return the created response
     */
    public static <ResourceT, EmbeddedT> OldHalListResponse<ResourceT, EmbeddedT> accepted(
            @NonNull HalListWrapper<ResourceT, EmbeddedT> body) {
        Assert.notNull(body, valueNotAllowedToBeNull("Body"));
        return new OldHalListResponse<>(body, createHeaders(), HttpStatus.ACCEPTED);
    }

    /**
     * Creates a {@link OldHalListResponse} with {@link HttpStatus#NO_CONTENT}.
     *
     * @param <ResourceT>
     *         resource type
     * @param <EmbeddedT>
     *         embedded type
     * @return the created response
     */
    public static <ResourceT, EmbeddedT> OldHalListResponse<ResourceT, EmbeddedT> noContent() {
        return new OldHalListResponse<>(createHeaders(), HttpStatus.NO_CONTENT);
    }

    /**
     * Creates a {@link OldHalListResponse} with {@link HttpStatus#BAD_REQUEST}.
     *
     * @param <ResourceT>
     *         resource type
     * @param <EmbeddedT>
     *         embedded type
     * @return the created response
     */
    public static <ResourceT, EmbeddedT> OldHalListResponse<ResourceT, EmbeddedT> badRequest() {
        return new OldHalListResponse<>(createHeaders(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Creates a {@link OldHalListResponse} with {@link HttpStatus#NOT_FOUND}.
     *
     * @param <ResourceT>
     *         resource type
     * @param <EmbeddedT>
     *         embedded type
     * @return the created response
     */
    public static <ResourceT, EmbeddedT> OldHalListResponse<ResourceT, EmbeddedT> notFound() {
        return new OldHalListResponse<>(createHeaders(), HttpStatus.NOT_FOUND);
    }

    /**
     * Creates a {@link OldHalListResponse} with {@link HttpStatus#FORBIDDEN}.
     *
     * @param <ResourceT>
     *         resource type
     * @param <EmbeddedT>
     *         embedded type
     * @return the created response
     */
    public static <ResourceT, EmbeddedT> OldHalListResponse<ResourceT, EmbeddedT> forbidden() {
        return new OldHalListResponse<>(createHeaders(), HttpStatus.FORBIDDEN);
    }

    /**
     * Creates a {@link OldHalListResponse} with {@link HttpStatus#UNAUTHORIZED}.
     *
     * @param <ResourceT>
     *         resource type
     * @param <EmbeddedT>
     *         embedded type
     * @return the created response
     */
    public static <ResourceT, EmbeddedT> OldHalListResponse<ResourceT, EmbeddedT> unauthorized() {
        return new OldHalListResponse<>(createHeaders(), HttpStatus.UNAUTHORIZED);
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
    public OldHalListResponse<ResourceT, EmbeddedT> contentType(@NonNull MediaType mediaType) {
        Assert.notNull(mediaType, valueNotAllowedToBeNull("MediaType"));
        return new OldHalListResponse<>(this.halWrapper, withContentType(mediaType), this.httpStatusCode);
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
    public OldHalListResponse<ResourceT, EmbeddedT> location(@NonNull URI location) {
        Assert.notNull(location, valueNotAllowedToBeNull("Location URI"));
        return new OldHalListResponse<>(this.halWrapper, withLocation(location), this.httpStatusCode);
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
    public OldHalListResponse<ResourceT, EmbeddedT> eTag(@NonNull String etag) {
        Assert.notNull(etag, valueNotAllowedToBeNull("ETag"));
        Assert.isTrue(!etag.isBlank(), valueNotAllowedToBeEmpty("ETag"));
        return new OldHalListResponse<>(this.halWrapper, withETag(etag), this.httpStatusCode);
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
    public <NewResourceT, NewEmbeddedT> OldHalListResponse<NewResourceT, NewEmbeddedT> map(
            @NonNull Function<HalListWrapper<ResourceT, EmbeddedT>,
                    HalListWrapper<NewResourceT, NewEmbeddedT>> mapper) {
        Assert.notNull(mapper, valueNotAllowedToBeNull("Mapper function"));
        return new OldHalListResponse<>(
                this.halWrapper != null ? mapper.apply(this.halWrapper) : null,
                this.headers,
                this.httpStatusCode
        );
    }
}
