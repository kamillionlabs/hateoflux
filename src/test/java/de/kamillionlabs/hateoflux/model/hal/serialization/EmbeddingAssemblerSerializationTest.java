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
 * @since 14.11.2024
 */
/*
 * Copyright (c) 2024 kamillion contributors
 *
 * This work is licensed under the GNU General Public License (GPL).
 *
 * @since 14.10.2024
 */

package de.kamillionlabs.hateoflux.model.hal.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.kamillionlabs.hateoflux.assembler.EmbeddingHalWrapperAssembler;
import de.kamillionlabs.hateoflux.dummy.TestDataGenerator;
import de.kamillionlabs.hateoflux.dummy.TestDataGenerator.AuthorName;
import de.kamillionlabs.hateoflux.dummy.TestDataGenerator.BookTitle;
import de.kamillionlabs.hateoflux.dummy.model.Author;
import de.kamillionlabs.hateoflux.dummy.model.Book;
import de.kamillionlabs.hateoflux.model.hal.HalListWrapper;
import de.kamillionlabs.hateoflux.model.hal.HalResourceWrapper;
import de.kamillionlabs.hateoflux.model.link.Link;
import de.kamillionlabs.hateoflux.utility.SortCriteria;
import de.kamillionlabs.hateoflux.utility.pair.MultiRightPairFlux;
import de.kamillionlabs.hateoflux.utility.pair.MultiRightPairList;
import de.kamillionlabs.hateoflux.utility.pair.PairFlux;
import de.kamillionlabs.hateoflux.utility.pair.PairList;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.skyscreamer.jsonassert.JSONCompareMode.NON_EXTENSIBLE;

/**
 * @author Younes El Ouarti
 */
public class EmbeddingAssemblerSerializationTest {

    // Implementation for testing purposes -----------------------------------------------------------------------------
    static class AssemblerUnderTest implements EmbeddingHalWrapperAssembler<Author, Book> {

        @Override
        public Class<Book> getEmbeddedTClass() {
            return Book.class;
        }

        @Override
        public Class<Author> getResourceTClass() {
            return Author.class;
        }

        @Override
        public Link buildSelfLinkForResource(Author resourceToWrap, ServerWebExchange exchange) {
            return Link.of("resource/self/link");
        }

        @Override
        public List<Link> buildOtherLinksForResource(Author resourceToWrap, ServerWebExchange exchange) {
            return List.of(Link.of("resource/other/link")
                    .withRel("other"));
        }

        @Override
        public Link buildSelfLinkForResourceList(ServerWebExchange exchange) {
            return Link.of("resource-list/self/link");

        }

        @Override
        public List<Link> buildOtherLinksForResourceList(ServerWebExchange exchange) {
            return List.of(Link.of("resource-list/other/link")
                    .withRel("other"));
        }

        @Override
        public Link buildSelfLinkForEmbedded(Book embedded, ServerWebExchange exchange) {
            return Link.of("embedded/self/link");
        }

        @Override
        public List<Link> buildOtherLinksForEmbedded(Book embedded, ServerWebExchange exchange) {
            return List.of(Link.of("embedded/other/link")
                    .withRel("other"));
        }
    }
    // -----------------------------------------------------------------------------------------------------------------

    private final AssemblerUnderTest assemblerUnderTest = new AssemblerUnderTest();

    private final ObjectMapper mapper = new ObjectMapper();

    private final TestDataGenerator testData = new TestDataGenerator();


