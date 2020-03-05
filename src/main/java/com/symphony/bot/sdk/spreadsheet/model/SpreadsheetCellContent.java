package com.symphony.bot.sdk.spreadsheet.model;

import lombok.Builder;
import lombok.Data;

/**
 * Model representing the content of a spreadsheet cell
 *
 * @author Gabriel Berberian
 */
@Data
@Builder
public class SpreadsheetCellContent {

  private Object value;
  private Object expr;
}
