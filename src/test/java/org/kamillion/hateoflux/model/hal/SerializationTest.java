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
 * @since 29.06.2024
 */

package org.kamillion.hateoflux.model.hal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.kamillion.hateoflux.dummy.controller.AuthorController;
import org.kamillion.hateoflux.dummy.model.Author;
import org.kamillion.hateoflux.dummy.model.Book;
import org.kamillion.hateoflux.model.link.IanaRelation;
import org.kamillion.hateoflux.model.link.Link;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.List;

import static org.kamillion.hateoflux.linkbuilder.SpringControllerLinkBuilder.linkTo;
import static org.skyscreamer.jsonassert.JSONCompareMode.NON_EXTENSIBLE;

/**
 * @author Younes El Ouarti
 */
public class SerializationTest {

    private final ObjectMapper mapper = new ObjectMapper();

    private final Book germanBedtimeStories = Book.builder()
            .title("The German Bedtime Stories")
            .author("Herbert Almann")
            .isbn("123-4567890123")
            .publishedDate("1889-05-17")
            .build();

    private final Book cookBookForManlyMen = Book.builder()
            .title("The Only Cook Book a Manly Men Needs")
            .author("Herbert Almann")
            .isbn("234-5678901234")
            .publishedDate("1889-05-17")
            .build();

    private final Author author = Author.builder()
            .name("Herbert Almann")
            .birthDate("1851-09-13")
            .mainGenre("Bedtime Stories")
            .build();


    @Test
    public void givenHalEntity_whenSerialized_thenNoErrors() throws Exception {
        //GIVEN
        var halEntity = HalEntityWrapper.wrap(germanBedtimeStories)
                .withLinks(
                        Link.of("/book/123").withRel(IanaRelation.SELF),
                        Link.of("/author/1").withRel("author"))
                .withEmbeddedEntity(HalEntityWrapper.wrap(author)
                        .withLinks(
                                Link.linkAsSelfOf("/author/").slash("1"),
                                linkTo(AuthorController.class, c -> c.getBooks(1)).withRel("books")
                        )
                );

        //WHEN
        String actualJson = mapper.writeValueAsString(halEntity);

        //THEN
        JSONAssert.assertEquals("""
                {
                  "title": "The German Bedtime Stories",
                  "author": "Herbert Almann",
                  "isbn": "123-4567890123",
                  "publishedDate": "1889-05-17",
                  "_embedded": {
                    "author": {
                      "name": "Herbert Almann",
                      "birthDate": "1851-09-13",
                      "mainGenre": "Bedtime Stories",
                      "_links": {
                        "books": {
                          "href": "/author/1/books"
                        },
                        "self": {
                          "href": "/author/1"
                        }
                      }
                    }
                  },
                  "_links": {
                    "self": {
                      "href": "/book/123"
                    },
                    "author": {
                      "href": "/author/1"
                    }
                  }
                }
                """, actualJson, NON_EXTENSIBLE);
    }

    @Test
    public void givenHalEntityWithEmbeddedCollection_whenSerialized_thenNoErrors() throws Exception {
        //GIVEN
        var halEntity = HalEntityWrapper.wrap(author)
                .withLinks(Link.linkAsSelfOf("/author/1"))
                .withNonEmptyEmbeddedCollection(List.of(
                        HalEntityWrapper.wrap(germanBedtimeStories)
                                .withLinks(Link.linkAsSelfOf("/book/123")),
                        HalEntityWrapper.wrap(cookBookForManlyMen)
                                .withLinks(Link.linkAsSelfOf("/book/234")))
                );

        //WHEN
        String actualJson = mapper.writeValueAsString(halEntity);

        //THEN
        JSONAssert.assertEquals("""
                {
                  "name": "Herbert Almann",
                  "birthDate": "1851-09-13",
                  "mainGenre": "Bedtime Stories",
                  "_embedded": {
                    "customBooks": [
                      {
                        "title": "The German Bedtime Stories",
                        "author": "Herbert Almann",
                        "isbn": "123-4567890123",
                        "publishedDate": "1889-05-17",
                        "_links": {
                          "self": {
                            "href": "/book/123"
                          }
                        }
                      },
                      {
                        "title": "The Only Cook Book a Manly Men Needs",
                        "author": "Herbert Almann",
                        "isbn": "234-5678901234",
                        "publishedDate": "1889-05-17",
                        "_links": {
                          "self": {
                            "href": "/book/234"
                          }
                        }
                      }
                    ]
                  },
                  "_links": {
                    "self": {
                      "href": "/author/1"
                    }
                  }
                }
                """, actualJson, NON_EXTENSIBLE);
    }

    //TODO add tests for HalCollectionWrapper
}
