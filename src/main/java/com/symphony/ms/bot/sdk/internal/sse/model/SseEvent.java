package com.symphony.ms.bot.sdk.internal.sse.model;

import java.util.Map;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

  @JsonIgnore
  private Map<String, String> metadata;

  private Long retry;

}
