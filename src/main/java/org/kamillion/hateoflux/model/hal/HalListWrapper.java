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

/**
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

    public static <EntityT, EmbeddedT> HalListWrapper<EntityT, EmbeddedT> wrap(@NonNull List<HalEntityWrapper<EntityT
            , EmbeddedT>> listToWrap) {
        Assert.notNull(listToWrap, "List is not allowed to be null");
        Assert.notEmpty(listToWrap, "List is not allowed to be a empty");
        String name = determineRelationNameForObject(listToWrap);
        return new HalListWrapper<>(name, listToWrap);
    }

    public static <EntityT, EmbeddedT> HalListWrapper<EntityT, EmbeddedT> empty(@NonNull String listName) {
        Assert.hasText(listName, "List name is not allowed to be empty");
        return new HalListWrapper<>(listName, new ArrayList<>());
    }

    public static <EntityT, EmbeddedT> HalListWrapper<EntityT, EmbeddedT> empty(@NonNull Class<?> listItemTypeAsNameOrigin) {
        Assert.notNull(listItemTypeAsNameOrigin, "List item type name is not allowed to be null");
        String name = determineCollectionRelationName(listItemTypeAsNameOrigin);
        return new HalListWrapper<>(name, new ArrayList<>());
    }

    public HalListWrapper<EntityT, EmbeddedT> withPageInfo(HalPageInfo pageInfo) {
        return new HalListWrapper<>(this.entityList.getKey(), this.entityList.getValue(),
                pageInfo, this.getLinks());
    }

    @JsonIgnore
    public List<HalEntityWrapper<EntityT, EmbeddedT>> getEntityList() {
        return entityList.getValue();
    }

    @JsonIgnore
    public String getNameOfEmbeddedList() {
        return entityList.getKey();
    }
}
