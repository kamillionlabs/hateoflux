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

package org.kamillion.hateoflux.model.hal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.kamillion.hateoflux.model.link.IanaRelation;
import org.kamillion.hateoflux.model.link.Link;
import org.kamillion.hateoflux.model.link.LinkRelation;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.*;

/**
 * @author Younes El Ouarti
 */
@Data
public abstract class HalWrapper<HalWrapperT extends HalWrapper<? extends HalWrapperT>> {

    protected final Map<LinkRelation, Link> links = new LinkedHashMap<>();

    protected HalWrapper() {
    }

    @JsonProperty("_links")
    private Map<LinkRelation, Link> getCopyOfAllLinksAsMap() {
        return new HashMap<>(this.links);
    }

    @JsonIgnore
    public List<Link> getLinks() {
        return new ArrayList<>(links.values());
    }

    @JsonIgnore
    public Optional<Link> getLink(IanaRelation relation) {
        return Optional.ofNullable(links.get(LinkRelation.of(relation)));
    }

    @JsonIgnore
    public Optional<Link> getLink(String relation) {
        return Optional.ofNullable(links.get(LinkRelation.of(relation)));
    }

    @JsonIgnore
    public Link getRequiredLink(IanaRelation relation) {
        return links.get(LinkRelation.of(relation));
    }

    @JsonIgnore
    public Link getRequiredLink(String relation) {
        return links.get(LinkRelation.of(relation));
    }

    public HalWrapperT withLinks(@Nullable Link... links) {
        if (links != null && links.length > 0) {
            Arrays.stream(links).forEach(this::add);
        }
        return (HalWrapperT) this;
    }

    public HalWrapperT withLinks(@Nullable Iterable<Link> links) {
        if (links != null && links.iterator().hasNext()) {
            add(links);
        }
        return (HalWrapperT) this;
    }

    protected void add(@NonNull final Iterable<Link> links) {
        Assert.notNull(links, "Links is not allowed to be null");
        links.forEach(this::add);
    }

    protected void add(@NonNull final Link link) {
        Assert.notNull(link, "Link is not allowed to be null");
        final LinkRelation linkRelation = link.getLinkRelation();
        Assert.notNull(linkRelation, "Link must have a relation");
        Assert.isTrue(!linkRelation.getRelation().isBlank(), "Link must have a non empty relation");
        this.links.put(linkRelation, link);
    }

    /**
     * Determines the collection name based on {@link Relation} annotation or using a default naming strategy.
     *
     * @param clazz
     *         the resource class
     * @return the name to use for the collection
     */
    protected static String determineCollectionRelationName(Class<?> clazz) {
        return Optional.ofNullable(clazz.getAnnotation(Relation.class))
                .map(Relation::collectionRelation)
                .filter(relationName -> !relationName.isEmpty())
                .orElseGet(() -> (lowercaseFirstCharacter(clazz.getSimpleName()) + "s"));
    }

    private static String lowercaseFirstCharacter(String str) {
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }


    /**
     * Determines the entity name based on {@link Relation}  annotation or using a default naming strategy.
     *
     * @param clazz
     *         the resource class
     * @return the name to use for the entity
     */
    protected static String determineEntityRelationName(Class<?> clazz) {
        return Optional.ofNullable(clazz.getAnnotation(Relation.class))
                .map(Relation::value)
                .filter(relationName -> !relationName.isEmpty())
                .orElseGet(() -> lowercaseFirstCharacter(clazz.getSimpleName()));
    }

    /**
     * Determines the appropriate relation name for a given object based on its type. This method classifies the object
     * as either a collection or a single entity. If the object is an instance of {@link Iterable}, it is treated as a
     * collection, otherwise, it is treated as a single entity. The relation name is derived based on the
     * {@link Relation} annotation if present, or through a default naming convention otherwise.
     *
     * <p>The method returns a name that is used to represent the relationship of the object in hypermedia-driven
     * outputs:</p>
     *
     * <ul>
     *     <li>For collections: The pluralized form of the class name in camelCase or name in {@link Relation}.</li>
     *     <li>For single entities: The class name in camelCase or name in {@link Relation}.</li>
     * </ul>
     *
     * @param object
     *         The object for which to determine the relation name, must not be null (collections also not empty).
     * @return The relation name for the object, suitable for use in generating hypermedia links.
     *
     * @throws IllegalArgumentException
     *         If the object is null or empty.
     */
    protected static String determineRelationNameForObject(Object object) {
        Assert.notNull(object, "Object is not allowed to be when determining relation names");
        if (object instanceof Iterable<?> iterable) {
            Iterator<?> iterator = iterable.iterator();
            Assert.isTrue(iterator.hasNext(), "Iterable cannot be empty when determining relation names");
            Object firstElement = iterator.next();
            Class<?> clazz = firstElement.getClass();
            if (firstElement instanceof HalEmbeddedWrapper<?> wrapper) {
                clazz = wrapper.getEmbeddedEntity().getClass();
            }
            if (firstElement instanceof HalEntityWrapper<?, ?> wrapper) {
                clazz = wrapper.getEntity().getClass();
            }
            return determineCollectionRelationName(clazz);
        } else {
            return determineEntityRelationName(object.getClass());
        }
    }


}
