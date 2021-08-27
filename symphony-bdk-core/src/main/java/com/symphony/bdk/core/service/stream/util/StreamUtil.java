package com.symphony.bdk.core.service.stream.util;

import com.symphony.bdk.core.util.IdUtil;

import lombok.AccessLevel;
import lombok.Generated;
import lombok.NoArgsConstructor;
import org.apiguardian.api.API;

/**
 * Helper class providing Base64 id conversion. Preserved to avoid breaking changes for external users of this class.
 *
 * @deprecated Use {@link com.symphony.bdk.core.util.IdUtil} instead.
 */
@Deprecated
@Generated
@API(status = API.Status.EXPERIMENTAL)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StreamUtil {

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
   * @deprecated Use {@link com.symphony.bdk.core.util.IdUtil#toUrlSafeId(String)} instead.
   */
  public static String toUrlSafeStreamId(String streamId) {
    return IdUtil.toUrlSafeIdIfNeeded(streamId);
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
   * @deprecated Use {@link com.symphony.bdk.core.util.IdUtil#fromUrlSafeId(String)} instead.
   */
  public static String fromUrlSafeStreamId(String streamId) {
    return IdUtil.fromUrlSafeId(streamId);
  }
}
