package org.kamillion.hateoflux.linkbuilder;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.kamillion.hateoflux.linkbuilder.SpringControllerLinkBuilder.linkTo;


class SpringControllerLinkBuilderTest {


    @Test
    void givenMethodOfControllerClassWithNoParameters_whenLinkToCalled_thenLinkIsOk() {
        // GIVEN & WHEN
        final String link = linkTo(DummyController.class, DummyController::funcWithGetMappingUrl);

        //THEN
        assertThat(link).isEqualTo("/dummy/getmapping-url");
    }

}