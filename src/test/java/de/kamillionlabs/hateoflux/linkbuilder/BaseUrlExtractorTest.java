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
package de.kamillionlabs.hateoflux.linkbuilder;

import org.junit.jupiter.api.Test;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;

import static de.kamillionlabs.hateoflux.linkbuilder.BaseUrlExtractor.extractBaseUrl;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class BaseUrlExtractorTest {

    @Test
    void givenStandardHttpPort_whenExtractBaseUrl_thenPortExcluded() {
        ServerHttpRequest request = MockServerHttpRequest
                .get("http://example.com")
                .build();

        String baseUrl = BaseUrlExtractor.extractBaseUrl(request);

        assertThat(baseUrl).isEqualTo("http://example.com");
    }

    @Test
    void givenStandardHttpsPort_whenExtractBaseUrl_thenPortExcluded() {
        ServerHttpRequest request = MockServerHttpRequest
                .get("https://example.com")
                .build();

        String baseUrl = BaseUrlExtractor.extractBaseUrl(request);

        assertThat(baseUrl).isEqualTo("https://example.com");
    }

    @Test
    void givenNonStandardHttpPort_whenExtractBaseUrl_thenPortIncluded() {
        ServerHttpRequest request = MockServerHttpRequest
                .get("http://example.com:8080")
                .build();

        String baseUrl = BaseUrlExtractor.extractBaseUrl(request);

        assertThat(baseUrl).isEqualTo("http://example.com:8080");
    }

    @Test
    void givenNonStandardHttpsPort_whenExtractBaseUrl_thenPortIncluded() {
        ServerHttpRequest request = MockServerHttpRequest
                .get("https://example.com:8443")
                .build();

        String baseUrl = BaseUrlExtractor.extractBaseUrl(request);

        assertThat(baseUrl).isEqualTo("https://example.com:8443");
    }

    @Test
    void givenNoPortSpecified_whenExtractBaseUrl_thenPortExcluded() {
        ServerHttpRequest request = MockServerHttpRequest
                .get("http://example.com")
                .build();

        String baseUrl = BaseUrlExtractor.extractBaseUrl(request);

        assertThat(baseUrl).isEqualTo("http://example.com");
    }

    @Test
    void givenHttpsWithPort80_whenExtractBaseUrl_thenPortIncluded() {
        ServerHttpRequest request = MockServerHttpRequest
                .get("https://example.com:80")
                .build();

        String baseUrl = BaseUrlExtractor.extractBaseUrl(request);

        assertThat(baseUrl).isEqualTo("https://example.com:80");
    }

    @Test
    void givenHttpWithPort443_whenExtractBaseUrl_thenPortIncluded() {
        ServerHttpRequest request = MockServerHttpRequest
                .get("http://example.com:443")
                .build();

        String baseUrl = BaseUrlExtractor.extractBaseUrl(request);

        assertThat(baseUrl).isEqualTo("http://example.com:443");
    }

    @Test
    void givenNullRequest_whenExtractBaseUrl_thenThrowException() {
        ServerHttpRequest request = null;

        assertThatThrownBy(() -> BaseUrlExtractor.extractBaseUrl(request))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("request");
    }


    @Test
    void givenSomeLongPath_whenExtractBaseUrl_thenOnlyBaseUrlIsIncluded() {
        ServerHttpRequest request = MockServerHttpRequest
                .get("http://example.com/somePath/1234")
                .build();

        String baseUrl = BaseUrlExtractor.extractBaseUrl(request);

        assertThat(baseUrl).isEqualTo("http://example.com");
    }

    @Test
    void givenStandardHttpPort_whenExtractBaseUrlFromExchange_thenPortExcluded() {
        MockServerHttpRequest request = MockServerHttpRequest
                .get("http://example.com")
                .build();

        ServerWebExchange exchange = MockServerWebExchange.from(request);

        String baseUrl = extractBaseUrl(exchange);

        assertThat(baseUrl).isEqualTo("http://example.com");
    }
}
