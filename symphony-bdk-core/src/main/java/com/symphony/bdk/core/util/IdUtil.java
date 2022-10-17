package com.symphony.bdk.core.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apiguardian.api.API;

import java.util.Base64;

/**
 * Helper class providing Base64 id conversion. Useful for stream or message ids.
 */
@API(status = API.Status.EXPERIMENTAL)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IdUtil {

  private static final char[] NOT_URL_SAFE_BASE_64_CHARS = new char[] {'+', '/', '='};

  /**
   * Convert the Base64 id to its URL-safe Base64 version if it contains non URL-safe characters.
   *
   * @param base64Id Base64 id.
   * @return Base64 URL-safe id.
   */
  public static String toUrlSafeIdIfNeeded(String base64Id) {
    if (StringUtils.containsAny(base64Id, NOT_URL_SAFE_BASE_64_CHARS)) {
      return toUrlSafeId(base64Id);
    } else {
      return base64Id;
    }
  }

  /**
   * Convert the stream id to the corresponding URL-safe encoded stream id.
   *
   * <p>Example of usage:
   * <pre>{@code
   *   String urlSafeStreamId = StreamUtil.toUrlSafeStreamId("XlU3OH9eVMzq+yss7M/xyn///oxwgbtGbQ==");
   * }</pre></p>
   */
  public static String toUrlSafeId(String base64Id) {
    byte[] decodedURLBytes = Base64.getDecoder().decode(base64Id);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(decodedURLBytes);
  }

  /**
   * Convert the URL-safe encoded stream id to the corresponding original stream id.
   *
   * <p>Example of usage:
   * <pre>{@code
   *   String streamId = StreamUtil.fromUrlSafeStreamId("XlU3OH9eVMzq-yss7M_xyn___oxwgbtGbQ");
   * }</pre></p>
   */
  public static String fromUrlSafeId(String urlSafeBase64Id) {
    byte[] decodedURLBytes = Base64.getUrlDecoder().decode(urlSafeBase64Id);
    return Base64.getEncoder().encodeToString(decodedURLBytes);
  }
}
