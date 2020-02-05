package com.symphony.ms.bot.sdk.spreadsheet.service;

import com.symphony.ms.bot.sdk.internal.symphony.exception.SymphonyClientException;
import com.symphony.ms.bot.sdk.spreadsheet.model.RoomSpreadsheet;
import com.symphony.ms.bot.sdk.spreadsheet.model.SpreadsheetCell;
import com.symphony.ms.bot.sdk.spreadsheet.model.SpreadsheetRoom;

import java.util.List;
import java.util.Map;

/**
 * Service to control the spreadsheet
 *
 * @author Gabriel Berberian
 */
public interface SpreadsheetService {

  /**
   * Gets all spreadsheets
   *
   * @return all spreadsheets
   */
  Map<String, Object[][]> getSpreadsheets();

  /**
   * Gets the spreadsheet of a room
   *
   * @param streamId the room id
   * @return the room spreadsheet
   */
  Object[][] getSpreadsheet(String streamId);

  /**
   * Sets the spreadsheet of a room
   *
   * @param roomSpreadsheet the spreadsheet
   */
  void setSpreadsheet(RoomSpreadsheet roomSpreadsheet);

  /**
   * Puts a cell in a spreadsheet of a room
   *
   * @param cell     the updated cell
   * @param streamId the room id
   */
  void putCell(SpreadsheetCell cell, String streamId);

  /**
   * Gets the rooms that can have spreadsheet (bot's room), with a flag signing if the room already
   * have a spreadsheet
   *
   * @return a list with the bot room information and the "has spreadsheet" flags
   * @throws SymphonyClientException on failure getting the bot rooms
   */
  List<SpreadsheetRoom> getBotRooms() throws SymphonyClientException;
}
