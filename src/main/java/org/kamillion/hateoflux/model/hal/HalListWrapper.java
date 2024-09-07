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
 * @since 22.06.2024
 */

package org.kamillion.hateoflux.model.hal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.kamillion.hateoflux.model.link.Link;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static org.kamillion.hateoflux.utility.MessageTemplates.valueNotAllowedToBeEmpty;
import static org.kamillion.hateoflux.utility.MessageTemplates.valueNotAllowedToBeNull;


/**
 * Represents an immutable wrapper class for managing a collection of {@link HalEntityWrapper} instances in a
 * hypermedia-driven format, adhering to HAL standards. This class is designed as a container for multiple entity
 * wrappers, facilitating the representation of lists of entities and their associated embedded entities.
 * <p>
 * The {@link HalListWrapper} is a final class, not designed for extension. It maintains a collection of
 * {@link HalEntityWrapper} objects, each holding a primary entity and its associated embedded entities. This container
 * supports the structured representation of entity lists in hypermedia responses.
 * <p>
 * The class can be utilized by instantiating and populating it with {@link HalEntityWrapper} instances, accommodating
 * the collective serialization of entities. It can also be enhanced with paging information through the
 * {@link HalPageInfo} class, which allows the inclusion of pagination data in the hypermedia response to support
 * scalable data interaction.
 *
 * @param <EntityT>
 *         the type of the primary object wrapped within each {@link HalEntityWrapper}
 * @param <EmbeddedT>
 *         the type of the embedded entities related to each primary entity, contained within the
 *         {@link HalEntityWrapper}
 * @author Younes El Ouarti
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@JsonInclude(NON_NULL)
public final class HalListWrapper<EntityT, EmbeddedT>
        extends HalWrapper<HalListWrapper<EntityT, EmbeddedT>> {

    @JsonProperty("_embedded")
    private Map.Entry<String, List<HalEntityWrapper<EntityT, EmbeddedT>>> entityList;

    private HalPageInfo page;

    private HalListWrapper(String listName,
                           List<HalEntityWrapper<EntityT, EmbeddedT>> listToWrap) {
        super();
        this.entityList = new AbstractMap.SimpleImmutableEntry<>(listName, listToWrap);
    }

    private HalListWrapper(String listName,
                           List<HalEntityWrapper<EntityT, EmbeddedT>> listToWrap,
                           HalPageInfo page,
                           Iterable<Link> links) {
        super();
        this.entityList = new AbstractMap.SimpleImmutableEntry<>(listName, listToWrap);
        this.page = page;
        this.withLinks(links);
    }

    public static <EntityT, EmbeddedT> HalListWrapper<EntityT, EmbeddedT> wrap(
            @NonNull List<HalEntityWrapper<EntityT, EmbeddedT>> listToWrap) {
        Assert.notNull(listToWrap, valueNotAllowedToBeNull("List to embed"));
        Assert.notEmpty(listToWrap, valueNotAllowedToBeEmpty("List to embed"));
        String name = determineRelationNameForObject(listToWrap);
        return new HalListWrapper<>(name, listToWrap);
    }

    /**
     * Creates an empty {@link HalListWrapper}. This method is intended to be used when there is a need to
     * explicitly communicate an empty result set. A later addition of data to this list is not intended.
     *
     * @param listName
     *         the name of the list, used for identification in the response structure
     * @param <EntityT>
     *         the type of the primary object that would be wrapped in {@link HalEntityWrapper}
     * @param <EmbeddedT>
     *         the type of the embedded entities related to the primary object
     * @return an instance of {@link HalListWrapper} with no {@link HalEntityWrapper} elements
     */
    public static <EntityT, EmbeddedT> HalListWrapper<EntityT, EmbeddedT> empty(@NonNull String listName) {
        Assert.hasText(listName, valueNotAllowedToBeEmpty("List name"));
        return new HalListWrapper<>(listName, new ArrayList<>());
    }

    /**
     * Creates an empty {@link HalListWrapper}. This method is intended to be used when there is a need to
     * explicitly communicate an empty result set. A later addition of data to this list is not intended.
     *
     * @param listItemTypeAsNameOrigin
     *         the class from which the list name is derived (see also {@link Relation})
     * @param <EntityT>
     *         the type of the primary object that would be wrapped in {@link HalEntityWrapper}
     * @param <EmbeddedT>
     *         the type of the embedded entities related to the primary object
     * @return an instance of {@link HalListWrapper} with no {@link HalEntityWrapper} elements
     */
    public static <EntityT, EmbeddedT> HalListWrapper<EntityT, EmbeddedT> empty(@NonNull Class<?> listItemTypeAsNameOrigin) {
        Assert.notNull(listItemTypeAsNameOrigin, valueNotAllowedToBeNull("List item type name"));
        String name = determineCollectionRelationName(listItemTypeAsNameOrigin);
        return new HalListWrapper<>(name, new ArrayList<>());
    }

    /**
     * Adds pagination details to the {@link HalListWrapper}
     *
     * @param pageInfo
     *         paging information for the list
     * @return New {@link HalListWrapper} with added pagination information
     */
    public HalListWrapper<EntityT, EmbeddedT> withPageInfo(HalPageInfo pageInfo) {
        return new HalListWrapper<>(this.entityList.getKey(), this.entityList.getValue(),
                pageInfo, this.getLinks());
    }

    /**
     * Returns the held list of entities.
     *
     * @return the held list of entities.
     */
    @JsonIgnore
    public List<HalEntityWrapper<EntityT, EmbeddedT>> getEntityList() {
        return new ArrayList<>(entityList.getValue());
    }

    /**
     * Name of the list of entities.
     *
     * @return Name of the list of entities.
     */
    @JsonIgnore
    public String getNameOfEntityList() {
        return entityList.getKey();
    }
}
