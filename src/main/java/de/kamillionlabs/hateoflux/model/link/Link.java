/*
 * Copyright (c)  2024 kamillionlabs contributors
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
 *
 * @since 23.05.2024
 */

package de.kamillionlabs.hateoflux.model.link;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.kamillionlabs.hateoflux.linkbuilder.BaseUrlExtractor;
import de.kamillionlabs.hateoflux.linkbuilder.UriExpander;
import de.kamillionlabs.hateoflux.linkbuilder.UriTemplateData;
import lombok.Getter;
import lombok.Value;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

import java.util.Map;

import static de.kamillionlabs.hateoflux.utility.ValidationMessageTemplates.valueNotAllowedToBeEmpty;

/**
 * Represents a hypermedia link with various attributes defining aspects of the link such as
 * href, templated nature, and media type among others.
 *
 * @author Younes El Ouarti
 */
@Getter
@JsonInclude(Include.NON_NULL)
@Value
public class Link {

    /**
     * The relationship between the current resource and the linked resource. Common
     * values include "self", "next", "previous", etc. Custom relations can also be used.
     */
    @JsonIgnore
    LinkRelation linkRelation;

    /**
     * The URI of the linked resource. This is a required attribute and is the
     * actual URL where the resource can be accessed.
     */
    String href;

    /**
     * A human-readable title for the link, which can be used for labeling the
     * link in user interfaces.
     */
    String title;

    /**
     * An identifier or label for the link, used for documentation or as additional
     * metadata in client applications.
     */
    String name;

    /**
     * Describes the media type of the linked resource, often used to specify the
     * type of content that the client can expect at the URL, such as "application/json"
     * or "text/html".
     */
    String media;

    /**
     * Further specifies the MIME type of the linked resource's expected content.
     * This can be used to indicate more specific formats when multiple representations
     * are available.
     */
    String type;

    /**
     * A URL that provides information about the deprecation of the link, useful
     * for alerting API consumers that a resource is outdated or scheduled for removal.
     */
    String deprecation;

    /**
     * A hint about the profile (or schema) that the linked resource conforms to,
     * providing additional semantics about the linked resource.
     */
    String profile;

    /**
     * Specifies the language of the linked resource, useful for applications supporting
     * multiple languages.
     */
    String hreflang;

    // CONSTRUCTORS AND CREATORS ---------------------------------------------------------------------------------------

    private Link(final LinkRelation linkRelation, final String href, final String title, final String name,
                 final String media, final String type, final String deprecation,
                 final String profile, final String hreflang) {
        this.linkRelation = linkRelation;
        this.href = href;
        this.title = title;
        this.name = name;
        this.media = media;
        this.type = type;
        this.deprecation = deprecation;
        this.profile = profile;
        this.hreflang = hreflang;
    }

    private Link(String href) {
        this(null, href, null, null, null, null, null, null, null);
    }


    /**
     * Creates a new {@link Link} instance with the specified href but without any IANA relation.
     * This is useful for creating links that do not need to express a specific relationship type.
     *
     * @param href
     *         Hypertext REFerence, commonly known as a URL (e.g., https://www.github.com).
     * @return A new instance of {@link Link} with no IANA relation.
     */
    public static Link of(String href) {
        return new Link(href);
    }

    /**
     * Creates a new {@link Link} instance and associates it with a specified IANA relation.
     * This method defines the type of relationship between the current resource and the linked resource.
     *
     * @param href
     *         Hypertext REFerence, commonly known as a URL (e.g., https://www.github.com).
     * @param relation
     *         IANA relation of the link, which must not be null (see {@link IanaRelation} for details).
     * @return A new instance of {@link Link} with the specified IANA relation.
     */
    public static Link of(IanaRelation relation, String href) {
        Assert.notNull(relation, "relation must not be null");
        return new Link(LinkRelation.of(relation), href, null, null, null, null, null, null, null);
    }

    /**
     * Creates a new {@link Link} instance with an IANA relation of type {@link IanaRelation#SELF}.
     * This type indicates that the link's URI is a reference to the resource itself.
     *
     * @param href
     *         Hypertext REFerence, commonly known as a URL (e.g., https://www.github.com).
     * @return A new instance of {@link Link} with a "self" IANA relation.
     */
    public static Link linkAsSelfOf(String href) {
        return of(IanaRelation.SELF, href);
    }


    // SPECIAL GETTERS -------------------------------------------------------------------------------------------------

