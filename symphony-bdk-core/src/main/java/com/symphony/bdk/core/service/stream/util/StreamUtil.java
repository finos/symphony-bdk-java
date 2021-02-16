package com.symphony.bdk.core.service.stream.util;

import org.apiguardian.api.API;

import java.util.Base64;

/**
 * Helper class providing stream id conversion
 */
@API(status = API.Status.EXPERIMENTAL)
public class StreamUtil {

  private StreamUtil() {
  }

  /**
   * Convert the stream id to the corresponding URLSafe encoded stream id
   *
   * @param streamId of the stream to be parsed
   * @return stream id after conversion
   */
  public static String toUrlSafeStreamId(String streamId) {
    if (streamId == null || streamId.isEmpty()) {
      return "";
    }
    return streamId.trim()
        .replaceAll("[=]+$", "")
        .replaceAll("\\+", "-")
        .replaceAll("/", "_");
  }

  /**
   * Convert the URLSafe encoded stream id to the corresponding original stream id
   *
   * @param streamId of the stream to be parsed
   * @return stream id after conversion
   */
  public static String fromUrlSafeStreamId(String streamId) {
    if (streamId == null || streamId.isEmpty()) {
      return "";
    }
    byte[] decodedURLBytes = Base64.getUrlDecoder().decode(streamId);
    return Base64.getEncoder().encodeToString(decodedURLBytes);
  }
}
