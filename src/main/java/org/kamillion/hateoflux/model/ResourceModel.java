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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Akin to Spring's {@code RepresentationModel}
 * TODO
 *
 * @author Younes El Ouarti
 */
@JsonInclude(Include.NON_NULL)
@Data
public class ResourceModel<T> {

    @JsonProperty("_links")
    private final Map<LinkRelation, Link> links = new LinkedHashMap<>();

    private T content;

    private ResourceModel() {
    }

    public ResourceModel(T content) {
        this.content = content;
    }

    public ResourceModel(T content, Link link) {
        this(content);
        add(link);
    }

    public ResourceModel(T content, Iterable<Link> links) {
        add(links);
        this.content = content;

    }

    public static <T> ResourceModel<T> of(T content) {
        return new ResourceModel<>(content);
    }

    public ResourceModel<T> withLink(Link link) {
        add(link);
        return this;
    }

    public ResourceModel<T> withLinks(Link... links) {
        Arrays.stream(links).forEach(this::add);
        return this;
    }

    public ResourceModel<T> withLinks(Iterable<Link> links) {
        add(links);
        return this;
    }


    private void add(final Iterable<Link> links) {
        Assert.notNull(links, "Links is not allowed to be null");
        links.forEach(this::add);
    }

    public void add(final Link link) {
        Assert.notNull(link, "Link is not allowed to be null");
        final LinkRelation linkRelation = link.getLinkRelation();
        Assert.notNull(linkRelation, "Link must have a relation");
        Assert.isTrue(linkRelation.getRelation().isBlank(), "Link must have a non empty relation");
        this.links.put(linkRelation, link);
    }
}
