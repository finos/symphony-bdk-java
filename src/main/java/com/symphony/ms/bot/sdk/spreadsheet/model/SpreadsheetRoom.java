package com.symphony.ms.bot.sdk.spreadsheet.model;

import com.symphony.ms.bot.sdk.internal.symphony.model.SymphonyStream;

import lombok.Builder;
import lombok.Data;

/**
 * Model signing if a room has a spreadsheet
 *
 * @author Gabriel Berberian
 */
@Data
@Builder
public class SpreadsheetRoom {

  private SymphonyStream stream;
  private boolean hasSpreadsheet;
}
