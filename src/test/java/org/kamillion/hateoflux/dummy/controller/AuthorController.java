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
 * @since 06.07.2024
 */

package org.kamillion.hateoflux.dummy.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author Younes El Ouarti
 */
@RestController
@RequestMapping("/author")
public class AuthorController {

    @GetMapping("/{id}")
    public Mono<Void> getAuthor(@PathVariable Integer id) {
        return Mono.empty();
    }


    @GetMapping("/{id}/books")
    public Flux<Void> getBooks(@PathVariable Integer id) {
        return Flux.empty();
    }
}
