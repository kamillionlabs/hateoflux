package de.kamillionlabs.hateoflux.model.hal;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class HalEmbeddedWrapperTest {

    @Test
    void givenString_whenWrapping_thenThrowsException() {
        assertThatThrownBy(() -> HalEmbeddedWrapper.wrap("hello"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Embedded is not allowed to be of type scalar (e.g. String, int, etc.)");
    }

    @Test
    void givenCharacter_whenWrapping_thenThrowsException() {
        assertThatThrownBy(() -> HalEmbeddedWrapper.wrap('h'))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Embedded is not allowed to be of type scalar (e.g. String, int, etc.)");
    }

    @Test
    void givenPrimitiveInt_whenWrapping_thenThrowsException() {
        assertThatThrownBy(() -> HalEmbeddedWrapper.wrap(3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Embedded is not allowed to be of type scalar (e.g. String, int, etc.)");
    }

    @Test
    void givenBigDecimal_whenWrapping_thenThrowsException() {
        assertThatThrownBy(() -> HalEmbeddedWrapper.wrap(new BigDecimal("4536433.58")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Embedded is not allowed to be of type scalar (e.g. String, int, etc.)");
    }

    @Test
    void givenBoolean_whenWrapping_thenThrowsException() {
        assertThatThrownBy(() -> HalEmbeddedWrapper.wrap(false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Embedded is not allowed to be of type scalar (e.g. String, int, etc.)");
    }
}