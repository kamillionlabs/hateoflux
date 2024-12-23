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
 */
package de.kamillionlabs.hateoflux.http;

import de.kamillionlabs.hateoflux.model.hal.HalResourceWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class HalMultiResourceResponseTest {

    // GIVEN constants for testing
    private final HalResourceWrapper<String, String> mockWrapper = mock(HalResourceWrapper.class);
    private final Flux<HalResourceWrapper<String, String>> mockBody = Flux.just(mockWrapper);
    private final HttpStatus conflictStatus = HttpStatus.CONFLICT;
    private final HttpHeaders headers = new HttpHeaders();

    @Test
    void givenBodyAndStatus_whenConstructed_thenCorrectValuesAssigned() {
        // GIVEN
        HalMultiResourceResponse<String, String> response = new HalMultiResourceResponse<>(mockBody, conflictStatus,
                null);

        // WHEN
        ResponseEntity<?> entity = response.toResponseEntity().block();

        // THEN
        assertThat(entity).isNotNull();
        assertThat(entity.getBody()).isNotNull();
        Flux<HalResourceWrapper<String, String>> body = (Flux) entity.getBody();
        assertThat(body.blockFirst()).isEqualTo(mockWrapper);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void givenBodyAndStatus_whenOfCalled_thenCorrectValuesAssigned() {
        // GIVEN
        HalMultiResourceResponse<String, String> response = HalMultiResourceResponse.of(mockBody, conflictStatus);

        // WHEN
        ResponseEntity<?> entity = response.toResponseEntity().block();

        // THEN
        assertThat(entity).isNotNull();
        assertThat(entity.getBody()).isNotNull();
        Flux<HalResourceWrapper<String, String>> body = (Flux) entity.getBody();
        assertThat(body.blockFirst()).isEqualTo(mockWrapper);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void givenEmptyBody_whenToResponseEntity_thenReturnsResponseWithOnlyHeadersAndStatus() {
        // GIVEN
        HalMultiResourceResponse<String, String> response = new HalMultiResourceResponse<>(Flux.empty(),
                conflictStatus, headers);

        // WHEN
        ResponseEntity<?> entity = response.toResponseEntity().block();

        // THEN
        assertThat(entity).isNotNull();
        assertThat(entity.getBody()).isNotNull();
        Flux body = (Flux) entity.getBody();
        Mono<Boolean> hasElements = body.hasElements();
        assertEquals(Boolean.FALSE, hasElements.block());
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void givenOnlyMonoStatus_whenOfCalled_thenReturnsResponseWithOnlyHeadersAndStatus() {
        // GIVEN
        HalMultiResourceResponse<String, String> response = HalMultiResourceResponse.of(conflictStatus);

        // WHEN
        ResponseEntity<?> entity = response.toResponseEntity().block();

        // THEN
        assertThat(entity).isNotNull();
        assertThat(entity.getBody()).isNotNull();
        Flux body = (Flux) entity.getBody();
        Mono<Boolean> hasElements = body.hasElements();
        assertEquals(Boolean.FALSE, hasElements.block());
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void givenOnlyStatus_whenOfCalled_thenReturnsResponseWithOnlyHeadersAndStatus() {
        // GIVEN
        HalMultiResourceResponse<String, String> response = HalMultiResourceResponse.of(HttpStatus.CONFLICT);

        // WHEN
        ResponseEntity<?> entity = response.toResponseEntity().block();

        // THEN
        assertThat(entity).isNotNull();
        assertThat(entity.getBody()).isNotNull();
        Flux body = (Flux) entity.getBody();
        Mono<Boolean> hasElements = body.hasElements();
        assertEquals(Boolean.FALSE, hasElements.block());
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void givenFactoryMethodOk_whenCalled_thenCreatesResponseWithOkStatus() {
        // GIVEN
        HalMultiResourceResponse<String, String> response = HalMultiResourceResponse.ok(mockBody);

        // WHEN
        ResponseEntity<?> entity = response.toResponseEntity().block();

        // THEN
        assertThat(entity).isNotNull();
        assertThat(entity.getBody()).isNotNull();
        Flux<HalResourceWrapper<String, String>> body = (Flux) entity.getBody();
        assertThat(body.blockFirst()).isEqualTo(mockWrapper);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void givenFactoryMethodOkWithoutBody_whenCalled_thenCreatesResponseWithOkStatus() {
        // GIVEN
        HalMultiResourceResponse<String, String> response = HalMultiResourceResponse.ok();

        // WHEN
        ResponseEntity<?> entity = response.toResponseEntity().block();

        // THEN
        assertThat(entity).isNotNull();
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void givenFactoryMethodNotFound_whenCalled_thenCreatesResponseWithNotFoundStatus() {
        // GIVEN
        HalMultiResourceResponse<String, String> response = HalMultiResourceResponse.notFound();

        // WHEN
        ResponseEntity<?> entity = response.toResponseEntity().block();

        // THEN
        assertThat(entity).isNotNull();
        assertThat(entity.getBody()).isNotNull();
        Flux body = (Flux) entity.getBody();
        Mono<Boolean> hasElements = body.hasElements();
        assertEquals(Boolean.FALSE, hasElements.block());
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void givenFactoryMethodOfWithBodyAndStatus_whenCalled_thenCreatesResponseCorrectly() {
        // GIVEN
        HalMultiResourceResponse<String, String> response = HalMultiResourceResponse.of(mockBody, conflictStatus);

        // WHEN
        ResponseEntity<?> entity = response.toResponseEntity().block();

        // THEN
        assertThat(entity).isNotNull();
        assertThat(entity.getBody()).isNotNull();
        Flux<HalResourceWrapper<String, String>> body = (Flux) entity.getBody();
        assertThat(body.blockFirst()).isEqualTo(mockWrapper);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void givenFactoryMethodOfWithOnlyStatus_whenCalled_thenCreatesResponseCorrectly() {
        // GIVEN
        HalMultiResourceResponse<String, String> response = HalMultiResourceResponse.of(conflictStatus);

        // WHEN
        ResponseEntity<?> entity = response.toResponseEntity().block();

        // THEN
        assertThat(entity).isNotNull();
        assertThat(entity.getBody()).isNotNull();
        Flux body = (Flux) entity.getBody();
        Mono<Boolean> hasElements = body.hasElements();
        assertEquals(Boolean.FALSE, hasElements.block());
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void givenNoContentFactory_whenCalled_thenCreatesResponseWithNoContentStatus() {
        // GIVEN
        HalMultiResourceResponse<String, String> response = HalMultiResourceResponse.noContent();

        // WHEN
        ResponseEntity<?> entity = response.toResponseEntity().block();

        // THEN
        assertThat(entity).isNotNull();
        assertThat(entity.getBody()).isNotNull();
        Flux body = (Flux) entity.getBody();
        Mono<Boolean> hasElements = body.hasElements();
        assertEquals(Boolean.FALSE, hasElements.block());
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void givenFactoryMethodCreatedWithoutBody_whenCalled_thenCreatesResponseWithCreatedStatus() {
        // GIVEN
        HalMultiResourceResponse<String, String> response = HalMultiResourceResponse.created();

        // WHEN
        ResponseEntity<?> entity = response.toResponseEntity().block();

        // THEN
        assertThat(entity).isNotNull();
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void givenFactoryMethodAcceptedWithoutBody_whenCalled_thenCreatesResponseWithAcceptedStatus() {
        // GIVEN
        HalMultiResourceResponse<String, String> response = HalMultiResourceResponse.accepted();

        // WHEN
        ResponseEntity<?> entity = response.toResponseEntity().block();

        // THEN
        assertThat(entity).isNotNull();
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
    }

    @Test
    void givenFactoryMethodCreated_whenCalled_thenCreatesResponseWithCreatedStatus() {
        // GIVEN
        HalMultiResourceResponse<String, String> response = HalMultiResourceResponse.created(mockBody);

        // WHEN
        ResponseEntity<?> entity = response.toResponseEntity().block();

        // THEN
        assertThat(entity).isNotNull();
        assertThat(entity.getBody()).isNotNull();
        Flux<HalResourceWrapper<String, String>> body = (Flux) entity.getBody();
        assertThat(body.blockFirst()).isEqualTo(mockWrapper);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void givenFactoryMethodAccepted_whenCalled_thenCreatesResponseWithAcceptedStatus() {
        // GIVEN
        HalMultiResourceResponse<String, String> response = HalMultiResourceResponse.accepted(mockBody);

        // WHEN
        ResponseEntity<?> entity = response.toResponseEntity().block();

        // THEN
        assertThat(entity).isNotNull();
        assertThat(entity.getBody()).isNotNull();
        Flux<HalResourceWrapper<String, String>> body = (Flux) entity.getBody();
        assertThat(body.blockFirst()).isEqualTo(mockWrapper);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
    }
}
