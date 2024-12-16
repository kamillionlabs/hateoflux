package de.kamillionlabs.hateoflux.http;

import de.kamillionlabs.hateoflux.model.hal.HalListWrapper;
import de.kamillionlabs.hateoflux.model.hal.HalResourceWrapper;
import de.kamillionlabs.hateoflux.model.hal.HalWrapper;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URI;

import static de.kamillionlabs.hateoflux.utility.ValidationMessageTemplates.valueNotAllowedToBeEmpty;
import static de.kamillionlabs.hateoflux.utility.ValidationMessageTemplates.valueNotAllowedToBeNull;

/**
 * Abstract base class for HAL (Hypertext Application Language) responses in a Spring WebFlux application.
 * This class serves as a specialized alternative to {@link ResponseEntity} for HAL-compliant responses,
 * wrapping {@link HalWrapper} instances to provide proper HTTP response handling.
 *
 * <p>Similar to {@link ResponseEntity}, this class allows setting HTTP headers and status codes while
 * ensuring proper serialization of HAL responses. The wrapped {@link HalWrapper} instance handles the
 * HAL-specific structure including links and embedded resources.
 *
 * @param <HalWrapperT>
 *         the type of {@link HalWrapper} being wrapped in this response
 * @see HalWrapper
 * @see HalResourceWrapper
 * @see OldHalListResponse
 * @see HalListWrapper
 * @see ResponseEntity
 */
@Getter
@EqualsAndHashCode
public abstract class OldHalResponse<HalWrapperT extends HalWrapper<?>> {

    /**
     * The HAL wrapper containing the response content.
     */
    protected HalWrapperT halWrapper;

    /**
     * HTTP headers for the response.
     */
    protected MultiValueMap<String, String> headers;

    /**
     * The HTTP status code for the response.
     */
    protected HttpStatusCode httpStatusCode;

    /**
     * Creates a new {@link OldHalResponse} with the given status code.
     *
     * @param status
     *         the HTTP status code
     * @throws IllegalArgumentException
     *         if status is null
     */
    public OldHalResponse(@NonNull HttpStatusCode status) {
        this(null, null, status);
    }

    /**
     * Creates a new {@link OldHalResponse} with the given body and status code.
     *
     * @param body
     *         the HAL wrapper body
     * @param status
     *         the HTTP status code
     * @throws IllegalArgumentException
     *         if status is null
     */
    public OldHalResponse(@Nullable HalWrapperT body, @NonNull HttpStatusCode status) {
        this(body, null, status);
    }

    /**
     * Creates a new {@link OldHalResponse} with the given headers and status code.
     *
     * @param headers
     *         the HTTP headers
     * @param status
     *         the HTTP status code
     * @throws IllegalArgumentException
     *         if status is null
     */
    public OldHalResponse(MultiValueMap<String, String> headers, @NonNull HttpStatusCode status) {
        this(null, headers, status);
    }

    /**
     * Creates a new {@link OldHalResponse} with the given body, headers, and raw status code.
     *
     * @param body
     *         the HAL wrapper body
     * @param headers
     *         the HTTP headers
     * @param rawStatus
     *         the HTTP status code as an integer
     */
    public OldHalResponse(@Nullable HalWrapperT body, @Nullable MultiValueMap<String, String> headers, int rawStatus) {
        this(body, headers, HttpStatusCode.valueOf(rawStatus));
    }

    /**
     * Creates a new {@link OldHalResponse} with the given body, headers, and status code.
     *
     * @param body
     *         the HAL wrapper body
     * @param headers
     *         the HTTP headers
     * @param statusCode
     *         the HTTP status code
     * @throws IllegalArgumentException
     *         IllegalArgumentException if status code is null
     */
    public OldHalResponse(@Nullable HalWrapperT body, @Nullable MultiValueMap<String, String> headers,
                          @NonNull HttpStatusCode statusCode) {
        Assert.notNull(statusCode, valueNotAllowedToBeNull("HttpStatusCode"));
        this.halWrapper = body;
        this.headers = headers;
        this.httpStatusCode = statusCode;
    }

    /**
     * Converts this {@link OldHalResponse} into a {@link ResponseEntity}.
     *
     * @return a {@link ResponseEntity} representing this response
     */
    public ResponseEntity<HalWrapperT> toResponseEntity() {
        return new ResponseEntity<>(halWrapper, headers, httpStatusCode);
    }

    /**
     * Helper method to create and validate headers for responses.
     *
     * @return empty headers map
     */
    protected static MultiValueMap<String, String> createHeaders() {
        return new LinkedMultiValueMap<>();
    }

    /**
     * Helper method to validate status code.
     *
     * @param status
     *         the status to validate
     * @throws IllegalArgumentException
     *         if status is null
     */
    protected static void validateStatus(@NonNull HttpStatusCode status) {
        Assert.notNull(status, valueNotAllowedToBeNull("HttpStatusCode"));
    }

    /**
     * Helper method to validate headers map.
     *
     * @param headers
     *         the headers to validate
     * @throws IllegalArgumentException
     *         if headers is null
     */
    protected static void validateHeaders(@NonNull MultiValueMap<String, String> headers) {
        Assert.notNull(headers, valueNotAllowedToBeNull("Headers"));
    }

    /**
     * Creates a new headers map with Content-Type header added.
     *
     * @param mediaType
     *         the media type to set
     * @return new headers map with the Content-Type header
     *
     * @throws IllegalArgumentException
     *         if mediaType is null
     */
    protected MultiValueMap<String, String> withContentType(@NonNull MediaType mediaType) {
        Assert.notNull(mediaType, valueNotAllowedToBeNull("MediaType"));
        MultiValueMap<String, String> newHeaders = new LinkedMultiValueMap<>();
        if (this.headers != null) {
            newHeaders.putAll(this.headers);
        }
        newHeaders.set(HttpHeaders.CONTENT_TYPE, mediaType.toString());
        return newHeaders;
    }

    /**
     * Creates a new headers map with Location header added.
     *
     * @param location
     *         the location URI
     * @return new headers map with the Location header
     *
     * @throws IllegalArgumentException
     *         if location is null
     */
    protected MultiValueMap<String, String> withLocation(@NonNull URI location) {
        Assert.notNull(location, valueNotAllowedToBeNull("Location URI"));
        MultiValueMap<String, String> newHeaders = new LinkedMultiValueMap<>();
        if (this.headers != null) {
            newHeaders.putAll(this.headers);
        }
        newHeaders.set(HttpHeaders.LOCATION, location.toString());
        return newHeaders;
    }

    /**
     * Creates a new headers map with ETag header added.
     *
     * @param etag
     *         the ETag value
     * @return new headers map with the ETag header
     *
     * @throws IllegalArgumentException
     *         if etag is null or empty
     */
    protected MultiValueMap<String, String> withETag(@NonNull String etag) {
        Assert.notNull(etag, valueNotAllowedToBeNull("ETag"));
        Assert.isTrue(!etag.isBlank(), valueNotAllowedToBeEmpty("ETag"));
        MultiValueMap<String, String> newHeaders = new LinkedMultiValueMap<>();
        if (this.headers != null) {
            newHeaders.putAll(this.headers);
        }
        newHeaders.set(HttpHeaders.ETAG, etag);
        return newHeaders;
    }
}

