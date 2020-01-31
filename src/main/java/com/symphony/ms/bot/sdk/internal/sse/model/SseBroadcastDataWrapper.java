package com.symphony.ms.bot.sdk.internal.sse.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Wraps SSE broadcast data and event id
 *
 * @author Gabriel Berberian
 */
@Data
@AllArgsConstructor
public class SseBroadcastDataWrapper {

  private Object data;
  private long eventId;

}
