package org.kamillion.hateoflux.linkbuilder;

import org.junit.jupiter.api.Test;
import org.kamillion.hateoflux.model.Link;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.kamillion.hateoflux.linkbuilder.SpringControllerLinkBuilder.linkTo;


class SpringControllerLinkBuilderTest {


    @Test
    void givenMethodOfControllerClassWithNoParameters_whenLinkToCalled_thenLinkIsOk() {
        // GIVEN & WHEN
        final Link link = linkTo(DummyController.class, DummyController::getMappingUrl);

        //THEN
        assertThat(link.getHref()).isEqualTo("/dummy/getmapping-url");
    }

}