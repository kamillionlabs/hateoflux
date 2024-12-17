package de.kamillionlabs.hateoflux.http;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpHeadersModuleTest {

    private static class TestHttpHeadersModule extends HttpHeadersModule<TestHttpHeadersModule> {
    }

    @Test
    void givenValidHeader_whenWithHeader_thenHeaderAdded() {
        // GIVEN
        TestHttpHeadersModule module = new TestHttpHeadersModule();

        // WHEN
        module.withHeader("X-Test-Header", "value1", "value2");

        // THEN
        assertThat(module.headers).isNotNull();
        assertThat(module.headers.get("X-Test-Header"))
                .containsExactly("value1", "value2");
    }

    @Test
    void givenValidContentType_whenWithContentType_thenContentTypeAdded() {
        // GIVEN
        TestHttpHeadersModule module = new TestHttpHeadersModule();

        // WHEN
        module.withContentType(MediaType.APPLICATION_JSON);

        // THEN
        assertThat(module.headers).isNotNull();
        assertThat(module.headers.get(HttpHeaders.CONTENT_TYPE))
                .containsExactly(MediaType.APPLICATION_JSON_VALUE);
    }

    @Test
    void givenValidLocation_whenWithLocation_thenLocationAdded() {
        // GIVEN
        TestHttpHeadersModule module = new TestHttpHeadersModule();
        URI location = URI.create("http://example.com/resource");

        // WHEN
        module.withLocation(location);

        // THEN
        assertThat(module.headers).isNotNull();
        assertThat(module.headers.get(HttpHeaders.LOCATION))
                .containsExactly(location.toString());
    }

    @Test
    void givenValidETag_whenWithETag_thenETagAdded() {
        // GIVEN
        TestHttpHeadersModule module = new TestHttpHeadersModule();

        // WHEN
        module.withETag("\"12345\"");

        // THEN
        assertThat(module.headers).isNotNull();
        assertThat(module.headers.get(HttpHeaders.ETAG)).containsExactly("\"12345\"");
    }

    @Test
    void givenNullMediaType_whenAddContentType_thenThrowsException() {
        // GIVEN
        TestHttpHeadersModule module = new TestHttpHeadersModule();

        // WHEN & THEN
        assertThatThrownBy(() -> module.withContentType((String) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("MediaType is not allowed to be null");
    }

    @Test
    void givenEmptyMediaType_whenAddContentType_thenThrowsException() {
        // GIVEN
        TestHttpHeadersModule module = new TestHttpHeadersModule();

        // WHEN & THEN
        assertThatThrownBy(() -> module.withContentType(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("MediaType is not allowed to be empty");
    }

    @Test
    void givenNullLocation_whenAddLocation_thenThrowsException() {
        // GIVEN
        TestHttpHeadersModule module = new TestHttpHeadersModule();

        // WHEN & THEN
        assertThatThrownBy(() -> module.withLocation((String) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Location URI is not allowed to be null");
    }

    @Test
    void givenEmptyLocation_whenAddLocation_thenThrowsException() {
        // GIVEN
        TestHttpHeadersModule module = new TestHttpHeadersModule();

        // WHEN & THEN
        assertThatThrownBy(() -> module.withLocation(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Location URI is not allowed to be empty");
    }

    @Test
    void givenNullETag_whenAddETag_thenThrowsException() {
        // GIVEN
        TestHttpHeadersModule module = new TestHttpHeadersModule();

        // WHEN & THEN
        assertThatThrownBy(() -> module.withETag(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ETag is not allowed to be null");
    }

    @Test
    void givenEmptyETag_whenAddETag_thenThrowsException() {
        // GIVEN
        TestHttpHeadersModule module = new TestHttpHeadersModule();

        // WHEN & THEN
        assertThatThrownBy(() -> module.withETag(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("ETag is not allowed to be empty");
    }


    @Test
    void givenAllMethodsAtOnes_whenAllWithMethodsExecuted_thenChainingReturnsCorrectType() {
        // GIVEN
        TestHttpHeadersModule module = new TestHttpHeadersModule();

        //THEN type is correct and doesnt result in compile errors
        TestHttpHeadersModule testHttpHeadersModule = module
                //WHEN
                .withHeader("X-Test-Header", "value1", "value2")
                .withContentType("application/json")
                .withLocation("http://example.com/resource")
                .withETag("\"12345\"");
    }
}

