package de.kamillionlabs.hateoflux.http;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.net.URI;

import static de.kamillionlabs.hateoflux.utility.ValidationMessageTemplates.valueNotAllowedToBeEmpty;
import static de.kamillionlabs.hateoflux.utility.ValidationMessageTemplates.valueNotAllowedToBeNull;

/**
 * A fluent builder for {@link HttpHeaders} that allows for the convenient addition of common HTTP headers.
 *
 * <p>This module provides methods to add headers such as {@code Content-Type}, {@code Location}, and {@code ETag}.
 * It utilizes method chaining to enable a readable and concise way of constructing {@link HttpHeaders} instances.</p>
 *
 * <p>The class is generic, allowing subclasses to return their own type from the builder methods, facilitating
 * extensibility and customization.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * HttpHeaders headers = new HttpHeadersModule<>()
 *     .withContentType(MediaType.APPLICATION_JSON)
 *     .withLocation(new URI("http://example.com/resource"))
 *     .withETag("\"123456\"");
 * }</pre>
 *
 * @param <HttpHeadersModuleT>
 *         the type of the concrete subclass extending this module
 * @author Younes El Ouarti
 * @see HttpHeaders
 */
public class HttpHeadersModule<HttpHeadersModuleT extends HttpHeadersModule<HttpHeadersModuleT>> {

    protected HttpHeaders headers;


    /**
     * Adds a new header with the specified name and values.
     *
     * <p>If the header already exists, the new values are appended to the existing ones.</p>
     *
     * @param name
     *         the name of the header to add; must not be {@code null} or empty
     * @param values
     *         the values of the header
     * @return the current instance for method chaining
     *
     * @throws IllegalArgumentException
     *         if {@code name} is {@code null} or empty
     */
    public HttpHeadersModuleT withHeader(@NonNull String name, String... values) {
        putNewHeader(name, values);
        return (HttpHeadersModuleT) this;
    }

    /**
     * Adds a {@code Content-Type} header with the specified {@link MediaType}.
     *
     * @param mediaType
     *         the media type to set; must not be {@code null}
     * @return the current instance for method chaining
     *
     * @throws IllegalArgumentException
     *         if {@code mediaType} is {@code null} or empty
     */
    public HttpHeadersModuleT withContentType(@NonNull MediaType mediaType) {
        addContentType(mediaType.toString());
        return (HttpHeadersModuleT) this;
    }

    /**
     * Adds a {@code Content-Type} header with the specified media type string.
     *
     * @param mediaType
     *         the media type string to set; must not be {@code null} or empty
     * @return the current instance for method chaining
     *
     * @throws IllegalArgumentException
     *         if {@code mediaType} is {@code null} or empty
     */
    public HttpHeadersModuleT withContentType(@NonNull String mediaType) {
        addContentType(mediaType);
        return (HttpHeadersModuleT) this;
    }

    /**
     * Adds a {@code Location} header with the specified {@link URI}.
     *
     * @param location
     *         the URI to set as the location; must not be {@code null} or empty
     * @return the current instance for method chaining
     *
     * @throws IllegalArgumentException
     *         if {@code location} is {@code null} or empty
     */
    public HttpHeadersModuleT withLocation(@NonNull URI location) {
        addLocation(location.toString());
        return (HttpHeadersModuleT) this;
    }

    /**
     * Adds a {@code Location} header with the specified location string.
     *
     * @param location
     *         the location string to set; must not be {@code null} or empty
     * @return the current instance for method chaining
     *
     * @throws IllegalArgumentException
     *         if {@code location} is {@code null} or empty
     */
    public HttpHeadersModuleT withLocation(@NonNull String location) {
        addLocation(location);
        return (HttpHeadersModuleT) this;
    }

    /**
     * Adds an {@code ETag} header with the specified ETag value.
     *
     * @param eTag
     *         the ETag value to set; must not be {@code null} or empty
     * @return the current instance for method chaining
     *
     * @throws IllegalArgumentException
     *         if {@code eTag} is {@code null} or empty
     */
    public HttpHeadersModuleT withETag(@NonNull String eTag) {
        addETag(eTag);
        return (HttpHeadersModuleT) this;
    }


    protected void addContentType(@NonNull String mediaType) {
        Assert.notNull(mediaType, valueNotAllowedToBeNull("MediaType"));
        Assert.isTrue(!mediaType.isBlank(), valueNotAllowedToBeEmpty("MediaType"));
        putNewHeader(HttpHeaders.CONTENT_TYPE, mediaType);
    }

    protected void addLocation(@NonNull String location) {
        Assert.notNull(location, valueNotAllowedToBeNull("Location URI"));
        Assert.isTrue(!location.isBlank(), valueNotAllowedToBeEmpty("Location URI"));
        putNewHeader(HttpHeaders.LOCATION, location);
    }

    protected void addETag(@NonNull String eTag) {
        Assert.notNull(eTag, valueNotAllowedToBeNull("ETag"));
        Assert.isTrue(!eTag.isBlank(), valueNotAllowedToBeEmpty("ETag"));
        putNewHeader(HttpHeaders.ETAG, eTag);
    }

    protected void putNewHeader(String key, String... values) {
        Assert.notNull(key, valueNotAllowedToBeNull("Key of attribute to put in header"));
        Assert.isTrue(!key.isBlank(), valueNotAllowedToBeNull("Key of attribute to put in header"));
        HttpHeaders newHeaders = new HttpHeaders();
        if (this.headers != null) {
            newHeaders.putAll(this.headers);
        }
        this.headers = newHeaders;

        for (String value : values) {
            this.headers.add(key, value);
        }
    }
}