    @Test
    public void givenSingleResourceWithSingleEmbedded_whenSerialized_thenNoErrors() throws Exception {
        //GIVEN
        var resource = Mono.just(testData.getAuthorByName(AuthorName.JOSHUA_BLOCH));
        var embedded = Mono.just(testData.getBookByTitle(BookTitle.EFFECTIVE_JAVA));

        //WHEN
        HalResourceWrapper<Author, Book> actual =
                assemblerUnderTest.wrapInResourceWrapper(resource, embedded, null).block();

        //THEN
        String actualJson = mapper.writeValueAsString(actual);

        JSONAssert.assertEquals("""
                {
                  "name": "Joshua Bloch",
                  "birthDate": "1961-08-28",
                  "mainGenre": "Programming",
                  "_embedded": {
                    "customBook": {
                      "title": "Effective Java",
                      "author": "Joshua Bloch",
                      "isbn": "978-0134685991",
                      "publishedDate": "2018-01-06",
                      "_links": {
                        "other": {
                          "href": "embedded/other/link"
                        },
                        "self": {
                          "href": "embedded/self/link"
                        }
                      }
                    }
                  },
                  "_links": {
                    "other": {
                      "href": "resource/other/link"
                    },
                    "self": {
                      "href": "resource/self/link"
                    }
                  }
                }
                """, actualJson, NON_EXTENSIBLE);

    }

    @Test
    public void givenSingleResourceWithSingleEmbeddedInList_whenSerialized_thenNoErrors() throws Exception {
        //GIVEN
        var resource = Mono.just(testData.getAuthorByName(AuthorName.JOSHUA_BLOCH));
        var embedded = Flux.just(testData.getBookByTitle(BookTitle.EFFECTIVE_JAVA));

        //WHEN
        HalResourceWrapper<Author, Book> actual =
                assemblerUnderTest.wrapInResourceWrapper(resource, embedded, null).block();

        //THEN
        String actualJson = mapper.writeValueAsString(actual);


        JSONAssert.assertEquals("""
                {
                  "name": "Joshua Bloch",
                  "birthDate": "1961-08-28",
                  "mainGenre": "Programming",
                  "_embedded": {
                    "customBooks": [
                      {
                        "title": "Effective Java",
                        "author": "Joshua Bloch",
                        "isbn": "978-0134685991",
                        "publishedDate": "2018-01-06",
                        "_links": {
                          "other": {
                            "href": "embedded/other/link"
                          },
                          "self": {
                            "href": "embedded/self/link"
                          }
                        }
                      }
                    ]
                  },
                  "_links": {
                    "other": {
                      "href": "resource/other/link"
                    },
                    "self": {
                      "href": "resource/self/link"
                    }
                  }
                }
                """, actualJson, NON_EXTENSIBLE);
    }

    @Test
    public void givenSingleResourceWithListOfMultipleEmbedded_whenSerialized_thenNoErrors() throws Exception {
        //GIVEN
        var resource = Mono.just(testData.getAuthorByName(AuthorName.BRIAN_GOETZ));
        var embedded = Flux.fromIterable(testData.getAllBooksByAuthorName(AuthorName.BRIAN_GOETZ));

        //WHEN
        HalResourceWrapper<Author, Book> actual =
                assemblerUnderTest.wrapInResourceWrapper(resource, embedded, null).block();

        //THEN
        String actualJson = mapper.writeValueAsString(actual);
        JSONAssert.assertEquals("""
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
                           "other": {
                             "href": "embedded/other/link"
                           },
                           "self": {
                             "href": "embedded/self/link"
                           }
                         }
                       },
                       {
                         "title": "Java Puzzlers",
                         "author": "Brian Goetz",
                         "isbn": "978-0321336781",
                         "publishedDate": "2005-07-24",
                         "_links": {
                           "other": {
                             "href": "embedded/other/link"
                           },
                           "self": {
                             "href": "embedded/self/link"
                           }
                         }
                       }
                     ]
                   },
                   "_links": {
                     "other": {
                       "href": "resource/other/link"
                     },
                     "self": {
                       "href": "resource/self/link"
                     }
                   }
                 }
                """, actualJson, NON_EXTENSIBLE);
    }

