package com.symphony.ms.bot.sdk.internal.sse.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;
import lombok.Data;

/**
 * SSE event to be sent to client application
 *
 * @author Marcus Secato
 *
 */
@Data
@Builder
@JsonInclude(Include.NON_NULL)
public class SseEvent {

  private String id;

  private String event;

  private Object data;

  private Long retry;

}
