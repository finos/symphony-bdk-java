package com.symphony.ms.bot.sdk.spreadsheet.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

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
  private List<SpreadsheetCell> cells;
}
