package org.kamillion.hateoflux.linkbuilder;

import org.junit.jupiter.api.Test;
import org.kamillion.hateoflux.model.Link;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.kamillion.hateoflux.linkbuilder.SpringControllerLinkBuilder.linkTo;


class SpringControllerLinkBuilderTest {


    @Test
    void givenGetMappingSimple_whenLinkToCalled_thenLinkIsOk() {
        // GIVEN & WHEN
        final Link link = linkTo(DummyController.class, DummyController::getMappingSimple);

        //THEN
        assertThat(link.getHref()).isEqualTo("/dummy/getmapping-url");
    }

    @Test
    void givenPostMappingAndParameter_whenLinkToCalled_thenLinkIsOk() {
        // GIVEN & WHEN
        String someUuid = "00000000-0000-0000-0000-000000000000";
        final Link link = linkTo(DummyController.class, c -> c.postMappingAndParameter(UUID.fromString(someUuid)));

        //THEN
        assertThat(link.getHref()).isEqualTo("/dummy/" + someUuid);
    }

    @Test
    void givenPostMappingAndParameterAndSlash_whenLinkToCalled_thenLinkIsOk() {
        // GIVEN & WHEN
        String someUuid = "00000000-0000-0000-0000-000000000000";
        final Link link = linkTo(DummyController.class,
                c -> c.postMappingAndParameterAndSlash(UUID.fromString(someUuid)));

        //THEN
        assertThat(link.getHref()).isEqualTo("/dummy/" + someUuid + "/");
    }

    @Test
    void givenPutMappingAndParameterAsSubresource_whenLinkToCalled_thenLinkIsOk() {
        // GIVEN & WHEN
        String someUuid = "00000000-0000-0000-0000-000000000000";
        final Link link = linkTo(DummyController.class,
                c -> c.putMappingAndParameterAsSubresource(UUID.fromString(someUuid)));

        //THEN
        assertThat(link.getHref()).isEqualTo("/dummy/" + someUuid + "/subresource");
    }

    @Test
    void givenRequestPutMappingAWithQueryParametersWithAllArgs_whenLinkToCalled_thenLinkIsOk() {
        // GIVEN & WHEN
        final Link link = linkTo(DummyController.class, c -> c.requestPutMappingWithQueryParameters(3, "foo"));

        //THEN
        assertThat(link.getHref()).isEqualTo("/dummy/request?size=3&name=foo");
    }

    @Test
    void givenRequestPutMappingWithQueryParametersWithRequiredOnly_whenLinkToCalled_thenLinkIsOk() {
        // GIVEN & WHEN
        final Link link = linkTo(DummyController.class, c -> c.requestPutMappingWithQueryParameters(null, "foo"));

        //THEN
        assertThat(link.getHref()).isEqualTo("/dummy/request?name=foo");
    }

    @Test
    void givenRequestPutMappingWithQueryParametersAndSlash_whenLinkToCalled_thenLinkIsOk() {
        // GIVEN & WHEN
        final Link link = linkTo(DummyController.class, c -> c.requestPutMappingWithQueryParametersAndSlash(3, "foo"));

        //THEN
        assertThat(link.getHref()).isEqualTo("/dummy/request/?size=3&name=foo");
    }

    @Test
    void givenRequestPatchMappingAndParameterAsSubresourceAndQueryParameter_whenLinkToCalled_thenLinkIsOk() {
        // GIVEN & WHEN
        String someUuid = "00000000-0000-0000-0000-000000000000";
        final Link link = linkTo(DummyController.class,
                c -> c.patchMappingAndParameterAsSubresourceAndQueryParameter(UUID.fromString(someUuid), "bar"));

        //THEN
        assertThat(link.getHref()).isEqualTo("/dummy/" + someUuid + "/subresource?name=bar");
    }

}