package de.kamillionlabs.hateoflux.http;

import de.kamillionlabs.hateoflux.model.hal.HalListWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class HalListResponseTest {

    // GIVEN constants for testing
    private final HalListWrapper<String, String> mockWrapper = mock(HalListWrapper.class);
    private final Mono<HalListWrapper<String, String>> mockBody = Mono.just(mockWrapper);
    private final Mono<HttpStatus> conflictStatus = Mono.just(HttpStatus.CONFLICT);
    private final HttpHeaders headers = new HttpHeaders();


    @Test
    void givenBodyAndStatus_whenConstructed_thenCorrectValuesAssigned() {
        // GIVEN
        HalListResponse<String, String> response = new HalListResponse<>(mockBody, conflictStatus, null);

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
        HalListResponse<String, String> response = HalListResponse.of(mockBody, conflictStatus);

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
        HalListResponse<String, String> response = new HalListResponse<>(Mono.empty(), conflictStatus, headers);

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
        HalListResponse<String, String> response = HalListResponse.of(conflictStatus);

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
        HalListResponse<String, String> response = HalListResponse.of(HttpStatus.CONFLICT);

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
        HalListResponse<String, String> response = HalListResponse.ok(mockBody);

        // WHEN
        ResponseEntity<?> entity = response.toResponseEntity().block();

        // THEN
        assertThat(entity).isNotNull();
        assertThat(entity.getBody()).isEqualTo(mockWrapper);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void givenFactoryMethodOkWithoutBody_whenCalled_thenCreatesResponseWithOkStatus() {
        // GIVEN
        HalListResponse<String, String> response = HalListResponse.ok();

        // WHEN
        ResponseEntity<?> entity = response.toResponseEntity().block();

        // THEN
        assertThat(entity).isNotNull();
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void givenFactoryMethodNotFound_whenCalled_thenCreatesResponseWithNotFoundStatus() {
        // GIVEN
        HalListResponse<String, String> response = HalListResponse.notFound();

        // WHEN
        ResponseEntity<?> entity = response.toResponseEntity().block();

        // THEN
        assertThat(entity).isNotNull();
        assertThat(entity.getBody()).isNull();
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void givenFactoryMethodOfWithBodyAndStatus_whenCalled_thenCreatesResponseCorrectly() {
        // GIVEN
        HalListResponse<String, String> response = HalListResponse.of(mockBody, conflictStatus);

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
        HalListResponse<String, String> response = HalListResponse.of(conflictStatus);

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
        HalListResponse<String, String> response = HalListResponse.noContent();

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
        HalListResponse<String, String> response = HalListResponse.created(mockBody);

        // WHEN
        ResponseEntity<?> entity = response.toResponseEntity().block();

        // THEN
        assertThat(entity).isNotNull();
        assertThat(entity.getBody()).isEqualTo(mockWrapper);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void givenFactoryMethodCreatedWithoutBody_whenCalled_thenCreatesResponseWithCreatedStatus() {
        // GIVEN
        HalListResponse<String, String> response = HalListResponse.created();

        // WHEN
        ResponseEntity<?> entity = response.toResponseEntity().block();

        // THEN
        assertThat(entity).isNotNull();
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void givenFactoryMethodAccepted_whenCalled_thenCreatesResponseWithAcceptedStatus() {
        // GIVEN
        HalListResponse<String, String> response = HalListResponse.accepted(mockBody);

        // WHEN
        ResponseEntity<?> entity = response.toResponseEntity().block();

        // THEN
        assertThat(entity).isNotNull();
        assertThat(entity.getBody()).isEqualTo(mockWrapper);
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
    }

    @Test
    void givenFactoryMethodAcceptedWithoutBody_whenCalled_thenCreatesResponseWithAcceptedStatus() {
        // GIVEN
        HalListResponse<String, String> response = HalListResponse.accepted();

        // WHEN
        ResponseEntity<?> entity = response.toResponseEntity().block();

        // THEN
        assertThat(entity).isNotNull();
        assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
    }
}
