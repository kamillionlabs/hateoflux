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

package de.kamillionlabs.hateoflux.model.hal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.kamillionlabs.hateoflux.model.link.IanaRelation;
import de.kamillionlabs.hateoflux.model.link.Link;
import de.kamillionlabs.hateoflux.model.link.LinkRelation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.*;

import static de.kamillionlabs.hateoflux.utility.ValidationMessageTemplates.*;


/**
 * Abstract base class for HAL wrappers, providing essential functionality for managing hypermedia links according to
 * HAL (Hypertext Application Language) standards. This class facilitates the inclusion and handling of hypermedia
 * links, crucial for API navigability.
 * <p>
 * {@link HalWrapper} includes utility functions to manage hypermedia links. These functions allow subclasses to add,
 * and retrieve links, supporting structured implementation of HAL responses.
 * <p>
 * Subclasses are responsible for specific data implementations (e.g., resources, lists, pagination), using this class's
 * link management capabilities.
 *
 * @param <HalWrapperT>
 *         the implementation that is extending {@link HalWrapper}
 * @author Younes El Ouarti
 */
@Data
public abstract class HalWrapper<HalWrapperT extends HalWrapper<? extends HalWrapperT>> {

    /**
     * Links of the {@link HalWrapper} as whole.
     */
    @Schema(hidden = true)
    protected final Map<LinkRelation, Link> links = new LinkedHashMap<>();

    /**
     * Creates empty {@link HalWrapper}
     */
    protected HalWrapper() {
    }

    @JsonProperty("_links")
    @Schema(
            description = "Hypermedia links associated with the resource.",
            type = "object"
            //            additionalPropertiesSchema = Link.class
    )
    private Map<LinkRelation, Link> getLinksForJsonRendering() {
        return new HashMap<>(this.links);
    }

    /**
     * Get the list of links of the wrapped resource(s).
     *
     * @return the list of links of the wrapped resource(s).
     */
    @JsonIgnore
    @Schema(hidden = true)
    public List<Link> getLinks() {
        return new ArrayList<>(links.values());
    }


    /**
     * Get a specific link of the links of the wrapped resource(s).
     *
     * @param relation
     *         Relation with which the link to retrieve is identified
     * @return Found link
     */
    @JsonIgnore
    @Schema(hidden = true)
    public Optional<Link> getLink(IanaRelation relation) {
        return Optional.ofNullable(links.get(LinkRelation.of(relation)));
    }

    /**
     * Get a specific link of the links of the wrapped resource(s).
     *
     * @param relation
     *         Relation with which the link to retrieve is identified
     * @return Found link
     */
    @JsonIgnore
    @Schema(hidden = true)
    public Optional<Link> getLink(String relation) {
        return Optional.ofNullable(links.get(LinkRelation.of(relation)));
    }

    /**
     * Get a specific link of the links of the wrapped resource(s). In contrast to {@link #getLink(IanaRelation)},
     * this method assumes, that the link with the provided relation exists. Otherwise, an exception is thrown.
     *
     * @param relation
     *         Relation with which the link to retrieve is identified
     * @return Found link
     */
    @JsonIgnore
    @Schema(hidden = true)
    public Link getRequiredLink(IanaRelation relation) {
        return getLink(relation).orElseThrow(() -> new IllegalStateException(
                requiredValueWasNonExisting("link with the relation '" + relation + "'")));
    }

    /**
     * Get a specific link of the links of the wrapped resource(s). In contrast to {@link #getLink(IanaRelation)},
     * this method assumes, that the link with the provided relation exists. Otherwise, an exception is thrown.
     *
     * @param relation
     *         Relation with which the link to retrieve is identified
     * @return Found link
     */
    @JsonIgnore
    @Schema(hidden = true)
    public Link getRequiredLink(String relation) {
        return getLink(relation).orElseThrow(() -> new IllegalStateException(
                requiredValueWasNonExisting("link with the relation '" + relation + "'")));
    }

    /**
     * Adds {@link Link}s to the currently wrapped resource.
     *
     * @param links
     *         links to add
     * @return New wrapper with the added links
     */
    public HalWrapperT withLinks(@Nullable Link... links) {
        if (links != null && links.length > 0) {
            Arrays.stream(links).forEach(this::add);
        }
        return (HalWrapperT) this;
    }

