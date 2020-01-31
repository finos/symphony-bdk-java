package com.symphony.ms.bot.sdk.spreadsheet.model;

import lombok.Data;

/**
 * Model representing a spreadsheet cell
 *
 * @author Gabriel Berberian
 */
@Data
public class SpreadsheetCell {

  private int line;
  private int column;
  private String value;
}
