package com.symphony.ms.bot.sdk.spreadsheet.service;

import com.symphony.ms.bot.sdk.spreadsheet.model.SpreadsheetCell;
import com.symphony.ms.bot.sdk.sse.SpreadsheetPublisher;

import org.springframework.stereotype.Service;

/**
 * The service that manages the shared spreadsheet
 *
 * @author Gabriel Berberian
 */
@Service
public class SpreadsheetServiceImpl implements SpreadsheetService {

  private final SpreadsheetPublisher spreadsheetPublisher;

  private String[][] currentSpreadsheet;

  public SpreadsheetServiceImpl(SpreadsheetPublisher spreadsheetPublisher) {
    this.spreadsheetPublisher = spreadsheetPublisher;
  }

  @Override
  public String[][] getCurrentSpreadsheet() {
    return currentSpreadsheet;
  }

  @Override
  public void setCurrentSpreadsheet(String[][] spreadsheet) {
    currentSpreadsheet = spreadsheet;
    spreadsheetPublisher.broadcast(spreadsheet);
  }

  @Override
  public void putCell(SpreadsheetCell cell) {
    if (currentSpreadsheet != null) {
      currentSpreadsheet[cell.getLine()][cell.getColumn()] = cell.getValue();
      spreadsheetPublisher.broadcast(cell);
    }
  }

}
