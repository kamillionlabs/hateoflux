/*
 * Copyright (c)  2024 kamillion-suite contributors
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

package org.kamillion.hateoflux.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Value;
import org.kamillion.hateoflux.linkbuilder.UriExpander;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * Represents a hypermedia link with various attributes defining aspects of the link such as
 * href, templated nature, and media type among others.
 *
 * @author Younes El Ouarti
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
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
     * Indicates whether the href is a URI template that should be templated with variables.
     */
    boolean templated;

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

    private Link(final LinkRelation linkRelation, final String href, final String title, final String name,
                 final String media, final String type, final boolean templated, final String deprecation,
                 final String profile, final String hreflang) {
        this.linkRelation = linkRelation;
        this.href = href;
        this.title = title;
        this.name = name;
        this.media = media;
        this.type = type;
        this.templated = templated;
        this.deprecation = deprecation;
        this.profile = profile;
        this.hreflang = hreflang;
    }

    /**
     * Appends a specified URI part to the current {@Link}'s href. The method ensures proper formatting with slashes.
     *
     * @param uriPart
     *         The URI part to be appended.
     * @return A new instance of {@link Link} with the appended URI part.
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


    public Link expand(Object... pathVariables) {
        String newHref = UriExpander.expand(this.href, pathVariables);
        return this.withHref(newHref);
    }

    public Link expand(Map<String, Object> pathVariables) {
        String newHref = UriExpander.expand(this.href, pathVariables);
        return this.withHref(newHref);
    }


    private Link(String href) {
        this(null, href, null, null, null, null, false, null, null, null);
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
        return new Link(LinkRelation.of(relation), href, null, null, null, null, false, null, null, null);
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


    /**
     * Returns a new {@link Link} that is a copy of the current link. The new link
     * includes the specified relation to describe the type of relationship to the linked resource.
     *
     * @param relation
     *         A string specifying the IANA relation of the link.
     * @return A new {@link Link} object with the specified relation added.
     */
    public Link withRel(String relation) {
        Assert.hasText(relation, "relation must not be empty");
        return new Link(LinkRelation.of(relation), this.href, this.title, this.name, this.media, this.type,
                this.templated, this.deprecation, this.profile, this.hreflang);
    }


    /**
     * Returns a new {@link Link} that is a copy of the current link. The new link
     * includes the specified IANA relation to describe the type of relationship to the linked resource.
     *
     * @param relation
     *         The IANA relation of the link.
     * @return A new {@link Link} object with the specified IANA relation added.
     */
    public Link withRel(IanaRelation relation) {
        Assert.notNull(relation, "relation must not be null");
        return new Link(LinkRelation.of(relation), this.href, this.title, this.name, this.media, this.type,
                this.templated, this.deprecation, this.profile, this.hreflang);
    }

    /**
     * Returns a new {@link Link} that is a copy of the current link, with the relation
     * set to "self". This indicates that the link's URI is a reference to the resource itself.
     *
     * @return A new {@link Link} object with the "self" relation.
     */
    public Link withSelfRel() {
        return withRel(IanaRelation.SELF);
    }

    /**
     * Returns a new {@link Link} that is a copy of the current link, with the href
     * attribute updated to the specified href.
     *
     * @param href
     *         The new href for the link.
     * @return A new {@link Link} object with the updated href.
     */
    public Link withHref(String href) {
        return new Link(this.linkRelation, href, this.title, this.name, this.media, this.type, this.templated,
                this.deprecation, this.profile, this.hreflang);
    }

    /**
     * Returns a new {@link Link} that is a copy of the current link, with the title
     * attribute updated to the specified title.
     *
     * @param title
     *         The new title for the link.
     * @return A new {@link Link} object with the updated title.
     */
    public Link withTitle(String title) {
        return new Link(this.linkRelation, this.href, title, this.name, this.media, this.type, this.templated,
                this.deprecation, this.profile, this.hreflang);
    }

    /**
     * Returns a new {@link Link} that is a copy of the current link, with the name
     * attribute updated to the specified name.
     *
     * @param name
     *         The new name for the link.
     * @return A new {@link Link} object with the updated name.
     */
    public Link withName(String name) {
        return new Link(this.linkRelation, this.href, this.title, name, this.media, this.type, this.templated,
                this.deprecation, this.profile, this.hreflang);
    }

    /**
     * Returns a new {@link Link} that is a copy of the current link, with the media
     * attribute updated to the specified media type.
     *
     * @param media
     *         The new media type for the link.
     * @return A new {@link Link} object with the updated media type.
     */
    public Link withMedia(String media) {
        return new Link(this.linkRelation, this.href, this.title, this.name, media, this.type, this.templated,
                this.deprecation, this.profile, this.hreflang);
    }

    /**
     * Returns a new {@link Link} that is a copy of the current link, with the templated
     * flag set to the specified value. This indicates whether the href attribute is
     * a URI template.
     *
     * @param templated
     *         The new value for the templated flag.
     * @return A new {@link Link} object with the updated templated flag.
     */
    public Link withTemplated(boolean templated) {
        return new Link(this.linkRelation, this.href, this.title, this.name, media, this.type, templated,
                this.deprecation, this.profile, this.hreflang);
    }

    /**
     * Returns a new {@link Link} that is a copy of the current link, with the deprecation
     * URL updated to the specified deprecation URL. This URL indicates that the linked
     * resource is deprecated.
     *
     * @param deprecation
     *         The new deprecation URL for the link.
     * @return A new {@link Link} object with the updated deprecation URL.
     */
    public Link withDeprecation(String deprecation) {
        return new Link(this.linkRelation, this.href, this.title, this.name, media, this.type, this.templated,
                deprecation, this.profile, this.hreflang);
    }

    /**
     * Returns a new {@link Link} that is a copy of the current link, with the profile
     * attribute updated to the specified profile URL. This URL indicates the schema
     * or profile that the linked resource conforms to.
     *
     * @param profile
     *         The new profile URL for the link.
     * @return A new {@link Link} object with the updated profile.
     */
    public Link withProfile(String profile) {
        return new Link(this.linkRelation, this.href, this.title, this.name, media, this.type, this.templated,
                this.deprecation, profile, this.hreflang);
    }

    /**
     * Returns a new {@link Link} that is a copy of the current link, with the hreflang
     * attribute updated to the specified language code. This code indicates the language
     * of the linked resource.
     *
     * @param hreflang
     *         The new language code for the link.
     * @return A new {@link Link} object with the updated language code.
     */
    public Link withHreflang(String hreflang) {
        return new Link(this.linkRelation, this.href, this.title, this.name, media, this.type, this.templated,
                this.deprecation, this.profile, hreflang);
    }
}

