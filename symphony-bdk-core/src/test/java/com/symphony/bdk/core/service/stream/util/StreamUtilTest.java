package com.symphony.bdk.core.service.stream.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class StreamUtilTest {

  @Test
  public void testToUrlSafeStreamId() {
    String streamId = "XlU3OH9eVMzq+yss7M/xyn///oxwgbtGbQ==";
    assertEquals("XlU3OH9eVMzq-yss7M_xyn___oxwgbtGbQ", StreamUtil.toUrlSafeStreamId(streamId));
  }

  @Test
  public void testToFromUrlSafeStreamId() {
    String streamId = "XlU3OH9eVMzq-yss7M_xyn___oxwgbtGbQ";
    assertEquals("XlU3OH9eVMzq+yss7M/xyn///oxwgbtGbQ==", StreamUtil.fromUrlSafeStreamId(streamId));
  }
}
