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
 * @since 29.06.2024
 */

package de.kamillionlabs.hateoflux.model.hal.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.kamillionlabs.hateoflux.dummy.TestDataGenerator;
import de.kamillionlabs.hateoflux.dummy.controller.AuthorController;
import de.kamillionlabs.hateoflux.dummy.model.Author;
import de.kamillionlabs.hateoflux.dummy.model.Book;
import de.kamillionlabs.hateoflux.model.hal.HalEmbeddedWrapper;
import de.kamillionlabs.hateoflux.model.hal.HalListWrapper;
import de.kamillionlabs.hateoflux.model.hal.HalPageInfo;
import de.kamillionlabs.hateoflux.model.hal.HalResourceWrapper;
import de.kamillionlabs.hateoflux.model.link.IanaRelation;
import de.kamillionlabs.hateoflux.model.link.Link;
import de.kamillionlabs.hateoflux.utility.SortCriteria;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.List;

import static de.kamillionlabs.hateoflux.linkbuilder.SpringControllerLinkBuilder.linkTo;
import static de.kamillionlabs.hateoflux.utility.SortDirection.ASCENDING;
import static org.skyscreamer.jsonassert.JSONCompareMode.NON_EXTENSIBLE;

/**
 * @author Younes El Ouarti
 */
public class ManualSerializationTest {

    private final ObjectMapper mapper = new ObjectMapper();

