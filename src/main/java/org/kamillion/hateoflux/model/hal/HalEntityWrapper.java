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
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * Represents a wrapper class for adding hypermedia links to any arbitrary entity object and,
 * optionally, incorporating embedded entities. This class serves a similar purpose
 * to Spring HATEOAS' {@code EntityModel} but specifically adheres to HAL standards.
 * <p>
 * The {@link HalEntityWrapper} is a final class and not intended for extension. It encapsulates
 * an instance of {@code EntityT}, representing the primary entity, and can optionally include
 * an instance of {@code EmbeddedT} for additional embedded entities related to the primary entity.
 * This wrapper ensures that when serialized, the fields of the entity object are presented at the
 * top level.
 * <p>
 * Usage of this class involves creating an instance with the entity and, if necessary, embedded
 * entities. Links can then be added to enrich the entity model with HAL's hypermedia-driven format.
 * During serialization, this wrapper directly integrates the entity and any embedded entities into
 * the enclosing structure.
 *
 * @param <EntityT>
 *         the type of the object being wrapped, which contains the main data
 * @param <EmbeddedT>
 *         the type of the object representing additional embedded resources related to the main data, if any
 * @author Younes El Ouarti
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@JsonInclude(NON_NULL)
public final class HalEntityWrapper<EntityT, EmbeddedT>
        extends HalWrapper<HalEntityWrapper<EntityT, EmbeddedT>> {

    @JsonUnwrapped
    private final EntityT entity;

    @JsonIgnore
    private Map.Entry<String, List<HalEmbeddedWrapper<EmbeddedT>>> embedded;

    @JsonProperty("_embedded")
    private Map.Entry<String, ?> getEmbeddedForSerialization() {
        if (embedded != null) {
            List<HalEmbeddedWrapper<EmbeddedT>> embeddedList = embedded.getValue();
            if (embeddedList.size() == 1) {
                //unwrap list
                return new AbstractMap.SimpleImmutableEntry<>(embedded.getKey(), embeddedList.get(0));
            } else {
                return embedded;
            }
        } else {
            return null;
        }
    }

    private HalEntityWrapper(EntityT entity) {
        super();
        this.entity = entity;
    }

    private HalEntityWrapper(EntityT entity, String embeddedName,
                             List<HalEmbeddedWrapper<EmbeddedT>> embedded, Iterable<Link> links) {
        super();
        this.entity = entity;
        this.embedded = new AbstractMap.SimpleImmutableEntry<>(embeddedName, embedded);
        this.withLinks(links);
    }

    private HalEntityWrapper(EntityT entity, String embeddedName, HalEmbeddedWrapper<EmbeddedT> embedded,
                             Iterable<Link> links) {
        this(entity, embeddedName, List.of(embedded), links);
    }

    /**
     * Wrapper for any given entity to make it conform to HAL standards. When serializing, the fields of the entity
     * will be on top level and not nested in the object they came in.
     *
     * <p>For wrapping a list of entities, use {@link HalListWrapper} instead.</p>
     *
     * <p>The embedded entity is optional. Initially the type of the embedded is set to {@link Void} as "being
     * non-existing". The embedded is then not accessible. If embedding is desirable, methods such as
     * {@link #withEmbeddedEntity(HalEmbeddedWrapper)} or other appropriate {@code withEmbeddedXYZ()} methods
     * can be utilized.</p>
     *
     * @param <EntityT>
     *         the type of the entity to be wrapped
     * @param entity
     *         the object to wrap
     * @return a new instance containing the wrapped entity
     */
    public static <EntityT> HalEntityWrapper<EntityT, Void> wrap(@NonNull EntityT entity) {
        Assert.notNull(entity, "Entity is not allowed to be null");
        Assert.isTrue(!(entity instanceof Iterable<?>), "Entity is not allowed to be a collection/iterable. Use " +
                "HalListWrapper instead");
        return new HalEntityWrapper<>(entity);
    }


    /**
     * Adds an embedded entity i.e. object to the {@link HalEntityWrapper}. The relation name for the embedded
     * entity is determined by the {@link Relation} annotation on the entity's class, if present, or by their class
     * name otherwise.
     *
     * <p>Only one embedded element can be held at a time. If multiple objects need to be embedded, use
     * {@link #withNonEmptyEmbeddedList(List)} or {@link #withEmbeddedList(String, List)}.
     * Regardless, a {@link HalEntityWrapper} can only contain one type of embedded element at any time.</p>
     *
     * <p>Calling any {@code withEmbeddedXYZ()} method multiple times results in <b>overriding</b> the previously
     * embedded entity each time.</p>
     *
     * @param <NewEmbeddedT>
     *         the type of the items in the list to embed
     * @param embedded
     *         the entity to be embedded
     * @return new instance with the embedded entity
     *
     * @throws IllegalArgumentException
     *         if the entity is null
     */
    public <NewEmbeddedT> HalEntityWrapper<EntityT, NewEmbeddedT> withEmbeddedEntity(
            @NonNull HalEmbeddedWrapper<NewEmbeddedT> embedded) {
        Assert.notNull(embedded, "Embedded null is not allowed");
        String name = determineRelationNameForObject(embedded.getEmbeddedEntity());
        return new HalEntityWrapper<>(this.entity, name, embedded, this.getLinks());
    }

    /**
     * Adds an embedded list of type {@code NewEmbeddedT} to the {@link HalEntityWrapper}. The relation name for the
     * embedded list is determined by the {@link Relation} annotation on the list's items, if present, or by their class
     * name otherwise.
     *
     * <p>Due to type erasure with Java generics, retrieving the class name of elements in an empty list
     * is not feasible because no type information is retained at runtime. Therefore, the list must be
     * <b>initialized and not empty</b>.</p>
     *
     * <p>Calling any {@code withEmbeddedXYZ()} method multiple times results in <b>overriding</b> the previously
     * embedded entity each time.</p>
     *
     * @param <NewEmbeddedT>
     *         the type of the items in the list to embed
     * @param entitiesToEmbed
     *         the list to be embedded
     * @return new instance with the embedded list
     *
     * @throws IllegalArgumentException
     *         if the list is null or empty
     */
    public <NewEmbeddedT> HalEntityWrapper<EntityT, NewEmbeddedT> withNonEmptyEmbeddedList(
            @NonNull List<HalEmbeddedWrapper<NewEmbeddedT>> entitiesToEmbed) {
        Assert.notNull(entitiesToEmbed, "List to embed is not allowed to be null");
        Assert.notEmpty(entitiesToEmbed, "List to embed is not allowed to be empty");
        String name = determineRelationNameForObject(entitiesToEmbed);
        return new HalEntityWrapper<>(this.entity, name, entitiesToEmbed, this.getLinks());
    }

    /**
     * Adds an embedded list to the {@link HalEntityWrapper}. The list is allowed to be empty. The relation
     * name for the list is specified by {@code embeddedListName}.
     *
     * <p>Calling any {@code withEmbeddedXYZ()} method multiple times results in <b>overriding</b> the previously
     * embedded entity each time.</p>
     *
     * @param <NewEmbeddedT>
     *         the type of the items in the list to embed
     * @param embeddedListName
     *         Non-null and non-empty name or relation for the embedded list
     * @param entitiesToEmbed
     *         Non-null list to be embedded
     * @return new instance with the embedded list
     *
     * @throws IllegalArgumentException
     *         if {@code entitiesToEmbed} is null or if the {@code embeddedListName} is null or empty
     */
    public <NewEmbeddedT> HalEntityWrapper<EntityT, NewEmbeddedT> withEmbeddedList(
            @NonNull String embeddedListName, List<HalEmbeddedWrapper<NewEmbeddedT>> entitiesToEmbed) {
        Assert.notNull(embeddedListName, "Name for embedded must not be null");
        Assert.hasText(embeddedListName, "Name for embedded must not be empty or contain only whitespace");
        Assert.notNull(entitiesToEmbed, "List to embed is not allowed to be null");
        return new HalEntityWrapper<>(this.entity, embeddedListName, entitiesToEmbed, this.getLinks());
    }

    /**
     * Adds an embedded list to the {@link HalEntityWrapper}. The list is allowed to be empty. The relation
     * name for the list is determined by {@code embeddedTypeAsNameOrigin}. If the passed class is annotated with
     * {@link Relation}, the name is taken from the annotation; otherwise, it is derived from the class name.
     *
     * <p>Calling any {@code withEmbeddedXYZ()} method multiple times results in <b>overriding</b> the previously
     * embedded entity each time.</p>
     *
     * @param <NewEmbeddedT>
     *         the type of the items in the list to embed
     * @param embeddedTypeAsNameOrigin
     *         the class type used to derive the name or relation for the embedded list. This parameter must be
     *         non-null.
     * @param entitiesToEmbed
     *         the list to be embedded
     * @return a new instance with the embedded list
     *
     * @throws IllegalArgumentException
     *         if {@code entitiesToEmbed} is null or if {@code embeddedTypeAsNameOrigin} is null
     */
    public <NewEmbeddedT> HalEntityWrapper<EntityT, NewEmbeddedT> withEmbeddedList(
            @NonNull Class<?> embeddedTypeAsNameOrigin, List<HalEmbeddedWrapper<NewEmbeddedT>> entitiesToEmbed) {
        Assert.notNull(embeddedTypeAsNameOrigin, "Embedded type must not be null");
        Assert.notNull(entitiesToEmbed, "List to embed is not allowed to be null");
        String name = determineEntityRelationName(embeddedTypeAsNameOrigin);
        return new HalEntityWrapper<>(this.entity, name, entitiesToEmbed, this.getLinks());
    }


    @JsonIgnore
    public Optional<List<HalEmbeddedWrapper<EmbeddedT>>> getEmbedded() {
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
    public List<HalEmbeddedWrapper<EmbeddedT>> getRequiredEmbedded() {
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
