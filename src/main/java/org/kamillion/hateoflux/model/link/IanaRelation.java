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
 * @since 24.05.2024
 */

package org.kamillion.hateoflux.model.link;

import lombok.Getter;

/**
 * A subset of IANA relations. The enum focuses on the most important links for REST.
 *
 * @author Younes El Ouarti
 * @see <a href="https://www.iana.org/assignments/link-relations/link-relations.xhtml">Official IANA documentation</a>
 */
@Getter
public enum IanaRelation {
    // ---------- Document relationships ----------

    /**
     * Refers to a resource that is the subject of the link's context.
     */
    ABOUT("about"),

    /**
     * Refers to a substitute for this context.
     */
    ALTERNATE("alternate"),

    /**
     * Refers to the author of the context.
     */
    AUTHOR("author"),

    /**
     * Designates the preferred version of a resource.
     */
    CANONICAL("canonical"),

    /**
     * The target IRI points to a resource which represents the collection resource for the context IRI.
     */
    COLLECTION("collection"),

    /**
     * Refers to a table of contents.
     */
    CONTENTS("contents"),

    /**
     * Refers to a copyright notice for the context.
     */
    COPYRIGHT("copyright"),

    // ---------- Form and editing links ----------

    /**
     * The target IRI points to a resource where a submission form can be obtained.
     */
    CREATE_FORM("create-form"),

    /**
     * Refers to a resource that can be directly edited.
     */
    EDIT("edit"),

    /**
     * The target IRI points to a resource where a submission form for editing this resource can be obtained.
     */
    EDIT_FORM("edit-form"),

    // ---------- Navigation links ----------

    /**
     * An IRI that refers to the furthest preceding resource in a series of resources.
     */
    FIRST("first"),

    /**
     * The target IRI points to a resource that is a member of the collection represented by the context IRI.
     */
    ITEM("item"),

    /**
     * An IRI that refers to the furthest following resource in a series of resources.
     */
    LAST("last"),

    /**
     * Indicates that the link's context is a part of a series, and that the next in the series is the link target.
     */
    NEXT("next"),

    /**
     * Indicates that the link's context is a part of a series, and that the previous in the series is the link target.
     */
    PREV("prev"),

    /**
     * Identifies a related resource.
     */
    RELATED("related"),

    // ---------- Service links ----------

    /**
     * Refers to a search tool for finding resources.
     */
    SEARCH("search"),

    /**
     * Conveys an identifier for the link's context.
     */
    SELF("self"),

    /**
     * Indicates a URI that can be used to retrieve a service.
     */
    SERVICE("service"),

    // ---------- Versioning and metadata ----------

    /**
     * Points to a resource containing the version history for the context.
     */
    VERSION_HISTORY("version-history"),

    /**
     * Refers to a license associated with this context.
     */
    LICENSE("license"),

    /**
     * Refers to a parent document in a hierarchy of documents.
     */
    UP("up");

    /**
     * Name of the link relation as defined in the IANA registry.
     */
    private final String name;

    IanaRelation(final String name) {
        this.name = name;
    }

}

