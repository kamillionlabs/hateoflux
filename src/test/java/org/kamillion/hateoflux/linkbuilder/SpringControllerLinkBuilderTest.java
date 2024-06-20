package org.kamillion.hateoflux.linkbuilder;

import org.junit.jupiter.api.Test;
import org.kamillion.hateoflux.model.Link;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.kamillion.hateoflux.linkbuilder.SpringControllerLinkBuilder.linkTo;


class SpringControllerLinkBuilderTest {


    @Test
    void givenGetMappingSimple_whenLinkTo_thenLinkIsFull() {
        // GIVEN & WHEN
        final Link link = linkTo(DummyController.class, DummyController::getMappingSimple);

        //THEN
        assertThat(link.getHref()).isEqualTo("/dummy/getmapping-url");
    }

    @Test
    void givenPostMappingWithParameter_whenLinkTo_thenParameterIsSubstituted() {
        // GIVEN & WHEN
        String someUuid = "00000000-0000-0000-0000-000000000000";
        final Link link = linkTo(DummyController.class, c -> c.postMappingWithParameter(UUID.fromString(someUuid)));

        //THEN
        assertThat(link.getHref()).isEqualTo("/dummy/" + someUuid);
    }

    @Test
    void givenPostMappingWithParameterAndSlash_whenLinkTo_thenLinkHasTrailingSlash() {
        // GIVEN & WHEN
        String someUuid = "00000000-0000-0000-0000-000000000000";
        final Link link = linkTo(DummyController.class,
                c -> c.postMappingWithParameterAndSlash(UUID.fromString(someUuid)));

        //THEN
        assertThat(link.getHref()).isEqualTo("/dummy/" + someUuid + "/");
    }

    @Test
    void givenPutMappingWithParameterAsSubresource_whenLinkTo_thenParameterIsSubstituted() {
        // GIVEN & WHEN
        String someUuid = "00000000-0000-0000-0000-000000000000";
        final Link link = linkTo(DummyController.class,
                c -> c.putMappingWithParameterAsSubresource(UUID.fromString(someUuid)));

        //THEN
        assertThat(link.getHref()).isEqualTo("/dummy/" + someUuid + "/subresource");
    }

    @Test
    void givenRequestPutMappingAWithQueryParametersWithAllArgs_whenLinkTo_thenBothQueryParametersAreSet() {
        // GIVEN & WHEN
        final Link link = linkTo(DummyController.class, c -> c.requestPutMappingWithQueryParameters(3, "foo"));

        //THEN
        assertThat(link.getHref()).isEqualTo("/dummy/request?size=3&name=foo");
    }

    @Test
    void givenRequestPutMappingWithQueryParametersWithRequiredOnly_whenLinkTo_thenOnlyOneQueryParameterIsSet() {
        // GIVEN & WHEN
        final Link link = linkTo(DummyController.class, c -> c.requestPutMappingWithQueryParameters(null, "foo"));

        //THEN
        assertThat(link.getHref()).isEqualTo("/dummy/request?name=foo");
    }

    @Test
    void givenRequestPutMappingWithQueryParametersAndSlash_whenLinkTo_thenLinkHasTrailingSlashBeforeQuery() {
        // GIVEN & WHEN
        final Link link = linkTo(DummyController.class, c -> c.requestPutMappingWithQueryParametersAndSlash(3, "foo"));

        //THEN
        assertThat(link.getHref()).isEqualTo("/dummy/request/?size=3&name=foo");
    }

    @Test
    void givenPatchMappingWithParameterAsSubresourceAndQueryParameter_whenLinkTo_thenPathAndQueryParameterAreSubstituted() {
        // GIVEN & WHEN
        String someUuid = "00000000-0000-0000-0000-000000000000";
        final Link link = linkTo(DummyController.class,
                c -> c.patchMappingWithParameterAsSubresourceAndQueryParameter(UUID.fromString(someUuid), "bar"));

        //THEN
        assertThat(link.getHref()).isEqualTo("/dummy/" + someUuid + "/subresource?name=bar");
    }

    @Test
    void givenPostMappingWithParameterAndCustomName_whenLinkTo_thenSubstitutionWorks() {
        // GIVEN & WHEN
        String name = "foo";
        final Link link = linkTo(DummyController.class,
                c -> c.postMappingWithParameterAndCustomName(name));

        //THEN
        assertThat(link.getHref()).isEqualTo("/dummy/" + name);
    }

    @Test
    void givenRequestPutMappingWithQueryParameterAndCustomName_whenLinkTo_thenCustomNameIsUsed() {
        // GIVEN & WHEN
        final Link link = linkTo(DummyController.class,
                c -> c.requestPutMappingWithQueryParameterAndCustomName(3));

        //THEN
        assertThat(link.getHref()).isEqualTo("/dummy/request?customSize=3");
    }


}