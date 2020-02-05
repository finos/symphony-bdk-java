package com.symphony.ms.bot.sdk.spreadsheet.controller;

import com.symphony.ms.bot.sdk.internal.symphony.exception.SymphonyClientException;
import com.symphony.ms.bot.sdk.spreadsheet.model.RoomSpreadsheet;
import com.symphony.ms.bot.sdk.spreadsheet.model.SpreadsheetCell;
import com.symphony.ms.bot.sdk.spreadsheet.model.SpreadsheetRoom;
import com.symphony.ms.bot.sdk.spreadsheet.service.SpreadsheetService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

/**
 * The spreadsheet controller
 *
 * @author Gabriel Berberian
 */
@Controller
@RequestMapping(value = "/spreadsheet")
public class SpreadsheetController {

  private final SpreadsheetService spreadsheetService;

  public SpreadsheetController(SpreadsheetService spreadsheetService) {
    this.spreadsheetService = spreadsheetService;
  }

  /**
   * Gets all spreadsheets
   *
   * @return all spreadsheets
   */
  @GetMapping
  public ResponseEntity<Map<String, Object[][]>> getAllSpreadsheets() {
    Map<String, Object[][]> currentSpreadsheets = spreadsheetService.getSpreadsheets();
    if (currentSpreadsheets != null && !currentSpreadsheets.isEmpty()) {
      return ResponseEntity.ok().body(currentSpreadsheets);
    }
    return ResponseEntity.noContent().build();
  }

  /**
   * Gets a specific spreadsheet
   *
   * @param streamId the id of the stream the spreadsheet belongs to
   * @return the spreadsheet
   */
  @GetMapping("{streamId}")
  public ResponseEntity<Object[][]> getSpreadsheet(@PathVariable String streamId) {
    Object[][] currentSpreadsheet = spreadsheetService.getSpreadsheet(streamId);
    if (currentSpreadsheet != null) {
      return ResponseEntity.ok().body(currentSpreadsheet);
    }
    return ResponseEntity.noContent().build();
  }

  /**
   * Creates a new spreadsheet to a room. If the room already have a spreadsheet it is overwritten
   *
   * @param roomSpreadsheet the new spreadsheet
   * @return the response success
   */
  @PostMapping
  public ResponseEntity postSpreadsheet(@RequestBody RoomSpreadsheet roomSpreadsheet) {
    spreadsheetService.setSpreadsheet(roomSpreadsheet);
    return ResponseEntity.ok().build();
  }

  /**
   * Updates a spreadsheet
   *
   * @param cell     the cell to be updated
   * @param streamId the id of the room the spreadsheet belongs to
   * @return the response success
   */
  @PutMapping("{streamId}")
  public ResponseEntity putSpreadsheet(@RequestBody SpreadsheetCell cell,
      @PathVariable String streamId) {
    spreadsheetService.putCell(cell, streamId);
    return ResponseEntity.ok().build();
  }

  /**
   * Gets the rooms that can have a spreadsheet
   *
   * @return the rooms with a flag signing if they have a spreadsheet
   */
  @GetMapping("rooms")
  public ResponseEntity<List<SpreadsheetRoom>> getRooms() {
    List<SpreadsheetRoom> spreadsheetRooms;
    try {
      spreadsheetRooms = spreadsheetService.getBotRooms();
    } catch (SymphonyClientException e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    if (spreadsheetRooms != null && !spreadsheetRooms.isEmpty()) {
      return ResponseEntity.ok().body(spreadsheetRooms);
    }
    return ResponseEntity.noContent().build();
  }

}
