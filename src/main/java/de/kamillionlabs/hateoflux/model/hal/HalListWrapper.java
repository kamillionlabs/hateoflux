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
 * @since 22.06.2024
 */

package de.kamillionlabs.hateoflux.model.hal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.kamillionlabs.hateoflux.model.link.Link;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static de.kamillionlabs.hateoflux.utility.ValidationMessageTemplates.valueNotAllowedToBeEmpty;
import static de.kamillionlabs.hateoflux.utility.ValidationMessageTemplates.valueNotAllowedToBeNull;


/**
 * Represents an immutable wrapper class for managing a collection of {@link HalResourceWrapper} instances in a
 * hypermedia-driven format, adhering to HAL standards. This class is designed as a container for multiple resource
 * wrappers, facilitating the representation of lists of resources and their associated embedded resources.
 * <p>
 * The {@link HalListWrapper} is a final class, not designed for extension. It maintains a collection of
 * {@link HalResourceWrapper} objects, each holding a primary resource and its associated embedded resources. This
 * container
 * supports the structured representation of resource lists in hypermedia responses.
 * <p>
 * The class can be utilized by instantiating and populating it with {@link HalResourceWrapper} instances,
 * accommodating
 * the collective serialization of resources. It can also be enhanced with paging information through the
 * {@link HalPageInfo} class, which allows the inclusion of pagination data in the hypermedia response to support
 * scalable data interaction.
 *
 * @param <ResourceT>
 *         the type of the primary object wrapped within each {@link HalResourceWrapper}
 * @param <EmbeddedT>
 *         the type of the embedded resources related to each primary resource, contained within the
 *         {@link HalResourceWrapper}
 * @author Younes El Ouarti
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@JsonInclude(NON_NULL)
public final class HalListWrapper<ResourceT, EmbeddedT>
        extends HalWrapper<HalListWrapper<ResourceT, EmbeddedT>> {

    @JsonProperty("_embedded")
    private Map.Entry<String, List<HalResourceWrapper<ResourceT, EmbeddedT>>> resourceList;

    private HalPageInfo page;

    private HalListWrapper(String listName,
                           List<HalResourceWrapper<ResourceT, EmbeddedT>> listToWrap) {
        super();
        this.resourceList = new AbstractMap.SimpleImmutableEntry<>(listName, listToWrap);
    }

    private HalListWrapper(String listName,
                           List<HalResourceWrapper<ResourceT, EmbeddedT>> listToWrap,
                           HalPageInfo page,
                           Iterable<Link> links) {
        super();
        this.resourceList = new AbstractMap.SimpleImmutableEntry<>(listName, listToWrap);
        this.page = page;
        this.withLinks(links);
    }

    /**
     * Wraps a given list of resources, where each resource needs to be wrapped in a {@link HalResourceWrapper}.
     * This method ensures that the list of resources conforms to HAL standards. Each {@link HalResourceWrapper}
     * contains a main resource and an optional embedded resource.
     *
     * @param <ResourceT>
     *         the type of the resource to be wrapped
     * @param <EmbeddedT>
     *         the type of the embedded resource
     * @param listToWrap
     *         the list of resources to be wrapped
     * @return a new instance containing the wrapped resources
     *
     * @see HalResourceWrapper#wrap(Object)
     */

    public static <ResourceT, EmbeddedT> HalListWrapper<ResourceT, EmbeddedT> wrap(
            @NonNull List<HalResourceWrapper<ResourceT, EmbeddedT>> listToWrap) {
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
     * @param <ResourceT>
     *         the type of the primary object that would be wrapped in {@link HalResourceWrapper}
     * @param <EmbeddedT>
     *         the type of the embedded resources related to the primary object
     * @return an instance of {@link HalListWrapper} with no {@link HalResourceWrapper} elements
     */
    public static <ResourceT, EmbeddedT> HalListWrapper<ResourceT, EmbeddedT> empty(@NonNull String listName) {
        Assert.hasText(listName, valueNotAllowedToBeEmpty("List name"));
        return new HalListWrapper<>(listName, new ArrayList<>());
    }

    /**
     * Creates an empty {@link HalListWrapper}. This method is intended to be used when there is a need to
     * explicitly communicate an empty result set. A later addition of data to this list is not intended.
     *
     * @param listItemTypeAsNameOrigin
     *         the class from which the list name is derived (see also {@link Relation})
     * @param <ResourceT>
     *         the type of the primary object that would be wrapped in {@link HalResourceWrapper}
     * @param <EmbeddedT>
     *         the type of the embedded resources related to the primary object
     * @return an instance of {@link HalListWrapper} with no {@link HalResourceWrapper} elements
     */
    public static <ResourceT, EmbeddedT> HalListWrapper<ResourceT, EmbeddedT> empty(@NonNull Class<?> listItemTypeAsNameOrigin) {
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
    public HalListWrapper<ResourceT, EmbeddedT> withPageInfo(HalPageInfo pageInfo) {
        return new HalListWrapper<>(this.resourceList.getKey(), this.resourceList.getValue(),
                pageInfo, this.getLinks());
    }

    /**
     * Returns the held list of resources.
     *
     * @return the held list of resources.
     */
    @JsonIgnore
    public List<HalResourceWrapper<ResourceT, EmbeddedT>> getResourceList() {
        return new ArrayList<>(resourceList.getValue());
    }

    /**
     * Name of the list of resources.
     *
     * @return Name of the list of resources.
     */
    @JsonIgnore
    public String getNameOfResourceList() {
        return resourceList.getKey();
    }
}
