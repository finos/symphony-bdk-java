package com.symphony.ms.bot.sdk.spreadsheet.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * Model to represent spreadsheet reset action
 *
 * @author Gabriel Berberian
 */
@Data
@Builder
public class SpreadsheetResetEvent {

  private String userId;
  private String streamId;
  private Map spreadsheet;
}