    @JsonProperty("templated")
    private Boolean isTemplatedForJsonRendering() {
        return UriTemplateData.of(href).isTemplated() ? true : null;
    }

    /**
     * Indicates whether the href is a URI template that should be templated with variables.
     *
     * @return {@code true} if href of the link is templated; {@code false} otherwise
     */
    @JsonIgnore
    public boolean isTemplated() {
        return UriTemplateData.of(href).isTemplated();
    }

    // SETTER ----------------------------------------------------------------------------------------------------------

    /**
     * The relationship between the current resource and the linked resource. Common
     * values include "self", "next", "previous", etc. Custom relations can also be used.
     *
     * @param relation
     *         A string specifying the IANA relation of the link.
     * @return Returns a new {@link Link} that is a copy of the current link with the specified relation added.
     */
    public Link withRel(String relation) {
        Assert.hasText(relation, valueNotAllowedToBeEmpty("relation"));
        return new Link(LinkRelation.of(relation), this.href, this.title, this.name, this.media, this.type,
                this.deprecation, this.profile, this.hreflang);
    }

    /**
     * The relationship between the current resource and the linked resource. Common
     * values include "self", "next", "previous", etc. Custom relations can also be used.
     *
     * @param relation
     *         The IANA relation of the link.
     * @return Returns a new {@link Link} that is a copy of the current link with the specified IANA relation added.
     */
    public Link withRel(IanaRelation relation) {
        Assert.notNull(relation, "relation must not be null");
        return new Link(LinkRelation.of(relation), this.href, this.title, this.name, this.media, this.type,
                this.deprecation, this.profile, this.hreflang);
    }

    /**
     * The relationship between the current resource and the linked resource. Common
     * values include "self", "next", "previous", etc. Custom relations can also be used.
     *
     * @return Returns a new {@link Link} that is a copy of the current link with the "self" relation.
     */
    public Link withSelfRel() {
        return withRel(IanaRelation.SELF);
    }

    /**
     * The URI of the linked resource. This is a required attribute and is the
     * actual URL where the resource can be accessed.
     *
     * @param href
     *         The new href for the link.
     * @return Returns a new {@link Link} that is a copy of the current link with the updated href.
     */
    public Link withHref(String href) {
        return new Link(this.linkRelation, href, this.title, this.name, this.media, this.type,
                this.deprecation, this.profile, this.hreflang);
    }

    /**
     * A human-readable title for the link, which can be used for labeling the
     * link in user interfaces.
     *
     * @param title
     *         The new title for the link.
     * @return Returns a new {@link Link} that is a copy of the current link with the updated title.
     */
    public Link withTitle(String title) {
        return new Link(this.linkRelation, this.href, title, this.name, this.media, this.type,
                this.deprecation, this.profile, this.hreflang);
    }

    /**
     * An identifier or label for the link, used for documentation or as additional
     * metadata in client applications.
     *
     * @param name
     *         The new name for the link.
     * @return Returns a new {@link Link} that is a copy of the current link with the updated name.
     */
    public Link withName(String name) {
        return new Link(this.linkRelation, this.href, this.title, name, this.media, this.type,
                this.deprecation, this.profile, this.hreflang);
    }

    /**
     * Describes the media type of the linked resource, often used to specify the
     * type of content that the client can expect at the URL, such as "application/json"
     * or "text/html".
     *
     * @param media
     *         The new media type for the link.
     * @return Returns a new {@link Link} that is a copy of the current link with the updated media type.
     */
    public Link withMedia(String media) {
        return new Link(this.linkRelation, this.href, this.title, this.name, media, this.type,
                this.deprecation, this.profile, this.hreflang);
    }

    /**
     * Further specifies the MIME type of the linked resource's expected content.
     * This can be used to indicate more specific formats when multiple representations
     * are available.
     *
     * @param type
     *         The new MIME type for the link.
     * @return Returns a new {@link Link} that is a copy of the current link with the updated type.
     */
    public Link withType(String type) {
        return new Link(this.linkRelation, this.href, this.title, this.name, this.media, type,
                this.deprecation, this.profile, this.hreflang);
    }

    /**
     * A URL that provides information about the deprecation of the link, useful
     * for alerting API consumers that a resource is outdated or scheduled for removal.
     *
     * @param deprecation
     *         The new deprecation URL for the link.
     * @return Returns a new {@link Link} that is a copy of the current link with the updated deprecation URL.
     */
    public Link withDeprecation(String deprecation) {
        return new Link(this.linkRelation, this.href, this.title, this.name, this.media, this.type,
                deprecation, this.profile, this.hreflang);
    }

