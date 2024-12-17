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
 * @since 03.06.2024
 */

package de.kamillionlabs.hateoflux.dummy.controller;

import de.kamillionlabs.hateoflux.model.hal.Composite;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.UUID;

/**
 * @author Younes El Ouarti
 */
@RestController
@RequestMapping("/dummy")
public class DummyController {


    @GetMapping("/getmapping-url")
    public Mono<Integer> getMappingSimple() {
        return Mono.just(3);
    }

    @PostMapping("/t1/{someUuId}")
    public Mono<String> postMappingWithParameter(@PathVariable UUID someUuId) {
        return Mono.just("test");
    }

    @PostMapping("/t2/{someUuId}")
    public Mono<Void> postMappingWithParameterAndCustomName(@PathVariable("someUuId") String customName) {
        return Mono.empty();
    }

    @PostMapping("/t3/{someUuId}/")
    public Mono<Void> postMappingWithParameterAndSlash(@PathVariable UUID someUuId) {
        return Mono.empty();
    }


    @PutMapping("/{someUuId}/subresource")
    public Mono<Void> putMappingWithParameterAsSubresource(@PathVariable UUID someUuId) {
        return Mono.empty();
    }

    @RequestMapping(value = "/request1", method = RequestMethod.PUT)
    public Mono<Void> requestPutMappingWithQueryParameters(@RequestParam(required = false) Integer size,
                                                           @RequestParam String name) {
        return Mono.empty();
    }

    @RequestMapping(value = "/request2", method = RequestMethod.PUT)
    public Mono<Void> requestPutMappingWithQueryParameterAndCustomName(@RequestParam("customSize") Integer size) {
        return Mono.empty();
    }

    @RequestMapping(value = "/request3/", method = RequestMethod.PUT)
    public Mono<Void> requestPutMappingWithQueryParametersAndSlash(@RequestParam(required = false) Integer size,
                                                                   @RequestParam String name) {
        return Mono.empty();
    }

    @PatchMapping("/{someUuId}/subresource")
    public Mono<Void> patchMappingWithParameterAsSubresourceAndQueryParameter(@PathVariable UUID someUuId,
                                                                              @RequestParam String name) {
        return Mono.empty();
    }

    @PostMapping("/names1")
    public Mono<Void> postMappingWithCollectionAsQueryParameter(@RequestParam Collection<String> names) {
        return Mono.empty();
    }

    @PostMapping("/names2")
    public Mono<Void> postMappingWithCompositeCollectionAsQueryParameter(@Composite @RequestParam Collection<String> names) {
        return Mono.empty();
    }

    @PostMapping("/void-of-nothing")
    public Mono<Void> postMappingWithVoidAsReturnValue() {
        return Mono.empty();
    }


}
