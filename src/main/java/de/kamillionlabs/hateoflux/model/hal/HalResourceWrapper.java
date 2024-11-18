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
 * @since 21.06.2024
 */

package de.kamillionlabs.hateoflux.model.hal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import de.kamillionlabs.hateoflux.model.link.Link;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.util.*;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static de.kamillionlabs.hateoflux.utility.ValidationMessageTemplates.*;

/**
 * Represents an immutable wrapper class for adding hypermedia links to any arbitrary resource object and, optionally,
 * incorporating embedded resources, adhering to HAL standards.
 * <p>
 * The {@link HalResourceWrapper} is a final class and not intended for extension. It encapsulates an instance of
 * {@code ResourceT}, representing the primary or main resource, and can optionally include an instance of
 * {@code EmbeddedT} for additional embedded resources related to said main resource. This wrapper ensures that when
 * serialized, the fields of the resource object are presented at the top level.
 * <p>
 * Usage of this class involves creating an instance with the resource and, if necessary, embedded resources. Links can
 * then be added to enrich the resource model with HAL's hypermedia-driven format. During serialization, this wrapper
 * directly integrates the resource and any embedded resources into the enclosing structure.
 *
 * @param <ResourceT>
 *         the type of the object being wrapped, which contains the main data
 * @param <EmbeddedT>
 *         the type of the object representing additional embedded resources related to the main data, if any
 * @author Younes El Ouarti
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@JsonInclude(NON_NULL)
public final class HalResourceWrapper<ResourceT, EmbeddedT>
        extends HalWrapper<HalResourceWrapper<ResourceT, EmbeddedT>> {

    @JsonUnwrapped
    private final ResourceT resource;

    @JsonIgnore
    private Map.Entry<String, List<HalEmbeddedWrapper<EmbeddedT>>> embedded;

    @JsonIgnore
    private Boolean isEmbeddedOriginallyAList;

    @JsonProperty("_embedded")
    private Map.Entry<String, ?> getEmbeddedForSerialization() {
        if (embedded != null && embedded.getValue() != null) {
            if (isEmbeddedOriginallyAList) {
                return embedded;
            } else {
                List<HalEmbeddedWrapper<EmbeddedT>> embeddedList = embedded.getValue();
                HalEmbeddedWrapper<EmbeddedT> embeddedWrapper = embeddedList.get(0);
                if (embeddedWrapper.isEmpty()) {
                    return null;
                } else {
                    //unwrap list
                    return new AbstractMap.SimpleImmutableEntry<>(embedded.getKey(), embeddedWrapper);
                }
            }
        } else {
            return null;
        }
    }

    private HalResourceWrapper(ResourceT resource) {
        super();
        this.resource = resource;
    }

    private HalResourceWrapper(ResourceT resource, String embeddedName,
                               List<HalEmbeddedWrapper<EmbeddedT>> embedded, Iterable<Link> links) {
        super();
        this.isEmbeddedOriginallyAList = true;
        this.resource = resource;
        this.embedded = new AbstractMap.SimpleImmutableEntry<>(embeddedName, embedded);
        this.withLinks(links);
    }

    private HalResourceWrapper(ResourceT resource, String embeddedName, HalEmbeddedWrapper<EmbeddedT> embedded,
                               Iterable<Link> links) {
        this(resource, embeddedName, List.of(embedded), links);
        this.isEmbeddedOriginallyAList = false;
    }

    /**
     * Wraps any given resource into a {@link HalResourceWrapper} to make it conform to HAL standards.
     *
     * <p>For wrapping a list of resources, use {@link HalListWrapper} instead.</p>
     *
     * <p>The embedded resource is optional. Initially the type of the embedded is set to {@link Void} as "being
     * non-existing". The embedded is then not accessible. If embedding is desirable, methods such as
     * {@link #withEmbeddedResource(HalEmbeddedWrapper)} or other appropriate {@code withEmbeddedXYZ()} methods
     * can be utilized.</p>
     *
     * @param <ResourceT>
     *         the type of the resource to be wrapped
     * @param resource
     *         the object to wrap
     * @return a new instance containing the wrapped resource
     */
    public static <ResourceT> HalResourceWrapper<ResourceT, Void> wrap(@NonNull ResourceT resource) {
        Assert.notNull(resource, valueNotAllowedToBeNull("Resource"));
        Assert.isTrue(!(resource instanceof Iterable<?>), valueIsNotAllowedToBeOfType("Resource", "collection" +
                "/iterable. " +
                "Use HalListWrapper instead"));
        return new HalResourceWrapper<>(resource);
    }


    /**
     * Adds an embedded resource i.e. object to the {@link HalResourceWrapper}. The relation name for the embedded
     * resource is determined by the {@link Relation} annotation on the resource's class, if present, or by their class
     * name otherwise.
     *
     * <p>Only one embedded element can be held at a time. If multiple objects need to be embedded, use
     * {@link #withNonEmptyEmbeddedList(List)}, {@link #withEmbeddedList(String, List)}, or
     * {@link #withEmbeddedList(Class, List)}.
     *
     * <p>Calling any {@code withEmbeddedXYZ()} method multiple times results in <b>overriding</b> the previously
     * embedded resource each time.</p>
     *
     * <p>
     * <b>Hint:</b> Call this method with an empty {@code embedded} ({@link HalEmbeddedWrapper#empty()}) if the
     * embedded type is desired to be changed from {@code Void}.
     *
     * @param <NewEmbeddedT>
     *         the type of the resource to embed
     * @param embedded
     *         the resource to embed (may be empty, but not null)
     * @return new instance with the embedded resource
     *
     * @throws IllegalArgumentException
     *         if the resource is null
     */
    public <NewEmbeddedT> HalResourceWrapper<ResourceT, NewEmbeddedT> withEmbeddedResource(HalEmbeddedWrapper<NewEmbeddedT> embedded) {
        if (embedded == null || embedded.isEmpty()) {
            //when serialized, the embedded will be removed from the JSON. Name doesn't matter.
            return new HalResourceWrapper<>(this.resource, "n/a", embedded, this.getLinks());
        }
        String name = determineRelationNameForObject(embedded.getEmbeddedResource());
        return new HalResourceWrapper<>(this.resource, name, embedded, this.getLinks());
    }

    /**
     * Adds an embedded list of type {@code NewEmbeddedT} to the {@link HalResourceWrapper}. The relation name for the
     * embedded list is determined by the {@link Relation} annotation on the list's items, if present, or by their class
     * name otherwise.
     *
     * <p>Due to type erasure with Java generics, retrieving the class name of elements in an empty list
     * is not feasible because no type information is retained at runtime. Therefore, the list must be
     * <b>initialized and not empty</b>.</p>
     *
     * <p>Calling any {@code withEmbeddedXYZ()} method multiple times results in <b>overriding</b> the previously
     * embedded resource each time.</p>
     *
     * @param <NewEmbeddedT>
     *         the type of the items in the list to embed
     * @param resourcesToEmbed
     *         the list to be embedded
     * @return new instance with the embedded list
     *
     * @throws IllegalArgumentException
     *         if the list is null or empty
     */
    public <NewEmbeddedT> HalResourceWrapper<ResourceT, NewEmbeddedT> withNonEmptyEmbeddedList(
            @NonNull List<HalEmbeddedWrapper<NewEmbeddedT>> resourcesToEmbed) {
        Assert.notNull(resourcesToEmbed, valueNotAllowedToBeNull("List to embed"));
        Assert.notEmpty(resourcesToEmbed, valueNotAllowedToBeEmpty("List to embed"));
        String name = determineRelationNameForObject(resourcesToEmbed);
        return new HalResourceWrapper<>(this.resource, name, resourcesToEmbed, this.getLinks());
    }

    /**
     * Adds an embedded list to the {@link HalResourceWrapper}. The list is allowed to be empty. The relation
     * name for the list is specified by {@code embeddedListName}.
     *
     * <p>Calling any {@code withEmbeddedXYZ()} method multiple times results in <b>overriding</b> the previously
     * embedded resource each time.</p>
     *
     * @param <NewEmbeddedT>
     *         the type of the items in the list to embed
     * @param embeddedListName
     *         Non-null and non-empty name or relation for the embedded list
     * @param resourcesToEmbed
     *         Non-null list to be embedded
     * @return new instance with the embedded list
     *
     * @throws IllegalArgumentException
     *         if {@code resourcesToEmbed} is null or if the {@code embeddedListName} is null or empty
     */
    public <NewEmbeddedT> HalResourceWrapper<ResourceT, NewEmbeddedT> withEmbeddedList(
            @NonNull String embeddedListName, List<HalEmbeddedWrapper<NewEmbeddedT>> resourcesToEmbed) {
        Assert.notNull(embeddedListName, valueNotAllowedToBeNull("Name for embedded"));
        Assert.hasText(embeddedListName, valueNotAllowedToBeEmpty("Name for embedded"));
        Assert.notNull(resourcesToEmbed, valueNotAllowedToBeNull("List to embed"));
        return new HalResourceWrapper<>(this.resource, embeddedListName, resourcesToEmbed, this.getLinks());
    }

    /**
     * Adds an embedded list to the {@link HalResourceWrapper}. The list is allowed to be empty. The relation
     * name for the list is determined by {@code embeddedTypeAsNameOrigin}. If the passed class is annotated with
     * {@link Relation}, the name is taken from the annotation; otherwise, it is derived from the class name.
     *
     * <p>Calling any {@code withEmbeddedXYZ()} method multiple times results in <b>overriding</b> the previously
     * embedded resource each time.</p>
     *
     * @param <NewEmbeddedT>
     *         the type of the items in the list to embed
     * @param embeddedTypeAsNameOrigin
     *         the class type used to derive the name or relation for the embedded list. This parameter must be
     *         non-null.
     * @param resourcesToEmbed
     *         the list to be embedded
     * @return a new instance with the embedded list
     *
     * @throws IllegalArgumentException
     *         if {@code embeddedTypeAsNameOrigin} is null
     */
    public <NewEmbeddedT> HalResourceWrapper<ResourceT, NewEmbeddedT> withEmbeddedList(
            @NonNull Class<NewEmbeddedT> embeddedTypeAsNameOrigin,
            List<HalEmbeddedWrapper<NewEmbeddedT>> resourcesToEmbed) {
        Assert.notNull(embeddedTypeAsNameOrigin, valueNotAllowedToBeNull("Embedded type name"));
        String name = determineCollectionRelationName(embeddedTypeAsNameOrigin);
        return new HalResourceWrapper<>(this.resource, name, resourcesToEmbed, this.getLinks());
    }


    /**
     * Returns the embedded resource(s). The embedded resource is stored in form of a list and thus will return a list
     * even if a single resource was embedded.
     *
     * @return List with embedded resources
     */
    @JsonIgnore
    public Optional<List<HalEmbeddedWrapper<EmbeddedT>>> getEmbedded() {
        return Optional.ofNullable(embedded).map(Map.Entry::getValue).map(ArrayList::new);
    }

    /**
     * Returns the name of the embedded resource or list of resources.
     *
     * @return Name of the embedded resource
     */
    @JsonIgnore
    public Optional<String> getNameOfEmbedded() {
        return Optional.ofNullable(embedded).map(Map.Entry::getKey);
    }

    /**
     * Indicates whether the {@link HalResourceWrapper} has an embedded resource
     *
     * @return {@code true} if an embedded resource exists; {@code false} otherwise
     */
    @JsonIgnore
    public boolean hasEmbedded() {
        return embedded != null;
    }


    /**
     * Returns the embedded resource(s). The embedded resource is stored in form of a list and thus will return a
     * list even if a single resource was embedded.
     *
     * <p>
     * In contrast to {@link #getEmbedded()}, it is assumed, that the embedded resource(s) exist, otherwise an
     * exception is thrown.
     *
     * @return List with embedded resources
     *
     * @throws IllegalArgumentException
     *         if no embedded resource(s) exist
     */
    @JsonIgnore
    public List<HalEmbeddedWrapper<EmbeddedT>> getRequiredEmbedded() {
        return getEmbedded()
                .orElseThrow(() -> new IllegalStateException(requiredValueWasNonExisting("embedded")));
    }

    /**
     * Returns the name of the embedded resource or list of resources.
     *
     * <p>
     * In contrast to {@link #getNameOfEmbedded()}, it is assumed, that the embedded resource(s) exist, otherwise an
     * exception is thrown.
     *
     * @return Name of the embedded resource
     *
     * @throws IllegalArgumentException
     *         if no embedded resource(s) exist
     */
    @JsonIgnore
    public String getRequiredNameOfEmbedded() {
        return getNameOfEmbedded()
                .orElseThrow(() -> new IllegalStateException(requiredValueWasNonExisting("name of embedded")));
    }
}
