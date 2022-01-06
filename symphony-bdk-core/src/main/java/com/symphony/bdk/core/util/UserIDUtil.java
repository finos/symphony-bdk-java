package com.symphony.bdk.core.util;

import org.apiguardian.api.API;

/**
 * Used to extract the tenant ID from a user ID.
 * <p>
 * The user ID is a combination of a unique tenant ID, and a unique sub-tenant ID, combined into a long.
 * The tenant ID is stored in the 27 highest bits (minus the sign bit which is unused so that all IDs remain a positive value)
 * which allows for 134 million pods.
 * This leaves 36 lowest bits for the user ID, which allows 68.7 billion users per tenant.
 */
@API(status = API.Status.STABLE)
public class UserIDUtil {

  private static final int TENANT_ID_BIT_LENGTH = 27;
  private static final int SUBTENANT_ID_BIT_LENGTH = 36;
  private static final int TENANT_ID_INDEX = 1;
  private static final int SUBTENANT_ID_INDEX = 0;

  private static final LongUtil USERID_UTIL = new LongUtil(SUBTENANT_ID_BIT_LENGTH, TENANT_ID_BIT_LENGTH);

  public static int extractTenantId(Long userId) {
    return (int) USERID_UTIL.extract(userId, TENANT_ID_INDEX);
  }

  @API(status = API.Status.INTERNAL)
  static class LongUtil {

    private final Segment[] segments;

    public LongUtil(int... sizes) {
      this.segments = new Segment[sizes.length];
      short totalSize = 0;
      short shift = 0;

      for (int i = 0; i < sizes.length; ++i) {
        short size = (short) sizes[i];
        Segment segment = new Segment(size, shift);
        shift += size;
        this.segments[i] = segment;
        totalSize += size;
      }

      if (totalSize > 64) {
        throw new IllegalArgumentException("total size is larger than the bit-count of a long");
      }
    }

    public long extract(long value, int index) {
      Segment s = this.segments[index];
      return value >> s.shift & s.mask;
    }

    @API(status = API.Status.INTERNAL)
    static class Segment {

      private final long mask;
      private final short shift;

      Segment(short size, short shift) {
        long l = 0L;

        for (int i = 0; i < size; ++i) {
          l |= 1L;
          l <<= 1;
        }

        l >>= 1;
        this.mask = l;
        this.shift = shift;
      }
    }
  }
}