    private final TestDataGenerator testData = new TestDataGenerator();

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
    public void givenHalResource_whenSerialized_thenNoErrors() throws Exception {
        //GIVEN
        HalResourceWrapper<Book, Author> halResource = HalResourceWrapper.wrap(germanBedtimeStories)
                .withLinks(
                        Link.of("/book/123").withRel(IanaRelation.SELF),
                        Link.of("/author/1").withRel("author"))
                .withEmbeddedResource(HalEmbeddedWrapper.wrap(author)
                        .withLinks(
                                Link.linkAsSelfOf("/author/").slash("1"),
                                linkTo(AuthorController.class, c -> c.getBooks(1)).withRel("books")
                        )
                );

        //WHEN
        String actualJson = mapper.writeValueAsString(halResource);

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
    public void givenHalResourceWithEmbeddedCollection_whenSerialized_thenNoErrors() throws Exception {
        //GIVEN
        var halResource = HalResourceWrapper.wrap(author)
                .withLinks(Link.linkAsSelfOf("/author/1"))
                .withNonEmptyEmbeddedList(List.of(
                        HalEmbeddedWrapper.wrap(germanBedtimeStories)
                                .withLinks(Link.linkAsSelfOf("/book/123")),
                        HalEmbeddedWrapper.wrap(cookBookForManlyMen)
                                .withLinks(Link.linkAsSelfOf("/book/234")))
                );


        //WHEN
        String actualJson = mapper.writeValueAsString(halResource);

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

    @Test
    public void givenHalListWrapperWithNoPaging_whenSerialized_thenNoErrors() throws Exception {
        //GIVEN
        var halListWrapper = HalListWrapper.wrap(List.of(
                        HalResourceWrapper.wrap(germanBedtimeStories)
                                .withLinks(Link.linkAsSelfOf("/book/123")),
                        HalResourceWrapper.wrap(cookBookForManlyMen)
                                .withLinks(Link.linkAsSelfOf("/book/234"))))
                .withLinks(Link.linkAsSelfOf("/author/1/books"));

        //WHEN
        String actualJson = mapper.writeValueAsString(halListWrapper);

        //THEN
        JSONAssert.assertEquals("""
                {
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
                      "href": "/author/1/books"
                    }
                  }
                }
                """, actualJson, NON_EXTENSIBLE);
    }

    @Test
    public void givenHalListWrapperWithPaging_whenSerialized_thenNoErrors() throws Exception {
        //GIVEN
        HalPageInfo halPageInfo = HalPageInfo.of(1, 3L, 3, 0);
        HalListWrapper<Book, Void> halListWrapper = HalListWrapper.wrap(
                        List.of(HalResourceWrapper.wrap(germanBedtimeStories)
                                .withLinks(Link.linkAsSelfOf("/book/123"))))
                .withLinks(Link.of("/author/1/books")
                        .deriveNavigationLinks(halPageInfo, SortCriteria.by("author", ASCENDING)))
                .withPageInfo(halPageInfo);

        //WHEN
        String actualJson = mapper.writeValueAsString(halListWrapper);

        //THEN
        JSONAssert.assertEquals("""
                {
                    "page": {
                      "size": 1,
                      "totalElements": 3,
                      "totalPages": 3,
                      "number": 0
                    },
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
                        }
                      ]
                    },
                    "_links": {
                      "next": {
                        "href": "/author/1/books?page=1&size=1&sort=author,asc"
                      },
                      "self": {
                        "href": "/author/1/books?page=0&size=1&sort=author,asc"
                      },
                      "last": {
                        "href": "/author/1/books?page=2&size=1&sort=author,asc"
                      }
                    }
                  }
                """, actualJson, NON_EXTENSIBLE);
    }

    @Test
    public void givenHalListWrapperWithEmbeddingListsAndEmptyOnes_whenSerialized_thenNoErrors() throws Exception {
        // GIVEN
        Author brianGoetzAuthor = testData.getAuthorByName("Brian Goetz");
        List<Book> brianGoetz = testData.getAllBooksByAuthorName("Brian Goetz");
        Author joshuaBlockAuthor = testData.getAuthorByName("Joshua Bloch");

        HalEmbeddedWrapper<Book> emptyEmbedded = HalEmbeddedWrapper.empty();
        List<HalResourceWrapper<Author, Book>> halResourceWrappers = List.of(
                HalResourceWrapper.wrap(brianGoetzAuthor)
                        .withNonEmptyEmbeddedList(
                                brianGoetz.stream()
                                        .map(resourceToWrap ->
                                                HalEmbeddedWrapper.wrap(resourceToWrap)
                                                        .withLinks(Link.linkAsSelfOf("/book/" + resourceToWrap.getIsbn())))
                                        .toList()
                        ).withLinks(Link.linkAsSelfOf("/author/" + brianGoetzAuthor.getName())),
                HalResourceWrapper.wrap(joshuaBlockAuthor)
                        .withEmbeddedList(Book.class, List.of())
                        .withLinks(Link.linkAsSelfOf("/author/" + joshuaBlockAuthor.getName()))
        );
        HalListWrapper<Author, Book> halListWrapper = HalListWrapper.wrap(halResourceWrappers)
                .withLinks(Link.linkAsSelfOf("/authors?includeBooks=true"));

        // WHEN
        String actualJson = mapper.writeValueAsString(halListWrapper);

        // THEN
        JSONAssert.assertEquals("""
                {
                     "_embedded": {
                       "authors": [
                         {
                           "name": "Brian Goetz",
                           "birthDate": "1969-05-22",
                           "mainGenre": "Programming Languages",
                           "_embedded": {
                             "customBooks": [
                               {
                                 "title": "Java Concurrency in Practice",
                                 "author": "Brian Goetz",
                                 "isbn": "978-0321349606",
                                 "publishedDate": "2006-05-19",
                                 "_links": {
                                    "self": {
                                      "href": "/book/978-0321349606"
                                     }
                                 }
                               },
                               {
                                 "title": "Java Puzzlers",
                                 "author": "Brian Goetz",
                                 "isbn": "978-0321336781",
                                 "publishedDate": "2005-07-24",
                                 "_links": {
                                    "self": {
                                      "href": "/book/978-0321336781"
                                     }
                                 }
                               }
                             ]
                           },
                           "_links": {
                             "self": {
                               "href": "/author/Brian Goetz"
                             }
                           }
                         },
                         {
                           "name": "Joshua Bloch",
                           "birthDate": "1961-08-28",
                           "mainGenre": "Programming",
                           "_embedded": {
                             "customBooks": []
                           },
                           "_links": {
                             "self": {
                               "href": "/author/Joshua Bloch"
                             }
                           }
                         }
                       ]
                     },
                     "_links": {
                       "self": {
                         "href": "/authors?includeBooks=true"
                       }
                     }
                   }
                """, actualJson, NON_EXTENSIBLE);

    }


}
