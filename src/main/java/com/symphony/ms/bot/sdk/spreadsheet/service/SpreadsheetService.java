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
  Map<String, Map> getSpreadsheets();

  /**
   * Gets the spreadsheet of a room
   *
   * @param streamId the room id
   * @return the room spreadsheet
   */
  Map getSpreadsheet(String streamId);

  /**
   * Sets the spreadsheet of a room
   *
   * @param roomSpreadsheet the spreadsheet
   * @param userId          the id of the user that is reseting the spreadsheet
   */
  void addSpreadsheet(RoomSpreadsheet roomSpreadsheet, String userId);

  /**
   * Puts a cells in a spreadsheet of a room
   *
   * @param cells    the updated cells
   * @param streamId the room id
   * @param userId   the id of the user that is updating the spreadsheet cells
   */
  void putCells(List<SpreadsheetCell> cells, String streamId, String userId);

  /**
   * Gets the rooms that can have spreadsheet (bot's room), with a flag signing if the room already
   * have a spreadsheet
   *
   * @return a list with the bot room information and the "has spreadsheet" flags
   * @throws SymphonyClientException on failure getting the bot rooms
   */
  List<SpreadsheetRoom> getBotRooms() throws SymphonyClientException;
}
