package com.symphony.bdk.core.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class IdUtilTest {

  @Test
  void testToUrlSafeId() {
    String streamId = "XlU3OH9eVMzq+yss7M/xyn///oxwgbtGbQ==";
    Assertions.assertEquals("XlU3OH9eVMzq-yss7M_xyn___oxwgbtGbQ", IdUtil.toUrlSafeId(streamId));
  }

  @Test
  void testToFromUrlSafeId() {
    String streamId = "XlU3OH9eVMzq-yss7M_xyn___oxwgbtGbQ";
    assertEquals("XlU3OH9eVMzq+yss7M/xyn///oxwgbtGbQ==", IdUtil.fromUrlSafeId(streamId));
  }

  @Test
  void testToUrlSafeIdIfNeeded() {
    assertEquals("XlU3OH9eVMzq-yss7M_xyn___oxwgbtGbQ",
        IdUtil.toUrlSafeIdIfNeeded("XlU3OH9eVMzq+yss7M/xyn///oxwgbtGbQ=="));

    // doing it twice does change it
    assertEquals("XlU3OH9eVMzq-yss7M_xyn___oxwgbtGbQ",
        IdUtil.toUrlSafeIdIfNeeded(IdUtil.toUrlSafeIdIfNeeded("XlU3OH9eVMzq+yss7M/xyn///oxwgbtGbQ==")));

    assertEquals("alreadybase64",
        IdUtil.toUrlSafeIdIfNeeded(IdUtil.toUrlSafeIdIfNeeded("alreadybase64")));
  }
}
