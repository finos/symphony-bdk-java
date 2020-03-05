package com.symphony.bot.sdk.spreadsheet.model;

import lombok.Data;

import java.util.Map;

/**
 * Model that relates spreadsheet with the room it belongs to
 *
 * @author Gabriel Berberian
 */
@Data
public class RoomSpreadsheet {

  private String streamId;
  private Map spreadsheet;
}
