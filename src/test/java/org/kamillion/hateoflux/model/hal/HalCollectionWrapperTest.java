package org.kamillion.hateoflux.model.hal;

import org.junit.jupiter.api.Test;

class HalCollectionWrapperTest {

    @Test
    public void testEmpty() {
        HalCollectionWrapper<String, Integer> kj = HalCollectionWrapper.empty("kj");
    }

}