package de.kamillionlabs.hateoflux.http;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.net.URI;

import static de.kamillionlabs.hateoflux.utility.ValidationMessageTemplates.valueNotAllowedToBeEmpty;
import static de.kamillionlabs.hateoflux.utility.ValidationMessageTemplates.valueNotAllowedToBeNull;

/**
 * @author Younes El Ouarti
 */
public class HttpHeadersModule<HttpHeadersModuleT extends HttpHeadersModule<HttpHeadersModuleT>> {

    protected HttpHeaders headers;


    public HttpHeadersModuleT withHeader(@NonNull String name, String... values) {
        putNewHeader(name, values);
        return (HttpHeadersModuleT) this;
    }

    public HttpHeadersModuleT withContentType(@NonNull MediaType mediaType) {
        addContentType(mediaType.toString());
        return (HttpHeadersModuleT) this;
    }

    public HttpHeadersModuleT withContentType(@NonNull String mediaType) {
        addContentType(mediaType);
        return (HttpHeadersModuleT) this;
    }

    public HttpHeadersModuleT withLocation(@NonNull URI location) {
        addLocation(location.toString());
        return (HttpHeadersModuleT) this;
    }

    public HttpHeadersModuleT withLocation(@NonNull String location) {
        addLocation(location);
        return (HttpHeadersModuleT) this;
    }

    public HttpHeadersModuleT withETag(@NonNull String eTag) {
        addETag(eTag);
        return (HttpHeadersModuleT) this;
    }


    /**
     * Creates a new headers map with Content-Type header added.
     *
     * @param mediaType
     *         the media type to set
     * @throws IllegalArgumentException
     *         if mediaType is null
     */
    protected void addContentType(@NonNull String mediaType) {
        Assert.notNull(mediaType, valueNotAllowedToBeNull("MediaType"));
        Assert.notNull(mediaType, valueNotAllowedToBeEmpty("MediaType"));
        putNewHeader(HttpHeaders.CONTENT_TYPE, mediaType);
    }

    /**
     * Creates a new headers map with Location header added.
     *
     * @param location
     *         the location URI
     * @throws IllegalArgumentException
     *         if location is null
     */
    protected void addLocation(@NonNull String location) {
        Assert.notNull(location, valueNotAllowedToBeNull("Location URI"));
        Assert.notNull(location, valueNotAllowedToBeEmpty("Location URI"));
        putNewHeader(HttpHeaders.LOCATION, location);
    }

    /**
     * Creates a new headers map with ETag header added.
     *
     * @param eTag
     *         the ETag value
     * @throws IllegalArgumentException
     *         if etag is null or empty
     */
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
