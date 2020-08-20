package com.symphony.bdk.core.service.datafeed;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DatafeedVersionTest {

    DatafeedVersion version;

    @Test
    void datafeedVersionTest() {
        version = DatafeedVersion.of("v2");
        assertEquals(version, DatafeedVersion.V2);
        version = DatafeedVersion.of("V2");
        assertEquals(version, DatafeedVersion.V2);
        version = DatafeedVersion.of("all_others_values");
        assertEquals(version, DatafeedVersion.V1);
    }
}