    /**
     * Adds {@link Link}s to the currently wrapped resource.
     *
     * @param links
     *         links to add
     * @return New wrapper with the added links
     */
    public HalWrapperT withLinks(@Nullable Iterable<Link> links) {
        if (links != null && links.iterator().hasNext()) {
            add(links);
        }
        return (HalWrapperT) this;
    }

    /**
     * Adds links to the {@link HalWrapper}. The list is not allowed to be null. The links in it must be fully specified
     * with a href and a relation.
     *
     * @param links
     *         links to add
     * @throws IllegalArgumentException
     *         if:
     *         <ul>
     *             <li>list is null</li>
     *             <li>Containing links that </li>
     *             <li>Containing links that are null or empty</li>
     *             <li>Containing links that have no href</li>
     *             <li>Containing links that have no link relation</li>
     *         </ul>
     */
    protected void add(@NonNull final Iterable<Link> links) {
        Assert.notNull(links, valueNotAllowedToBeNull("Links"));
        links.forEach(this::add);
    }

    /**
     * Adds links to the {@link HalWrapper}. The list is not allowed to be null. The links in it must be fully specified
     * with a href and a relation.
     *
     * @param link
     *         link to add
     * @throws IllegalArgumentException
     *         if:
     *         <ul>
     *             <li>link is null or empty</li>
     *             <li>link has no href</li>
     *             <li>link has no link relation</li>
     *         </ul>
     */
    protected void add(@NonNull final Link link) {
        Assert.notNull(link, valueNotAllowedToBeNull("Link"));
        Assert.notNull(link.getHref(), valueNotAllowedToBeNull("Href of link"));
        Assert.isTrue(!link.getHref().isBlank(), valueNotAllowedToBeEmpty("Href of link"));

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
     * Determines the resource name based on {@link Relation}  annotation or using a default naming strategy.
     *
     * @param clazz
     *         the resource class
     * @return the name to use for the resource
     */
    protected static String determineResourceRelationName(Class<?> clazz) {
        return Optional.ofNullable(clazz.getAnnotation(Relation.class))
                .map(Relation::value)
                .filter(relationName -> !relationName.isEmpty())
                // add fallback for cases where the Spring context isn't loaded and alias doesn't work
                .or(() -> Optional.ofNullable(clazz.getAnnotation(Relation.class))
                        .map(Relation::itemRelation)
                        .filter(relationName -> !relationName.isEmpty()))
                .orElseGet(() -> lowercaseFirstCharacter(clazz.getSimpleName()));
    }

    /**
     * Determines the appropriate relation name for a given object based on its type. This method classifies the object
     * as either a collection or a single resource. If the object is an instance of {@link Iterable}, it is treated as a
     * collection, otherwise, it is treated as a single resource. The relation name is derived based on the
     * {@link Relation} annotation if present, or through a default naming convention otherwise.
     *
     * <p>The method returns a name that is used to represent the relationship of the object in hypermedia-driven
     * outputs:</p>
     *
     * <ul>
     *     <li>For collections: The pluralized form of the class name in camelCase or name in {@link Relation}.</li>
     *     <li>For single a resource: The class name in camelCase or name in {@link Relation}.</li>
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
        Assert.notNull(object, "Object is not allowed to be null when determining relation names");
        if (object instanceof Iterable<?> iterable) {
            Iterator<?> iterator = iterable.iterator();
            Assert.isTrue(iterator.hasNext(), "Iterable cannot be empty when determining relation names");
            Object firstElement = iterator.next();
            Class<?> clazz = firstElement.getClass();
            if (firstElement instanceof HalEmbeddedWrapper<?> wrapper) {
                clazz = wrapper.getEmbeddedResource().getClass();
            }
            if (firstElement instanceof HalResourceWrapper<?, ?> wrapper) {
                clazz = wrapper.getResource().getClass();
            }
            return determineCollectionRelationName(clazz);
        } else {
            return determineResourceRelationName(object.getClass());
        }
    }

    protected static boolean isScalar(Class<?> clazz) {
        return clazz.isPrimitive()
                || clazz.equals(String.class)
                || Number.class.isAssignableFrom(clazz)
                || clazz.equals(Boolean.class)
                || clazz.equals(Character.class);
    }


}
