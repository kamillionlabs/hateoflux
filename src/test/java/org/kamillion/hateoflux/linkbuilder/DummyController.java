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
 * @since 03.06.2024
 */

package org.kamillion.hateoflux.linkbuilder;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * @author Younes El Ouarti
 */
@RestController
@RequestMapping("/dummy")
public class DummyController {


    @GetMapping("/getmapping-url")
    public Mono<Integer> getMappingUrl() {
        return Mono.just(3);
    }

    @PostMapping("/{someUuId}")
    public Mono<Void> postMappingAndParameter(@PathVariable UUID someUuId) {
        return Mono.empty();
    }

    @PostMapping("/{someUuId}/")
    public Mono<Void> postMappingAndParameterAndSlash(@PathVariable UUID someUuId) {
        return Mono.empty();
    }

    @PutMapping("/{someUuId}/subresource")
    public Mono<Void> putMappingAndParameterAsSubresource(@PathVariable UUID someUuId) {
        return Mono.empty();
    }

    @RequestMapping(value = "/request", method = RequestMethod.PUT)
    public Mono<Void> requestPutMappingAWithQueryParameters(@RequestParam(required = false) Integer size,
                                                            @RequestParam String name) {
        return Mono.empty();
    }

    @RequestMapping(value = "/request/", method = RequestMethod.PUT)
    public Mono<Void> requestPutMappingAWithQueryParametersAndSlash(@RequestParam(required = false) Integer size,
                                                                    @RequestParam String name) {
        return Mono.empty();
    }

    @PatchMapping("/{someUuId}/subresource")
    public Mono<Void> patchMappingAndParameterAsSubresourceAndQueryParameter(@PathVariable UUID someUuId, @RequestParam String name) {
        return Mono.empty();
    }

}
