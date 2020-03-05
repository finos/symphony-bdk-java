package com.symphony.bot.sdk.spreadsheet.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.symphony.bot.sdk.internal.sse.model.SsePublishable;
import com.symphony.bot.sdk.internal.symphony.model.SymphonyUser;

import lombok.Builder;
import lombok.Data;

/**
 * Model to represent spreadsheet events
 *
 * @author Gabriel Berberian
 */
@Data
@Builder
@JsonInclude(Include.NON_NULL)
public class SpreadsheetEvent implements SsePublishable {

  private String userId;
  private String streamId;
  private List<SpreadsheetCell> cells;
  private SymphonyUser user;
  @JsonIgnore
  private String type;
  @JsonIgnore
  private String id;
}
