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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UriTemplateDataTest {

    @ParameterizedTest
    @CsvSource(delimiter = ';', value = {
            // uri | path parameters | query parameters | uri without query parameters

            //Without parameters
            "https://example.com; -; -; https://example.com",

            //With path parameters at the end
            "https://example.com/{var}; var; -; https://example.com/{var}",

            //With path parameters at the front
            "{var}/some-path/; var; -; {var}/some-path/",

            //With path parameters in the middle
            "/root/{var}/some-path/; var; -; /root/{var}/some-path/",

            //With path parameters at the front and at the end
            "{var1}/some-path/{var2}; var1|var2; -; {var1}/some-path/{var2}",

            //With path and query parameters
            "{var1}/some-path/{?var2}; var1; var2; {var1}/some-path/",

            //With multiple query parameters
            "/some-path/{?var1,var2}; -; var1|var2; /some-path/",

            //Single exploded query parameter
            "/some-path/{?var1*}; -; var1; /some-path/",
    })
    void givenTemplatedUri_whenConstructorCalled_ThenCorrectlyInitialize(String uri,
                                                                         String expectedPathParams,
                                                                         String expectedQueryParams,
                                                                         String expectedPathWithoutQueryParams) {
        //GIVEN
        String[] expectedPathParamsArray = expectedPathParams == null || "-".equals(expectedPathParams.trim())
                ? new String[]{} : expectedPathParams.split("\\|");
        String[] expectedQueryParamsArray = expectedQueryParams == null || "-".equals(expectedQueryParams.trim())
                ? new String[]{} : expectedQueryParams.split("\\|");

        //WHEN
        UriTemplateData actualTemplate = UriTemplateData.of(uri);

        //THEN
        assertThat(actualTemplate.getOriginalUriTemplate()).isEqualTo(uri);
        assertThat(actualTemplate.getPathParameterNames()).containsExactly(expectedPathParamsArray);
        assertThat(actualTemplate.getQueryParameterNames()).containsExactly(expectedQueryParamsArray);
        assertThat(actualTemplate.getUriTemplateWithoutQueryParameters()).isEqualTo(expectedPathWithoutQueryParams);
    }


    @Test
    void givenSpaceInQueryParameterListInTemplatedUri_whenConstructor_ThenThrowIllegalArgumentException() {
        //GIVEN
        String templateUri = "/some-path/{?var1, var2}";

        assertThatThrownBy(() -> {
            //WHEN
            UriTemplateData.of(templateUri);
            //THEN
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Leading or trailing whitespace in any query parameter is not allowed (also before or " +
                        "after a comma). Template was '" + templateUri + "'");
    }


}