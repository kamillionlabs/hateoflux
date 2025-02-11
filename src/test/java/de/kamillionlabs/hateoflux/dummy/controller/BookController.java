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
 * @since 06.07.2024
 */

package de.kamillionlabs.hateoflux.dummy.controller;

import de.kamillionlabs.hateoflux.dummy.TestDataGenerator;
import de.kamillionlabs.hateoflux.dummy.TestDataGenerator.AuthorName;
import de.kamillionlabs.hateoflux.dummy.TestDataGenerator.BookTitle;
import de.kamillionlabs.hateoflux.dummy.model.Book;
import de.kamillionlabs.hateoflux.http.HalListResponse;
import de.kamillionlabs.hateoflux.http.HalMultiResourceResponse;
import de.kamillionlabs.hateoflux.http.HalResourceResponse;
import de.kamillionlabs.hateoflux.model.hal.HalListWrapper;
import de.kamillionlabs.hateoflux.model.hal.HalResourceWrapper;
import de.kamillionlabs.hateoflux.model.link.Link;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author Younes El Ouarti
 */
@RestController
@RequestMapping("/book")
public class BookController {

    private final TestDataGenerator testData = new TestDataGenerator();

    @GetMapping("/getmapping-url")
    public Mono<Integer> getMappingSimple() {
        return Mono.just(3);
    }

    @GetMapping("/{id}")
    public Mono<Void> getBook(@PathVariable Integer id) {
        return Mono.empty();
    }

    @GetMapping("/teapot")
    public HalResourceResponse<Book, Void> teapotEndpoint() {
        return HalResourceResponse.of(HttpStatus.I_AM_A_TEAPOT);
    }

    @GetMapping("/get-book")
    public HalResourceResponse<Book, Void> getBookFound() {
        Book book = testData.getBookByTitle(BookTitle.EFFECTIVE_JAVA);
        HalResourceWrapper<Book, Void> bookWrapper = HalResourceWrapper.wrap(book)
                .withLinks(Link.linkAsSelfOf("/book/effective-java"));

        return HalResourceResponse.ok(Mono.just(bookWrapper));
    }


    @GetMapping("/get-list-of-books")
    public HalListResponse<Book, Void> getBooksByAuthor() {
        List<Book> books = testData.getAllBooksByAuthorName(AuthorName.ERICH_GAMMA);

        List<HalResourceWrapper<Book, Void>> bookWrappers = books.stream()
                .map(book -> HalResourceWrapper.wrap(book)
                        .withLinks(Link.linkAsSelfOf("/book/" + book.getTitle().toLowerCase().replace(" ", "-"))))
                .toList();

        HalListWrapper<Book, Void> listWrapper = HalListWrapper.wrap(bookWrappers)
                .withLinks(
                        Link.linkAsSelfOf("/books/erich-gamma")
                );

        return HalListResponse.ok(Mono.just(listWrapper));
    }


    @GetMapping("/get-book-with-header")
    public HalResourceResponse<Book, Void> getBookAccepted() {
        Book book = testData.getBookByTitle(BookTitle.REFACTORING);
        HalResourceWrapper<Book, Void> bookWrapper = HalResourceWrapper.wrap(book)
                .withLinks(Link.linkAsSelfOf("/book/refactoring"));

        return HalResourceResponse.accepted(Mono.just(bookWrapper));
    }

    @GetMapping("/wrapped-halresponse")
    public Mono<HalResourceResponse<Book, Void>> getWrappedResponse() {
        return Mono.just(HalResourceResponse.noContent());
    }


    @GetMapping("/get-books-flux")
    public HalMultiResourceResponse<Book, Void> getBooksFlux() {
        Flux<HalResourceWrapper<Book, Void>> wrappers = Flux.just(
                        testData.getBookByTitle(BookTitle.EFFECTIVE_JAVA),
                        testData.getBookByTitle(BookTitle.CLEAN_CODE))
                .map(book -> HalResourceWrapper.wrap(book)
                        .withLinks(Link.linkAsSelfOf("/book/" + book.getTitle().toLowerCase().replace(" ", "-"))));

        return HalMultiResourceResponse.ok(wrappers);
    }

}
