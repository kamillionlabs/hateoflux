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
import java.util.Collection;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

/**
 * @author Younes El Ouarti
 */
@Getter
@EqualsAndHashCode(callSuper = true)
@JsonInclude(NON_NULL)
public final class HalCollectionWrapper<ContentT, EmbeddedT>
        extends HalWrapper<HalCollectionWrapper<ContentT, EmbeddedT>> {

    @JsonProperty("_embedded")
    private Map.Entry<String, Collection<HalEntityWrapper<ContentT, EmbeddedT>>> embeddedCollection;

    private HalPageInfo page;

    private HalCollectionWrapper(String collectionName,
                                 Collection<HalEntityWrapper<ContentT, EmbeddedT>> collectionToWrap) {
        super();
        this.embeddedCollection = new AbstractMap.SimpleImmutableEntry<>(collectionName, collectionToWrap);
    }

    private HalCollectionWrapper(String collectionName,
                                 Collection<HalEntityWrapper<ContentT, EmbeddedT>> collectionToWrap, HalPageInfo page
            , Iterable<Link> links) {
        super();
        this.embeddedCollection = new AbstractMap.SimpleImmutableEntry<>(collectionName, collectionToWrap);
        this.page = page;
        this.withLinks(links);
    }

    public static <ContentT, EmbeddedT> HalCollectionWrapper<ContentT, EmbeddedT> wrap(@NonNull Collection<HalEntityWrapper<ContentT, EmbeddedT>> collectionToWrap) {
        Assert.notNull(collectionToWrap, "Collection is not allowed to be null");
        Assert.notEmpty(collectionToWrap, "Collection is not allowed to be a empty");
        String name = determineRelationNameForObject(collectionToWrap);
        return new HalCollectionWrapper<>(name, collectionToWrap);
    }

    public static <T, S> HalCollectionWrapper<T, S> empty(@NonNull String collectionName) {
        Assert.hasText(collectionName, "Collection name is not allowed to be empty");
        return new HalCollectionWrapper<>(collectionName, new ArrayList<>());
    }

    public static <T, S> HalCollectionWrapper<T, S> empty(@NonNull Class<?> collectionItemTypeAsNameOrigin) {
        Assert.notNull(collectionItemTypeAsNameOrigin, "Collection item type name is not allowed to be null");
        String name = determineCollectionRelationName(collectionItemTypeAsNameOrigin);
        return new HalCollectionWrapper<>(name, new ArrayList<>());
    }

    public HalCollectionWrapper<ContentT, EmbeddedT> withPageInfo(HalPageInfo pageInfo) {
        return new HalCollectionWrapper<>(this.embeddedCollection.getKey(), this.embeddedCollection.getValue(),
                pageInfo, this.getLinks());
    }

    @JsonIgnore
    public Collection<HalEntityWrapper<ContentT, EmbeddedT>> getEmbeddedCollection() {
        return embeddedCollection.getValue();
    }

    @JsonIgnore
    public String getNameOfEmbeddedCollection() {
        return embeddedCollection.getKey();
    }
}
