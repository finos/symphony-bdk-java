package com.symphony.bdk.core.service.stream.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apiguardian.api.API;

import java.util.Base64;

/**
 * Helper class providing stream id conversion
 */
@API(status = API.Status.EXPERIMENTAL)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StreamUtil {

  /**
   * Convert the stream id to the corresponding URLSafe encoded stream id
   *
   * @param streamId of the stream to be parsed
   * @return stream id after conversion
   *
   * <pre>{@code
   *   String urlSafeStreamId = StreamUtil.toUrlSafeStreamId("XlU3OH9eVMzq+yss7M/xyn///oxwgbtGbQ==");
   * }</pre>
   */
  public static String toUrlSafeStreamId(String streamId) {
    byte[] decodedURLBytes = Base64.getDecoder().decode(streamId);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(decodedURLBytes);
  }

  /**
   * Convert the URLSafe encoded stream id to the corresponding original stream id
   *
   * @param streamId of the stream to be parsed
   * @return stream id after conversion
   *
   * <pre>{@code
   *   String streamId = StreamUtil.fromUrlSafeStreamId("XlU3OH9eVMzq-yss7M_xyn___oxwgbtGbQ");
   * }</pre>
   */
  public static String fromUrlSafeStreamId(String streamId) {
    byte[] decodedURLBytes = Base64.getUrlDecoder().decode(streamId);
    return Base64.getEncoder().encodeToString(decodedURLBytes);
  }
}
