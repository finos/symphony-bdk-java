package com.symphony.ms.bot.sdk.spreadsheet.service;

import com.symphony.ms.bot.sdk.spreadsheet.model.SpreadsheetCell;

/**
 * Service to control the spreadsheet
 *
 * @author Gabriel Berberian
 */
public interface SpreadsheetService {

  /**
   * Get the current spreadsheet
   *
   * @return the current spreadsheet
   */
  String[][] getCurrentSpreadsheet();

  /**
   * Set the current spreadsheet
   *
   * @param spreadsheet
   */
  void setCurrentSpreadsheet(String[][] spreadsheet);

  /**
   * Put a cell into the current spreadsheet
   *
   * @param cell
   */
  void putCell(SpreadsheetCell cell);

}
