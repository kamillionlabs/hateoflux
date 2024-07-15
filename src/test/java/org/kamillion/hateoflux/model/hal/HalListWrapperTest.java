package org.kamillion.hateoflux.model.hal;

import org.junit.jupiter.api.Test;

class HalListWrapperTest {

    @Test
    public void testEmpty() {
        HalListWrapper<String, Integer> kj = HalListWrapper.empty("kj");
    }

}