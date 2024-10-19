package de.kamillionlabs.hateoflux.linkbuilder;

import de.kamillionlabs.hateoflux.dummy.controller.DummyController;
import de.kamillionlabs.hateoflux.dummy.controller.DummyControllerWithMemberVar;
import de.kamillionlabs.hateoflux.model.link.Link;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static de.kamillionlabs.hateoflux.linkbuilder.SpringControllerLinkBuilder.linkTo;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


class SpringControllerLinkBuilderTest {

    @Test
    void givenControllerWithNoDefaultConstructor_whenLinkToCalled_thenReturnLinkWithoutException() {
        //GIVEN
        Link link = linkTo(DummyControllerWithMemberVar.class, c -> c.convertTheNumber(123));

        //THEN
        assertThat(link.getHref()).isEqualTo("/dummy-member-var/123");

    }


    @ParameterizedTest
    @NullAndEmptySource
    void givenPostMappingWithCompositeCollectionAsQueryParameter_whenProvidedEmptyOrNullValues_thenLinkUnchanged(List<String> args) {
        //GIVEN & WHEN
        Link link = linkTo(DummyController.class, c -> c.postMappingWithCollectionAsQueryParameter(args));

        //THEN
        assertThat(link.getHref()).isEqualTo("/dummy/names");
    }

    @ParameterizedTest
    @CsvSource(delimiter = ';', value = { //
            "val1; ?names=val1",
            "val1|val2; ?names=val1&names=val2"
    })
    void givenPostMappingWithCompositeCollectionAsQueryParameter_whenProvidedValues_thenLinkIsCorrect(
            String args, String expectedQueryParameters) {
        //GIVEN & WHEN
        List<String> argsAsList = Arrays.stream(args.split("\\|")).toList();
        Link link = linkTo(DummyController.class,
                c -> c.postMappingWithCompositeCollectionAsQueryParameter(argsAsList));

        //THEN
        assertThat(link.getHref()).isEqualTo("/dummy/names" + expectedQueryParameters);
    }


    @ParameterizedTest
    @NullAndEmptySource
    void givenPostMappingWithCollectionAsQueryParameter_whenProvidedEmptyOrNullValues_thenLinkUnchanged(List<String> args) {
        //GIVEN & WHEN
        Link link = linkTo(DummyController.class, c -> c.postMappingWithCollectionAsQueryParameter(args));

        //THEN
        assertThat(link.getHref()).isEqualTo("/dummy/names");
    }

    @ParameterizedTest
    @CsvSource(delimiter = ';', value = { //
            "val1; ?names=val1",
            "val1|val2; ?names=val1,val2"
    })
    void givenPostMappingWithCollectionAsQueryParameter_whenProvidedValues_thenLinkIsCorrect(
            String args, String expectedQueryParameters) {
        //GIVEN & WHEN
        List<String> argsAsList = Arrays.stream(args.split("\\|")).toList();
        Link link = linkTo(DummyController.class, c -> c.postMappingWithCollectionAsQueryParameter(argsAsList));

        //THEN
        assertThat(link.getHref()).isEqualTo("/dummy/names" + expectedQueryParameters);
    }


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


    @Test
    void givenpostMappingWithVoidAsReturnValue_whenLinkTo_noExceptionIsThrownAndLinkIsCorrect() {
        // GIVEN & WHEN
        final Link link = linkTo(DummyController.class,
                DummyController::postMappingWithVoidAsReturnValue);

        //THEN
        assertThat(link.getHref()).isEqualTo("/dummy/void-of-nothing");
    }


}