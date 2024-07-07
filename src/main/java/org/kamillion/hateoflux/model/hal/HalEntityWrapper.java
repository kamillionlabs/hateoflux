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
 * @since 21.06.2024
 */

package org.kamillion.hateoflux.model.hal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.kamillion.hateoflux.model.link.Link;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * Represents a wrapper class for adding HAL-compliant hypermedia links and, optionally,
 * embedded resources to any arbitrary content object. This class serves a similar purpose
 * to Spring HATEOAS' {@code EntityModel}.
 * <p>
 * The {@link HalEntityWrapper} is a final class and not intended for extension. It encapsulates
 * an instance of {@code ContentT}, representing the primary data, and can optionally include
 * an instance of {@code EmbeddedT} for additional embedded data related to the primary content.
 * This wrapper ensures that when serialized, the fields of the content object are presented at the
 * top level, conforming to HAL standards.
 * <p>
 * Usage of this class involves creating an instance with the content and, if necessary, embedded
 * resources, after which links can be added to fully comply with HAL's hypermedia-driven format.
 * During serialization, this wrapper directly integrates the content and any embedded resources into
 * the enclosing structure.
 *
 * @param <ContentT>
 *         the type of the object being wrapped, which contains the main data
 * @param <EmbeddedT>
 *         the type of the object representing additional embedded resources related to the main data, if any
 * @author Younes El Ouarti
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@JsonInclude(NON_NULL)
public final class HalEntityWrapper<ContentT, EmbeddedT>
        extends HalWrapper<HalEntityWrapper<ContentT, EmbeddedT>> {

    @JsonUnwrapped
    private final ContentT content;

    @JsonProperty("_embedded")
    private Map.Entry<String, EmbeddedT> embedded;

    private HalEntityWrapper(ContentT content) {
        super();
        this.content = content;
    }

    private HalEntityWrapper(ContentT content, String embeddedName, EmbeddedT embedded, Iterable<Link> links) {
        super();
        this.content = content;
        this.embedded = new AbstractMap.SimpleImmutableEntry<>(embeddedName, embedded);
        this.withLinks(links);
    }

    /**
     * Adds an embedded entity i.e. object to the {@link HalEntityWrapper}. The relation name for the embedded
     * entity is determined by the {@link Relation} annotation on the entity's class, if present, or by their class
     * name otherwise.
     *
     * <p>Only one embedded element can be held at a time. If multiple objects need to be embedded, use
     * {@link #withNonEmptyEmbeddedCollection(Collection)} or {@link #withEmbeddedCollection(String, Collection)}.
     * Regardless, a {@link HalEntityWrapper} can only contain one type of embedded element at any time.</p>
     *
     * <p>Calling any {@code withEmbeddedXYZ()} method multiple times results in <b>overriding</b> the previously
     * embedded content each time.</p>
     *
     * @param <T>
     *         the type of the items in the embedded collection
     * @param <S>
     *         Type of items embedded within the secondary wrapper of the nested embedding structure a.k.a. "type of the
     *         embedded inside the embedded" (Optional; see {@link #wrap(Object)} which does not include embedded
     *         elements)
     * @param embedded
     *         the collection to be embedded
     * @return new instance with the embedded collection
     *
     * @throws IllegalArgumentException
     *         if the collection is null or empty
     */
    public <T, S> HalEntityWrapper<ContentT, HalEntityWrapper<T, S>> withEmbeddedEntity(
            @NonNull HalEntityWrapper<T, S> embedded) {
        Assert.notNull(embedded, "Embedded null is not allowed");
        T embeddedContent = embedded.getContent();
        Assert.notNull(embeddedContent, "Content of embedded is not allowed to be null"); //this should never happen
        // due to check in #wrap()
        if (embeddedContent instanceof Iterable) { //this should also never happen due to check in #wrap
            // This is a preemptive check to verify that given embedded adheres to hateoas.

            // This is OK: Each item in the list is wrapped as a HAL resource (use #withEmbeddedCollection)
            //   HalEntityResource<ContentT, List<HalEntityWrapper<T,S>>
            // This is NOT: Only the list is wrapped as a HAL resource, the items could be of any type (current case)
            //   HalEntityResource<ContentT, HalEntityResource<List<T>,S>>
            throw new IllegalArgumentException("Embedded entity is not allowed to be an iterable");
        }

        String name = determineRelationNameForObject(embeddedContent);
        return new HalEntityWrapper<>(this.content, name, embedded, this.getLinks());
    }

    /**
     * Adds an embedded collection to the {@link HalEntityWrapper}. The relation name for the embedded
     * collection is determined by the {@link Relation} annotation on the collection's items, if present,
     * or by their class name otherwise.
     *
     * <p>Due to type erasure with Java generics, retrieving the class name of elements in an empty collection
     * is not feasible because no type information is retained at runtime. Therefore, the collection must be
     * <b>initialized and not empty</b>.</p>
     *
     * <p>Calling any {@code withEmbeddedXYZ()} method multiple times results in <b>overriding</b> the previously
     * embedded content each time.</p>
     *
     * @param <T>
     *         the type of the items in the embedded collection
     * @param <S>
     *         Type of items embedded within the secondary wrapper of the nested embedding structure a.k.a. "type of the
     *         embedded inside the embedded" (Optional; see {@link #wrap(Object)} which does not include embedded
     *         elements)
     * @param collectionToEmbed
     *         the collection to be embedded
     * @return new instance with the embedded collection
     *
     * @throws IllegalArgumentException
     *         if the collection is null or empty
     */
    public <T, S> HalEntityWrapper<ContentT, Collection<HalEntityWrapper<T, S>>> withNonEmptyEmbeddedCollection(
            @NonNull Collection<HalEntityWrapper<T, S>> collectionToEmbed) {
        Assert.notNull(collectionToEmbed, "Collection to embed is not allowed to be null");
        Assert.notEmpty(collectionToEmbed, "Collection to embed is not allowed to be empty");
        String name = determineRelationNameForObject(collectionToEmbed);
        return new HalEntityWrapper<>(this.content, name, collectionToEmbed, this.getLinks());
    }

    /**
     * Adds an embedded collection to the {@link HalEntityWrapper}. The collection is allowed to be empty. The relation
     * name for the collection is specified by {@code embeddedName}.
     *
     * <p>Calling any {@code withEmbeddedXYZ()} method multiple times results in <b>overriding</b> the previously
     * embedded content each time.</p>
     *
     * @param <T>
     *         the type of the items in the embedded collection
     * @param <S>
     *         Type of items embedded within the secondary wrapper of the nested embedding structure a.k.a. "type of the
     *         embedded inside the embedded" (Optional; see {@link #wrap(Object)} which does not include embedded
     *         elements)
     * @param embeddedName
     *         Non-null and non-empty name or relation for the embedded collection
     * @param collectionToEmbed
     *         the collection to be embedded
     * @return new instance with the embedded collection
     *
     * @throws IllegalArgumentException
     *         if the {@code embeddedName} is null or empty
     */
    public <T, S> HalEntityWrapper<ContentT, Collection<HalEntityWrapper<T, S>>> withEmbeddedCollection(
            @NonNull String embeddedName, Collection<HalEntityWrapper<T, S>> collectionToEmbed) {
        Assert.notNull(embeddedName, "Name for embedded must not be null");
        Assert.hasText(embeddedName, "Name for embedded must not be empty or contain only whitespace");
        return new HalEntityWrapper<>(this.content, embeddedName, collectionToEmbed, this.getLinks());
    }

    /**
     * Adds an embedded collection to the {@link HalEntityWrapper}. The collection is allowed to be empty. The relation
     * name for the collection is determined by {@code embeddedTypeAsNameOrigin}. If the passed class is annotated with
     * {@link Relation}, the name is taken from the annotation; otherwise, it is derived from the class name.
     *
     * <p>Calling any {@code withEmbeddedXYZ()} method multiple times results in <b>overriding</b> the previously
     * embedded content each time.</p>
     *
     * @param <T>
     *         the type of the items in the embedded collection
     * @param <S>
     *         Type of items embedded within the secondary wrapper of the nested embedding structure a.k.a. "type of the
     *         embedded inside the embedded" (Optional; see {@link #wrap(Object)} which does not include embedded
     *         elements)
     * @param embeddedTypeAsNameOrigin
     *         the class type used to derive the name or relation for the embedded collection.
     *         This parameter must be non-null.
     * @param collectionToEmbed
     *         the collection to be embedded
     * @return a new instance with the embedded collection
     *
     * @throws IllegalArgumentException
     *         if {@code embeddedTypeAsNameOrigin} is null
     */
    public <T, S> HalEntityWrapper<ContentT, Collection<HalEntityWrapper<T, S>>> withEmbeddedCollection(
            @NonNull Class<?> embeddedTypeAsNameOrigin, Collection<HalEntityWrapper<T, S>> collectionToEmbed) {
        Assert.notNull(embeddedTypeAsNameOrigin, "Embedded type must not be null");
        String name = determineEntityRelationName(embeddedTypeAsNameOrigin);
        return new HalEntityWrapper<>(this.content, name, collectionToEmbed, this.getLinks());
    }


    /**
     * Wrapper for any given object to make it conform to HAL standards. When serializing, the fields of the content
     * will be on top level and not nested in the object they came in. This class is similar to Spring HATEOAS'
     * {@code EntityModel}
     *
     * <p>For wrapping collections or iterables, use {@link HalCollectionWrapper} instead.</p>
     *
     * <p>The "_embedded" object is optional. Initially the type of the embedded is set to {@link Void} as "being
     * non-existing". The embedded is then not accessible. If embedding is desired, methods such as
     * {@link #withEmbeddedEntity(HalEntityWrapper)} or other appropriate {@code withEmbeddedXYZ()} methods
     * can be utilized.</p>
     *
     * @param <ContentT>
     *         the type of the object to be wrapped
     * @param content
     *         the object to wrap
     * @return a new instance containing the wrapped content
     */
    public static <ContentT> HalEntityWrapper<ContentT, Void> wrap(@NonNull ContentT content) {
        Assert.notNull(content, "Content is not allowed to be null");
        Assert.isTrue(!(content instanceof Iterable<?>), "Content is not allowed to be a collection/iterable. Use " +
                "HalCollectionWrapper instead");
        return new HalEntityWrapper<>(content);
    }


    @JsonIgnore
    public Optional<EmbeddedT> getEmbedded() {
        return Optional.ofNullable(embedded).map(Map.Entry::getValue);
    }

    @JsonIgnore
    public Optional<String> getNameOfEmbedded() {
        return Optional.ofNullable(embedded).map(Map.Entry::getKey);
    }

    @JsonIgnore
    public boolean hasEmbedded() {
        return embedded != null;
    }

    @JsonIgnore
    public EmbeddedT getRequiredEmbedded() {
        return getEmbedded()
                .orElseThrow(() -> new IllegalStateException("Attempted to retrieve a required, but non existing " +
                        "embedded"));
    }

    @JsonIgnore
    public String getRequiredNameOfEmbedded() {
        return getNameOfEmbedded()
                .orElseThrow(() -> new IllegalStateException("Attempted to retrieve the name of a required, but non " +
                        "existing embedded"));
    }
}
