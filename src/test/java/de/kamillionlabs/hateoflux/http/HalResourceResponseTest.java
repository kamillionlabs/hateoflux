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
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class HalResourceResponseTest {

    private final HalResourceWrapper<String, String> mockWrapper = mock(HalResourceWrapper.class);
    private final Mono<HalResourceWrapper<String, String>> mockBody = Mono.just(mockWrapper);
    private final Mono<HttpStatus> conflictStatus = Mono.just(HttpStatus.CONFLICT);
    private final HttpHeaders headers = new HttpHeaders();

    @Test
    void givenBodyAndStatus_whenConstructed_thenCorrectValuesAssigned() {
        // GIVEN
        HalResourceResponse<String, String> response = new HalResourceResponse<>(mockBody, conflictStatus, null);

        // WHEN
        ResponseEntity<?> entity = response.toResponseEntity().block();

        // THEN
        assertThat(entity).isNotNull();
        assertThat(entity.getBody()).isEqualTo(mockWrapper);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void givenBodyAndStatus_whenOfCalled_thenCorrectValuesAssigned() {
        // GIVEN
        HalResourceResponse<String, String> response = HalResourceResponse.of(mockBody, conflictStatus);

        // WHEN
        ResponseEntity<?> entity = response.toResponseEntity().block();

        // THEN
        assertThat(entity).isNotNull();
        assertThat(entity.getBody()).isEqualTo(mockWrapper);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void givenEmptyBody_whenToResponseEntity_thenReturnsResponseWithOnlyHeadersAndStatus() {
        // GIVEN
        HalResourceResponse<String, String> response = new HalResourceResponse<>(Mono.empty(), conflictStatus, headers);

        // WHEN
        ResponseEntity<?> entity = response.toResponseEntity().block();

        // THEN
        assertThat(entity).isNotNull();
        assertThat(entity.getBody()).isNull();
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void givenOnlyMonoStatus_whenOfCalled_thenReturnsResponseWithOnlyHeadersAndStatus() {
        // GIVEN
        HalResourceResponse<String, String> response = HalResourceResponse.of(conflictStatus);

        // WHEN
        ResponseEntity<?> entity = response.toResponseEntity().block();

        // THEN
        assertThat(entity).isNotNull();
        assertThat(entity.getBody()).isNull();
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void givenOnlyStatus_whenOfCalled_thenReturnsResponseWithOnlyHeadersAndStatus() {
        // GIVEN
        HalResourceResponse<String, String> response = HalResourceResponse.of(HttpStatus.CONFLICT);

        // WHEN
        ResponseEntity<?> entity = response.toResponseEntity().block();

        // THEN
        assertThat(entity).isNotNull();
        assertThat(entity.getBody()).isNull();
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void givenFactoryMethodOk_whenCalled_thenCreatesResponseWithOkStatus() {
        // GIVEN
        HalResourceResponse<String, String> response = HalResourceResponse.ok(mockBody);

        // WHEN
        ResponseEntity<?> entity = response.toResponseEntity().block();

        // THEN
        assertThat(entity).isNotNull();
        assertThat(entity.getBody()).isEqualTo(mockWrapper);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void givenFactoryMethodOkWithoutBody_whenCalled_thenCreatesResponseWithBadRequestStatus() {
        // GIVEN
        HalResourceResponse<String, String> response = HalResourceResponse.ok();

        // WHEN
        ResponseEntity<?> entity = response.toResponseEntity().block();

        // THEN
        assertThat(entity).isNotNull();
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void givenFactoryMethodNotFound_whenCalled_thenCreatesResponseWithNotFoundStatus() {
        // GIVEN
        HalResourceResponse<String, String> response = HalResourceResponse.notFound();

        // WHEN
        ResponseEntity<?> entity = response.toResponseEntity().block();

        // THEN
        assertThat(entity).isNotNull();
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void givenFactoryMethodOfWithBodyAndStatus_whenCalled_thenCreatesResponseCorrectly() {
        // GIVEN
        HalResourceResponse<String, String> response = HalResourceResponse.of(mockBody, conflictStatus);

        // WHEN
        ResponseEntity<?> entity = response.toResponseEntity().block();

        // THEN
        assertThat(entity).isNotNull();
        assertThat(entity.getBody()).isEqualTo(mockWrapper);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void givenFactoryMethodOfWithOnlyStatus_whenCalled_thenCreatesResponseCorrectly() {
        // GIVEN
        HalResourceResponse<String, String> response = HalResourceResponse.of(conflictStatus);

        // WHEN
        ResponseEntity<?> entity = response.toResponseEntity().block();

        // THEN
        assertThat(entity).isNotNull();
        assertThat(entity.getBody()).isNull();
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void givenNoContentFactory_whenCalled_thenCreatesResponseWithNoContentStatus() {
        // GIVEN
        HalResourceResponse<String, String> response = HalResourceResponse.noContent();

        // WHEN
        ResponseEntity<?> entity = response.toResponseEntity().block();

        // THEN
        assertThat(entity).isNotNull();
        assertThat(entity.getBody()).isNull();
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void givenFactoryMethodCreated_whenCalled_thenCreatesResponseWithCreatedStatus() {
        // GIVEN
        HalResourceResponse<String, String> response = HalResourceResponse.created(mockBody);

        // WHEN
        ResponseEntity<?> entity = response.toResponseEntity().block();

        // THEN
        assertThat(entity).isNotNull();
        assertThat(entity.getBody()).isEqualTo(mockWrapper);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void givenFactoryMethodAccepted_whenCalled_thenCreatesResponseWithAcceptedStatus() {
        // GIVEN
        HalResourceResponse<String, String> response = HalResourceResponse.accepted(mockBody);

        // WHEN
        ResponseEntity<?> entity = response.toResponseEntity().block();

        // THEN
        assertThat(entity).isNotNull();
        assertThat(entity.getBody()).isEqualTo(mockWrapper);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
    }

    @Test
    void givenFactoryMethodCreatedWithoutBody_whenCalled_thenCreatesResponseWithUnauthorizedStatus() {
        // GIVEN
        HalResourceResponse<String, String> response = HalResourceResponse.created();

        // WHEN
        ResponseEntity<?> entity = response.toResponseEntity().block();

        // THEN
        assertThat(entity).isNotNull();
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void givenFactoryMethodAcceptedWithoutBody_whenCalled_thenCreatesResponseWithForbiddenStatus() {
        // GIVEN
        HalResourceResponse<String, String> response = HalResourceResponse.accepted();

        // WHEN
        ResponseEntity<?> entity = response.toResponseEntity().block();

        // THEN
        assertThat(entity).isNotNull();
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
    }

}