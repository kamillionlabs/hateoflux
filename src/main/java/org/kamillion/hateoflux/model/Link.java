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
import org.springframework.util.Assert;

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


    private Link(String href) {
        this(null, href, null, null, null, null, false, null, null, null);
    }

    public static Link of(String href) {
        return new Link(href);
    }

    public static Link of(IanaRelation relation, String href) {
        Assert.notNull(relation, "relation must not be empty");
        return new Link(LinkRelation.of(relation), href, null, null, null, null, false, null, null, null);
    }

    public static Link linkAsSelfOf(String href) {
        return of(IanaRelation.SELF, href);
    }

    public Link withRel(String relation) {
        Assert.hasText(relation, "relation must not be empty");
        return new Link(LinkRelation.of(relation), this.href, this.title, this.name, this.media, this.type, this.templated, this.deprecation, this.profile, this.hreflang);
    }

    public Link withRel(IanaRelation relation) {
        Assert.notNull(relation, "relation must not be null");
        return new Link(LinkRelation.of(relation), this.href, this.title, this.name, this.media, this.type, this.templated, this.deprecation, this.profile, this.hreflang);
    }

    public Link withSelfRel() {
        return withRel(IanaRelation.SELF);
    }

    public Link withHref(String href) {
        return new Link(this.linkRelation, href, this.title, this.name, this.media, this.type, this.templated, this.deprecation, this.profile, this.hreflang);
    }

    public Link withTitle(String title) {
        return new Link(this.linkRelation, this.href, title, this.name, this.media, this.type, this.templated, this.deprecation, this.profile, this.hreflang);
    }

    public Link withName(String name) {
        return new Link(this.linkRelation, this.href, this.title, name, this.media, this.type, this.templated, this.deprecation, this.profile, this.hreflang);
    }

    public Link withMedia(String media) {
        return new Link(this.linkRelation, this.href, this.title, this.name, media, this.type, this.templated, this.deprecation, this.profile, this.hreflang);
    }

    public Link withTemplated(boolean templated) {
        return new Link(this.linkRelation, this.href, this.title, this.name, media, this.type, templated, this.deprecation, this.profile, this.hreflang);
    }

    public Link withDeprecation(String deprecation) {
        return new Link(this.linkRelation, this.href, this.title, this.name, media, this.type, this.templated, deprecation, this.profile, this.hreflang);
    }

    public Link withProfile(String profile) {
        return new Link(this.linkRelation, this.href, this.title, this.name, media, this.type, this.templated, this.deprecation, profile, this.hreflang);
    }

    public Link withHreflang(String hreflang) {
        return new Link(this.linkRelation, this.href, this.title, this.name, media, this.type, this.templated, this.deprecation, this.profile, hreflang);
    }
}

