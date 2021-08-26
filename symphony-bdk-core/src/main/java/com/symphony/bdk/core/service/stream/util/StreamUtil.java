package com.symphony.bdk.core.service.stream.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apiguardian.api.API;

import java.util.Base64;

/**
 * Helper class providing Base64 id conversion.
 */
@API(status = API.Status.EXPERIMENTAL)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StreamUtil {

  private static final char[] NOT_URL_SAFE_BASE_64_CHARS = new char[] {'+', '/', '='};

  /**
   * Convert the Base64 id to its URL-safe Base64 version. Useful for stream or message ids.
   *
   * @param id Base64 id.
   * @return Base64 URL-safe id.
   */
  public static String toUrlSafeId(String id) {
    if (StringUtils.containsAny(id, NOT_URL_SAFE_BASE_64_CHARS)) {
      return toUrlSafeStreamId(id);
    } else {
      return id;
    }
  }

  /**
   * Convert the stream id to the corresponding URL-safe encoded stream id.
   *
   * <p>Example of usage:
   * <pre>{@code
   *   String urlSafeStreamId = StreamUtil.toUrlSafeStreamId("XlU3OH9eVMzq+yss7M/xyn///oxwgbtGbQ==");
   * }</pre></p>
   *
   * @param streamId of the stream to be parsed
   * @return stream id after conversion
   */
  public static String toUrlSafeStreamId(String streamId) {
    byte[] decodedURLBytes = Base64.getDecoder().decode(streamId);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(decodedURLBytes);
  }

  /**
   * Convert the URL-safe encoded stream id to the corresponding original stream id.
   *
   * <p>Example of usage:
   * <pre>{@code
   *   String streamId = StreamUtil.fromUrlSafeStreamId("XlU3OH9eVMzq-yss7M_xyn___oxwgbtGbQ");
   * }</pre></p>
   *
   * @param streamId of the stream to be parsed
   * @return stream id after conversion
   */
  public static String fromUrlSafeStreamId(String streamId) {
    byte[] decodedURLBytes = Base64.getUrlDecoder().decode(streamId);
    return Base64.getEncoder().encodeToString(decodedURLBytes);
  }
}