    /**
     * A hint about the profile (or schema) that the linked resource conforms to,
     * providing additional semantics about the linked resource.
     *
     * @param profile
     *         The new profile URL for the link.
     * @return Returns a new {@link Link} that is a copy of the current link with the updated profile.
     */
    public Link withProfile(String profile) {
        return new Link(this.linkRelation, this.href, this.title, this.name, this.media, this.type,
                this.deprecation, profile, this.hreflang);
    }

    /**
     * Specifies the language of the linked resource, useful for applications supporting
     * multiple languages.
     *
     * @param hreflang
     *         The new language code for the link.
     * @return Returns a new {@link Link} that is a copy of the current link with the updated language code.
     */
    public Link withHreflang(String hreflang) {
        return new Link(this.linkRelation, this.href, this.title, this.name, this.media, this.type,
                this.deprecation, this.profile, hreflang);
    }


    // UTILITY ---------------------------------------------------------------------------------------------------------

    /**
     * Appends a specified URI part to the current {@link Link}'s href. The method ensures proper formatting with
     * slashes.
     *
     * @param uriPart
     *         The URI part to be appended.
     * @return A new {@link Link} object with the appended URI part.
     */
    public Link slash(String uriPart) {
        StringBuffer newHref = new StringBuffer(this.href);

        if (!this.href.endsWith("/")) {
            newHref.append("/");
        }

        if (StringUtils.hasText(uriPart)) {
            if (uriPart.startsWith("/")) {
                newHref.append(uriPart.substring(1));
            } else {
                newHref.append(uriPart);
            }
        }

        return this.withHref(newHref.toString());
    }


    /**
     * Extracts the base URL from the given {@link ServerHttpRequest} and prepends it to the current {@code href}.
     *
     * @param httpRequest
     *         The request that provides the data to extract the base URL from.
     * @return A new {@link Link} object with the prepended base URL.
     */
    public Link prependBaseUrl(ServerHttpRequest httpRequest) {
        String baseUrl = BaseUrlExtractor.extractBaseUrl(httpRequest);
        return prependBaseUrl(baseUrl);
    }

    /**
     * Extracts the base URL from the given {@link ServerWebExchange} and prepends it to the current {@code href}.
     *
     * @param exchangeWithBaseUrl
     *         The exchange that provides the data to extract the base URL from.
     * @return A new {@link Link} object with the prepended base URL.
     */
    public Link prependBaseUrl(ServerWebExchange exchangeWithBaseUrl) {
        String baseUrl = BaseUrlExtractor.extractBaseUrl(exchangeWithBaseUrl);
        return prependBaseUrl(baseUrl);
    }

    /**
     * Prepends the provided base URL to the current {@code href}.
     *
     * @param baseUrl
     *         Base URL to prepend to current link
     * @return A new {@link Link} object with the prepended base URL.
     */
    public Link prependBaseUrl(String baseUrl) {
        String currentHref = this.getHref();
        Link finalHref = Link.of(baseUrl).slash(currentHref);
        return this.withHref(finalHref.getHref());
    }


    /**
     * Utility method that serves as a proxy for {@link UriExpander#expand(String, Object...)}. Please refer to
     * mentioned method for full documentation.
     *
     * @param parameters
     *         parameters to expand in templated href
     * @return The expanded or original URI if expansion is not applicable
     */
    public Link expand(Object... parameters) {
        String newHref = UriExpander.expand(this.href, parameters);
        return this.withHref(newHref);
    }

    /**
     * Utility method that serves as a proxy for {@link UriExpander#expand(String, Map, boolean)}. Please refer to
     * mentioned method for full documentation.
     *
     * @param parameters
     *         to expand in templated href
     * @param collectionRenderedAsComposite
     *         specifies whether the collection should be rendered as composite (true) or non-composite (false)
     * @return the expanded or original URI if expansion is not applicable
     */
    public Link expand(Map<String, Object> parameters, boolean collectionRenderedAsComposite) {
        String newHref = UriExpander.expand(this.href, parameters, collectionRenderedAsComposite);
        return this.withHref(newHref);
    }

    /**
     * Utility method that serves as a proxy for {@link UriExpander#expand(String, Map)}. Please refer to
     * mentioned method for full documentation.
     *
     * @param parameters
     *         to expand in templated href
     * @return the expanded or original URI if expansion is not applicable
     */
    public Link expand(Map<String, Object> parameters) {
        return expand(parameters, false);
    }
}

