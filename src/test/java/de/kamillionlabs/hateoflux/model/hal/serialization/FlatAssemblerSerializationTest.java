/*
 * Copyright (c)  2024 kamillion labs contributors
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
 * @since 16.11.2024
 */
package de.kamillionlabs.hateoflux.model.hal.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.kamillionlabs.hateoflux.assembler.FlatHalWrapperAssembler;
import de.kamillionlabs.hateoflux.dummy.TestDataGenerator;
import de.kamillionlabs.hateoflux.dummy.TestDataGenerator.AuthorName;
import de.kamillionlabs.hateoflux.dummy.model.Author;
import de.kamillionlabs.hateoflux.model.hal.HalListWrapper;
import de.kamillionlabs.hateoflux.model.hal.HalResourceWrapper;
import de.kamillionlabs.hateoflux.model.link.Link;
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
public class FlatAssemblerSerializationTest {
    // Implementation for testing purposes -----------------------------------------------------------------------------
    static class AssemblerUnderTest implements FlatHalWrapperAssembler<Author> {


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
    }
    // -----------------------------------------------------------------------------------------------------------------

    private final AssemblerUnderTest assemblerUnderTest = new AssemblerUnderTest();

    private final ObjectMapper mapper = new ObjectMapper();

    private final TestDataGenerator testData = new TestDataGenerator();

    @Test
    public void givenSingleNonEmptyResource_whenSerialized_thenNoErrors() throws Exception {
        //GIVEN
        var resource = Mono.just(testData.getAuthorByName(AuthorName.JOSHUA_BLOCH));

        //WHEN
        HalResourceWrapper<Author, Void> actual =
                assemblerUnderTest.wrapInResourceWrapper(resource, null).block();

        //THEN
        String actualJson = mapper.writeValueAsString(actual);

        JSONAssert.assertEquals("""
                {
                  "name": "Joshua Bloch",
                  "birthDate": "1961-08-28",
                  "mainGenre": "Programming",
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
    public void givenNonEmptyListOfResource_whenSerialized_thenNoErrors() throws Exception {
        //GIVEN
        var resource = Flux.just(testData.getAuthorByName(AuthorName.JOSHUA_BLOCH),
                testData.getAuthorByName(AuthorName.MICHAEL_FEATHERS));

        //WHEN
        HalListWrapper<Author, Void> actual =
                assemblerUnderTest.wrapInListWrapper(resource, null).block();

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
                         "name": "Michael Feathers",
                         "birthDate": "1966-01-27",
                         "mainGenre": "Software Maintenance",
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
    public void givenEmptyListOfResource_whenSerialized_thenNoErrors() throws Exception {
        //GIVEN
        var resource = Flux.<Author>empty();

        //WHEN
        HalListWrapper<Author, Void> actual =
                assemblerUnderTest.wrapInListWrapper(resource, null).block();

        //THEN
        String actualJson = mapper.writeValueAsString(actual);
        JSONAssert.assertEquals("""
                {
                   "_embedded": {
                     "authors": [
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
    public void givenEmptyListOfResourceWithPaging_whenSerialized_thenNoErrors() throws Exception {
        //GIVEN
        var resource = Flux.<Author>empty();

        //WHEN
        HalListWrapper<Author, Void> actual =
                assemblerUnderTest.wrapInListWrapper(resource, Mono.just(10L), 2, null, List.of(), null).block();

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
                      "authors": []
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

}