    @Test
    public void givenSingleResourceWithEmptyFluxEmbedded_whenSerialized_thenNoErrors() throws Exception {
        //GIVEN
        var resource = Mono.just(testData.getAuthorByName(AuthorName.BRIAN_GOETZ));
        var embedded = Flux.<Book>empty();

        //WHEN
        HalResourceWrapper<Author, Book> actual =
                assemblerUnderTest.wrapInResourceWrapper(resource, embedded, null).block();

        //THEN
        String actualJson = mapper.writeValueAsString(actual);
        JSONAssert.assertEquals("""
                {
                   "name": "Brian Goetz",
                   "birthDate": "1969-05-22",
                   "mainGenre": "Programming Languages",
                   "_embedded": {
                     "customBooks": []
                   },
                   "_links": {
                     "other": {
                       "href": "resource/other/link"
                     },
                     "self": {
                       "href": "resource/self/link"
                     }
                   }
                 }
                """, actualJson, NON_EXTENSIBLE);
    }

    @Test
    public void givenSingleResourceWithEmptyMonoEmbedded_whenSerialized_thenNoErrors() throws Exception {
        //GIVEN
        Mono<Author> emptyResource = Mono.just(testData.getAuthorByName(AuthorName.ROBERT_MARTIN));
        Mono<Book> emptyEmbedded = Mono.empty();


        //WHEN
        HalResourceWrapper<Author, Book> actual = assemblerUnderTest.wrapInResourceWrapper(emptyResource,
                emptyEmbedded, null).block();

        //THEN
        String actualJson = mapper.writeValueAsString(actual);
        JSONAssert.assertEquals("""
                {
                   "name": "Robert C. Martin",
                   "birthDate": "1952-12-05",
                   "mainGenre": "Software Engineering",
                   "_links": {
                     "other": {
                       "href": "resource/other/link"
                     },
                     "self": {
                       "href": "resource/self/link"
                     }
                   }
                 }
                """, actualJson, NON_EXTENSIBLE);
    }

    @Test
    public void givenListOfResourceWithSingleEmbedded_whenSerialized_thenNoErrors() throws Exception {
        //GIVEN
        Author resource0 = testData.getAuthorByName(AuthorName.JOSHUA_BLOCH);
        Book embedded0 = testData.getAllBooksByAuthorName(AuthorName.JOSHUA_BLOCH).get(0);
        Author resource1 = testData.getAuthorByName(AuthorName.ERICH_GAMMA);
        Book embedded1 = testData.getAllBooksByAuthorName(AuthorName.ERICH_GAMMA).get(0);

        PairFlux<Author, Book> resourceList = PairFlux.fromIterable(PairList.of(resource0, embedded0, resource1,
                embedded1));

        //WHEN
        HalListWrapper<Author, Book> actual = assemblerUnderTest.wrapInListWrapper(resourceList, null)
                .block();

        //THEN
        String actualJson = mapper.writeValueAsString(actual);
        JSONAssert.assertEquals("""
                 {
                  "_embedded": {
                    "authors": [
                      {
                        "name": "Joshua Bloch",
                        "birthDate": "1961-08-28",
                        "mainGenre": "Programming",
                        "_embedded": {
                          "customBook": {
                            "title": "Effective Java",
                            "author": "Joshua Bloch",
                            "isbn": "978-0134685991",
                            "publishedDate": "2018-01-06",
                            "_links": {
                              "other": {
                                "href": "embedded/other/link"
                              },
                              "self": {
                                "href": "embedded/self/link"
                              }
                            }
                          }
                        },
                        "_links": {
                          "other": {
                            "href": "resource/other/link"
                          },
                          "self": {
                            "href": "resource/self/link"
                          }
                        }
                      },
                      {
                        "name": "Erich Gamma",
                        "birthDate": "1961-03-13",
                        "mainGenre": "Software Architecture",
                        "_embedded": {
                          "customBook": {
                            "title": "Design Patterns: Elements of Reusable Object-Oriented Software",
                            "author": "Erich Gamma",
                            "isbn": "978-0201633610",
                            "publishedDate": "1994-10-31",
                            "_links": {
                              "other": {
                                "href": "embedded/other/link"
                              },
                              "self": {
                                "href": "embedded/self/link"
                              }
                            }
                          }
                        },
                        "_links": {
                          "other": {
                            "href": "resource/other/link"
                          },
                          "self": {
                            "href": "resource/self/link"
                          }
                        }
                      }
                    ]
                  },
                  "_links": {
                    "self": {
                      "href": "resource-list/self/link"
                    },
                    "other": {
                      "href": "resource-list/other/link"
                    }
                  }
                }
                """, actualJson, NON_EXTENSIBLE);
    }

