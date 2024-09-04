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
 * @since 03.09.2024
 */

package org.kamillion.hateoflux.linkbuilder;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Younes El Ouarti
 */
@Data
public class QueryParameter {

    private String name = "";

    private List<String> listOfValues = new ArrayList<>();

    private boolean isCollection;

    private boolean isRenderedAsComposite;

    public String getValue() {
        return listOfValues.isEmpty() ? "" : listOfValues.get(0);
    }

    private QueryParameter(String name, List<String> listOfValues, boolean isCollection,
                           boolean isRenderedAsComposite) {
        this.name = name;
        this.listOfValues = listOfValues;
        this.isCollection = isCollection;
        this.isRenderedAsComposite = isRenderedAsComposite;
    }

    public QueryParameter of(String name, String value) {
        return new QueryParameter(name, List.of(value), false, false);
    }

    public QueryParameter of(String name, List<String> values, boolean isRenderedAsComposite) {
        return new QueryParameter(name, values, true, isRenderedAsComposite);
    }

    public static QueryParameterBuilder builder() {
        return new QueryParameterBuilder();
    }

    public static class QueryParameterBuilder {
        private String name = "";
        private List<String> values = new ArrayList<>();
        private boolean isCollection = false;
        private boolean isSpecifiedAsComposite = false;

        public QueryParameterBuilder name(String name) {
            this.name = name;
            return this;
        }

        public QueryParameterBuilder value(String value) {
            this.values = List.of(value);
            this.isCollection = false;
            this.isSpecifiedAsComposite = false;
            return this;
        }

        public QueryParameterBuilder value(Object value) {
            return value(String.valueOf(value));
        }

        public QueryParameterBuilder listOfValues(Collection<?> values, boolean isRenderedAsComposite) {
            this.values = values == null ? List.of() : values.stream().map(String::valueOf).toList();
            this.isCollection = true;
            this.isSpecifiedAsComposite = isRenderedAsComposite;
            return this;
        }

        public QueryParameter build() {
            return new QueryParameter(name, values, isCollection, isSpecifiedAsComposite);
        }
    }
}
