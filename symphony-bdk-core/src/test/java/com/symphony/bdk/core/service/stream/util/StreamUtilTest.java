package com.symphony.bdk.core.service.stream.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class StreamUtilTest {

  @Test
  void testToUrlSafeStreamId() {
    String streamId = "XlU3OH9eVMzq+yss7M/xyn///oxwgbtGbQ==";
    assertEquals("XlU3OH9eVMzq-yss7M_xyn___oxwgbtGbQ", StreamUtil.toUrlSafeStreamId(streamId));
  }

  @Test
  void testToFromUrlSafeStreamId() {
    String streamId = "XlU3OH9eVMzq-yss7M_xyn___oxwgbtGbQ";
    assertEquals("XlU3OH9eVMzq+yss7M/xyn///oxwgbtGbQ==", StreamUtil.fromUrlSafeStreamId(streamId));
  }

  @Test
  void testToUrlSafeId() {
    assertEquals("XlU3OH9eVMzq-yss7M_xyn___oxwgbtGbQ",
        StreamUtil.toUrlSafeId("XlU3OH9eVMzq+yss7M/xyn///oxwgbtGbQ=="));

    // doing it twice does change it
    assertEquals("XlU3OH9eVMzq-yss7M_xyn___oxwgbtGbQ",
        StreamUtil.toUrlSafeId(StreamUtil.toUrlSafeId("XlU3OH9eVMzq+yss7M/xyn///oxwgbtGbQ==")));

    assertEquals("alreadybase64",
        StreamUtil.toUrlSafeId(StreamUtil.toUrlSafeId("alreadybase64")));
  }
}
