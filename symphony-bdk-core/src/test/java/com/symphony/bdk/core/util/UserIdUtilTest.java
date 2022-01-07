package com.symphony.bdk.core.util;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UserIdUtilTest {

  @Test
  void shouldExtractTenantId() {
    final int tenantId = 189;
    final long userId = 12987981103203L;
    assertThat(UserIdUtil.extractTenantId(userId)).isEqualTo(tenantId);
  }

  @Test
  void testIllegalSegmentsSize() {
    IllegalArgumentException ex =
        assertThrows(IllegalArgumentException.class, () -> new UserIdUtil.LongUtil(32, 32, 32));
    assertThat(ex).hasMessage("total size is larger than the bit-count of a long");
  }
}
