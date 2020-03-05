package com.symphony.bot.sdk.internal.sse.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * SSE event to be sent to client application
 *
 * @author Marcus Secato
 */
@Data
@Builder
@JsonInclude(Include.NON_NULL)
public class SseEvent {

  private String id;
  private String event;
  private Long retry;
  private Object data;
  @JsonIgnore
  private Map<String, String> metadata;

}
