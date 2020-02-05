package com.symphony.ms.bot.sdk.spreadsheet.model;

import lombok.Data;

/**
 * Model that relates spreadsheet with the room it belongs to
 *
 * @author Gabriel Berberian
 */
@Data
public class RoomSpreadsheet {

  private String streamId;
  private Object[][] spreadsheet;
}
