package com.symphony.ms.bot.sdk.spreadsheet.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Model that relates a spreadsheet cell with the spreadsheet it belongs to
 *
 * @author Gabriel Berberian
 */
@Data
@AllArgsConstructor
public class RoomSpreadsheetCell {

  private String streamId;
  private SpreadsheetCell spreadsheetCell;
}