    @Test
    public void givenListOfResourceWithSingleEmbeddedInList_whenSerialized_thenNoErrors() throws Exception {
        //GIVEN
        Author resource0 = testData.getAuthorByName(AuthorName.JOSHUA_BLOCH);
        Book embedded0 = testData.getAllBooksByAuthorName(AuthorName.JOSHUA_BLOCH).get(0);
        Author resource1 = testData.getAuthorByName(AuthorName.ERICH_GAMMA);
        Book embedded1 = testData.getAllBooksByAuthorName(AuthorName.ERICH_GAMMA).get(0);

        MultiRightPairList<Author, Book> resourcesToWrap = MultiRightPairList.of(
                resource0, embedded0,
                resource1, embedded1);

        //WHEN
        HalListWrapper<Author, Book> actual =
                assemblerUnderTest.wrapInListWrapper(MultiRightPairFlux.fromIterable(resourcesToWrap), null).block();

        //THEN
        String actualJson = mapper.writeValueAsString(actual);
        JSONAssert.assertEquals("""
                {
                  "_embedded": {
                    "authors": [
                      {
                        "name": "Joshua Bloch",
                        "birthDate": "1961-08-28",
                        "mainGenre": "Programming",
                        "_embedded": {
                          "customBooks": [
                            {
                              "title": "Effective Java",
                              "author": "Joshua Bloch",
                              "isbn": "978-0134685991",
                              "publishedDate": "2018-01-06",
                              "_links": {
                                "other": {
                                  "href": "embedded/other/link"
                                },
                                "self": {
                                  "href": "embedded/self/link"
                                }
                              }
                            }
                          ]
                        },
                        "_links": {
                          "other": {
                            "href": "resource/other/link"
                          },
                          "self": {
                            "href": "resource/self/link"
                          }
                        }
                      },
                      {
                        "name": "Erich Gamma",
                        "birthDate": "1961-03-13",
                        "mainGenre": "Software Architecture",
                        "_embedded": {
                          "customBooks": [
                            {
                              "title": "Design Patterns: Elements of Reusable Object-Oriented Software",
                              "author": "Erich Gamma",
                              "isbn": "978-0201633610",
                              "publishedDate": "1994-10-31",
                              "_links": {
                                "other": {
                                  "href": "embedded/other/link"
                                },
                                "self": {
                                  "href": "embedded/self/link"
                                }
                              }
                            }
                          ]
                        },
                        "_links": {
                          "other": {
                            "href": "resource/other/link"
                          },
                          "self": {
                            "href": "resource/self/link"
                          }
                        }
                      }
                    ]
                  },
                  "_links": {
                    "other": {
                      "href": "resource-list/other/link"
                    },
                    "self": {
                      "href": "resource-list/self/link"
                    }
                  }
                }
                """, actualJson, NON_EXTENSIBLE);
    }

