package com.symphony.ms.bot.sdk.spreadsheet.model;

import lombok.Builder;
import lombok.Data;

/**
 * Model to represent spreadsheet update action
 *
 * @author Gabriel Berberian
 */
@Data
@Builder
public class SpreadsheetUpdateEvent {

  private String userId;
  private String streamId;
  private String key;
  private Object value;
  private Object expr;
}