    @Test
    public void givenListOfResourceWithListOfMultipleEmbedded_whenSerialized_thenNoErrors() throws Exception {
        //GIVEN
        Author resource0 = testData.getAuthorByName(AuthorName.BRIAN_GOETZ);
        List<Book> embeddeds0 = testData.getAllBooksByAuthorName(AuthorName.BRIAN_GOETZ);
        Author resource1 = testData.getAuthorByName(AuthorName.ERICH_GAMMA);
        List<Book> embeddeds1 = testData.getAllBooksByAuthorName(AuthorName.ERICH_GAMMA);

        MultiRightPairList<Author, Book> resourcesToWrap = MultiRightPairList.of(
                resource0, embeddeds0, //2 books
                resource1, embeddeds1); //3 books

        //WHEN
        HalListWrapper<Author, Book> actual =
                assemblerUnderTest.wrapInListWrapper(MultiRightPairFlux.fromIterable(resourcesToWrap), null)
                        .block();

        //THEN
        String actualJson = mapper.writeValueAsString(actual);
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
                                "other": {
                                  "href": "embedded/other/link"
                                },
                                "self": {
                                  "href": "embedded/self/link"
                                }
                              }
                            },
                            {
                              "title": "Java Puzzlers",
                              "author": "Brian Goetz",
                              "isbn": "978-0321336781",
                              "publishedDate": "2005-07-24",
                              "_links": {
                                "other": {
                                  "href": "embedded/other/link"
                                },
                                "self": {
                                  "href": "embedded/self/link"
                                }
                              }
                            }
                          ]
                        },
                        "_links": {
                          "other": {
                            "href": "resource/other/link"
                          },
                          "self": {
                            "href": "resource/self/link"
                          }
                        }
                      },
                      {
                        "name": "Erich Gamma",
                        "birthDate": "1961-03-13",
                        "mainGenre": "Software Architecture",
                        "_embedded": {
                          "customBooks": [
                            {
                              "title": "Design Patterns: Elements of Reusable Object-Oriented Software",
                              "author": "Erich Gamma",
                              "isbn": "978-0201633610",
                              "publishedDate": "1994-10-31",
                              "_links": {
                                "other": {
                                  "href": "embedded/other/link"
                                },
                                "self": {
                                  "href": "embedded/self/link"
                                }
                              }
                            },
                            {
                              "title": "Head First Design Patterns",
                              "author": "Erich Gamma",
                              "isbn": "978-0596007126",
                              "publishedDate": "2004-10-25",
                              "_links": {
                                "other": {
                                  "href": "embedded/other/link"
                                },
                                "self": {
                                  "href": "embedded/self/link"
                                }
                              }
                            },
                            {
                              "title": "Pattern-Oriented Software Architecture Volume 1",
                              "author": "Erich Gamma",
                              "isbn": "978-0471958697",
                              "publishedDate": "1995-10-25",
                              "_links": {
                                "other": {
                                  "href": "embedded/other/link"
                                },
                                "self": {
                                  "href": "embedded/self/link"
                                }
                              }
                            }
                          ]
                        },
                        "_links": {
                          "other": {
                            "href": "resource/other/link"
                          },
                          "self": {
                            "href": "resource/self/link"
                          }
                        }
                      }
                    ]
                  },
                  "_links": {
                    "other": {
                      "href": "resource-list/other/link"
                    },
                    "self": {
                      "href": "resource-list/self/link"
                    }
                  }
                }
                """, actualJson, NON_EXTENSIBLE);
    }


    @Test
    public void givenListOfResourceWithSingleEmbeddedAndPaging_whenSerialized_thenNoErrors() throws Exception {
        //GIVEN
        Author resource0 = testData.getAuthorByName(AuthorName.ROBERT_MARTIN);
        List<Book> embeddeds0 = testData.getAllBooksByAuthorName(AuthorName.ROBERT_MARTIN);
        Author resource1 = testData.getAuthorByName(AuthorName.JOSHUA_BLOCH);
        List<Book> embeddeds1 = testData.getAllBooksByAuthorName(AuthorName.JOSHUA_BLOCH);

        MultiRightPairList<Author, Book> resourcesToWrap = MultiRightPairList.of(
                resource0, embeddeds0, //1 book
                resource1, embeddeds1); //1 book

        MultiRightPairFlux<Author, Book> resourcesToWrapAsFlux = MultiRightPairFlux.fromIterable(resourcesToWrap);
        Mono<Long> totalElements = Mono.just(10L);
        int pageSize = 2;
        long offset = 0L;
        List<SortCriteria> sortCriteria = List.of();

        //WHEN
        HalListWrapper<Author, Book> actual = assemblerUnderTest.wrapInListWrapper(resourcesToWrapAsFlux,
                        totalElements, pageSize, offset, sortCriteria, null)
                .block();

        //THEN
        String actualJson = mapper.writeValueAsString(actual);
        JSONAssert.assertEquals("""
                {
                  "page": {
                    "size": 2,
                    "totalElements": 10,
                    "totalPages": 5,
                    "number": 0
                  },
                  "_embedded": {
                    "authors": [
                      {
                        "name": "Robert C. Martin",
                        "birthDate": "1952-12-05",
                        "mainGenre": "Software Engineering",
                        "_embedded": {
                          "customBooks": [
                            {
                              "title": "Clean Code",
                              "author": "Robert C. Martin",
                              "isbn": "978-0132350884",
                              "publishedDate": "2008-08-01",
                              "_links": {
                                "other": {
                                  "href": "embedded/other/link"
                                },
                                "self": {
                                  "href": "embedded/self/link"
                                }
                              }
                            }
                          ]
                        },
                        "_links": {
                          "other": {
                            "href": "resource/other/link"
                          },
                          "self": {
                            "href": "resource/self/link"
                          }
                        }
                      },
                      {
                        "name": "Joshua Bloch",
                        "birthDate": "1961-08-28",
                        "mainGenre": "Programming",
                        "_embedded": {
                          "customBooks": [
                            {
                              "title": "Effective Java",
                              "author": "Joshua Bloch",
                              "isbn": "978-0134685991",
                              "publishedDate": "2018-01-06",
                              "_links": {
                                "other": {
                                  "href": "embedded/other/link"
                                },
                                "self": {
                                  "href": "embedded/self/link"
                                }
                              }
                            }
                          ]
                        },
                        "_links": {
                          "other": {
                            "href": "resource/other/link"
                          },
                          "self": {
                            "href": "resource/self/link"
                          }
                        }
                      }
                    ]
                  },
                  "_links": {
                    "other": {
                      "href": "resource-list/other/link"
                    },
                    "self": {
                      "href": "resource-list/self/link?page=0&size=2"
                    },
                    "last": {
                      "href": "resource-list/self/link?page=4&size=2"
                    },
                    "next": {
                      "href": "resource-list/self/link?page=1&size=2"
                    }
                  }
                }
                """, actualJson, NON_EXTENSIBLE);
    }


    @Test
    public void givenEmptyListOfResource_whenSerialized_thenNoErrors() throws Exception {
        //GIVEN
        MultiRightPairFlux<Author, Book> resourcesToWrapAsFlux =
                MultiRightPairFlux.fromIterable(MultiRightPairList.of());

        //WHEN
        HalListWrapper<Author, Book> actual = assemblerUnderTest.wrapInListWrapper(resourcesToWrapAsFlux, null)
                .block();

        //THEN
        String actualJson = mapper.writeValueAsString(actual);
        JSONAssert.assertEquals("""
                {
                  "_embedded": {
                    "authors": []
                  },
                  "_links": {
                    "other": {
                      "href": "resource-list/other/link"
                    },
                    "self": {
                      "href": "resource-list/self/link"
                    }
                  }
                }
                """, actualJson, NON_EXTENSIBLE);
    }

    @Test
    public void givenEmptyListOfResourcesWithPaging_whenSerialized_thenNoErrors() throws Exception {
        //GIVEN
        MultiRightPairFlux<Author, Book> resourcesToWrapAsFlux =
                MultiRightPairFlux.fromIterable(MultiRightPairList.of());

        Mono<Long> totalElements = Mono.just(0L);
        int pageSize = 2;
        long offset = 0L;
        List<SortCriteria> sortCriteria = List.of();

        //WHEN
        HalListWrapper<Author, Book> actual = assemblerUnderTest.wrapInListWrapper(resourcesToWrapAsFlux,
                        totalElements, pageSize, offset, sortCriteria, null)
                .block();

        //THEN
        String actualJson = mapper.writeValueAsString(actual);
        JSONAssert.assertEquals("""
                {
                  "page": {
                    "size": 2,
                    "totalElements": 0,
                    "totalPages": 0,
                    "number": 0
                  },
                  "_embedded": {
                    "authors": []
                  },
                  "_links": {
                    "other": {
                      "href": "resource-list/other/link"
                    },
                    "self": {
                      "href": "resource-list/self/link?page=0&size=2"
                    }
                  }
                }
                """, actualJson, NON_EXTENSIBLE);
    }

    @Test
    public void givenListOfResourceWithEmptyAndInitializedListEmbedded_whenSerialized_thenNoErrors() throws Exception {
        //GIVEN
        Author resourceWithEmbedded = testData.getAuthorByName(AuthorName.BRIAN_GOETZ);
        List<Book> initializedEmbedded = testData.getAllBooksByAuthorName(AuthorName.BRIAN_GOETZ);
        Author resourceWithoutEmbedded = testData.getAuthorByName(AuthorName.ERICH_GAMMA);

        MultiRightPairList<Author, Book> resourcesToWrap = MultiRightPairList.of(
                resourceWithEmbedded, initializedEmbedded, //2 books
                resourceWithoutEmbedded, List.of());

        MultiRightPairFlux<Author, Book> resourceFlux = MultiRightPairFlux.fromIterable(resourcesToWrap);

        //WHEN
        HalListWrapper<Author, Book> actual = assemblerUnderTest.wrapInListWrapper(resourceFlux, null)
                .block();

        //THEN
        String actualJson = mapper.writeValueAsString(actual);
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
                                "other": {
                                  "href": "embedded/other/link"
                                },
                                "self": {
                                  "href": "embedded/self/link"
                                }
                              }
                            },
                            {
                              "title": "Java Puzzlers",
                              "author": "Brian Goetz",
                              "isbn": "978-0321336781",
                              "publishedDate": "2005-07-24",
                              "_links": {
                                "other": {
                                  "href": "embedded/other/link"
                                },
                                "self": {
                                  "href": "embedded/self/link"
                                }
                              }
                            }
                          ]
                        },
                        "_links": {
                          "other": {
                            "href": "resource/other/link"
                          },
                          "self": {
                            "href": "resource/self/link"
                          }
                        }
                      },
                      {
                        "name": "Erich Gamma",
                        "birthDate": "1961-03-13",
                        "mainGenre": "Software Architecture",
                        "_embedded": {
                          "customBooks": []
                        },
                        "_links": {
                          "other": {
                            "href": "resource/other/link"
                          },
                          "self": {
                            "href": "resource/self/link"
                          }
                        }
                      }
                    ]
                  },
                  "_links": {
                    "other": {
                      "href": "resource-list/other/link"
                    },
                    "self": {
                      "href": "resource-list/self/link"
                    }
                  }
                }
                """, actualJson, NON_EXTENSIBLE);
    }

    @Test
    public void givenListOfResourceWithNullAndInitializedListEmbedded_whenSerialized_thenNoErrors() throws Exception {
        //GIVEN
        Author resourceWithEmbedded = testData.getAuthorByName(AuthorName.BRIAN_GOETZ);
        List<Book> initializedEmbedded = testData.getAllBooksByAuthorName(AuthorName.BRIAN_GOETZ);
        Author resourceWithoutEmbedded = testData.getAuthorByName(AuthorName.ERICH_GAMMA);

        MultiRightPairList<Author, Book> resourcesToWrap = MultiRightPairList.of(
                resourceWithEmbedded, initializedEmbedded, //2 books
                resourceWithoutEmbedded, null);

        MultiRightPairFlux<Author, Book> resourceFlux = MultiRightPairFlux.fromIterable(resourcesToWrap);

        //WHEN
        HalListWrapper<Author, Book> actual = assemblerUnderTest.wrapInListWrapper(resourceFlux, null)
                .block();

        //THEN
        String actualJson = mapper.writeValueAsString(actual);
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
                                "other": {
                                  "href": "embedded/other/link"
                                },
                                "self": {
                                  "href": "embedded/self/link"
                                }
                              }
                            },
                            {
                              "title": "Java Puzzlers",
                              "author": "Brian Goetz",
                              "isbn": "978-0321336781",
                              "publishedDate": "2005-07-24",
                              "_links": {
                                "other": {
                                  "href": "embedded/other/link"
                                },
                                "self": {
                                  "href": "embedded/self/link"
                                }
                              }
                            }
                          ]
                        },
                        "_links": {
                          "other": {
                            "href": "resource/other/link"
                          },
                          "self": {
                            "href": "resource/self/link"
                          }
                        }
                      },
                      {
                        "name": "Erich Gamma",
                        "birthDate": "1961-03-13",
                        "mainGenre": "Software Architecture",
                        "_links": {
                          "other": {
                            "href": "resource/other/link"
                          },
                          "self": {
                            "href": "resource/self/link"
                          }
                        }
                      }
                    ]
                  },
                  "_links": {
                    "other": {
                      "href": "resource-list/other/link"
                    },
                    "self": {
                      "href": "resource-list/self/link"
                    }
                  }
                }
                """, actualJson, NON_EXTENSIBLE);
    }


    @Test
    public void givenListOfResourceWithNullAndInitializedSingleEmbedded_whenSerialized_thenNoErrors() throws Exception {
        //GIVEN
        Author resourceWithEmbedded = testData.getAuthorByName(AuthorName.BRIAN_GOETZ);
        Book initializedEmbedded = testData.getAllBooksByAuthorName(AuthorName.BRIAN_GOETZ).get(0);
        Author resourceWithoutEmbedded = testData.getAuthorByName(AuthorName.ERICH_GAMMA);

        PairList<Author, Book> resourcesToWrap = PairList.of(
                resourceWithEmbedded, initializedEmbedded, //2 books
                resourceWithoutEmbedded, null);

        PairFlux<Author, Book> resourceFlux = PairFlux.fromIterable(resourcesToWrap);

        //WHEN
        HalListWrapper<Author, Book> actual = assemblerUnderTest.wrapInListWrapper(resourceFlux, null)
                .block();

        //THEN
        String actualJson = mapper.writeValueAsString(actual);
        JSONAssert.assertEquals("""
                {
                  "_embedded": {
                    "authors": [
                      {
                        "name": "Brian Goetz",
                        "birthDate": "1969-05-22",
                        "mainGenre": "Programming Languages",
                        "_embedded": {
                          "customBook": {
                            "title": "Java Concurrency in Practice",
                            "author": "Brian Goetz",
                            "isbn": "978-0321349606",
                            "publishedDate": "2006-05-19",
                            "_links": {
                              "other": {
                                "href": "embedded/other/link"
                              },
                              "self": {
                                "href": "embedded/self/link"
                              }
                            }
                          }
                        },
                        "_links": {
                          "other": {
                            "href": "resource/other/link"
                          },
                          "self": {
                            "href": "resource/self/link"
                          }
                        }
                      },
                      {
                        "name": "Erich Gamma",
                        "birthDate": "1961-03-13",
                        "mainGenre": "Software Architecture",
                        "_links": {
                          "other": {
                            "href": "resource/other/link"
                          },
                          "self": {
                            "href": "resource/self/link"
                          }
                        }
                      }
                    ]
                  },
                  "_links": {
                    "other": {
                      "href": "resource-list/other/link"
                    },
                    "self": {
                      "href": "resource-list/self/link"
                    }
                  }
                }
                """, actualJson, NON_EXTENSIBLE);


    }

}
