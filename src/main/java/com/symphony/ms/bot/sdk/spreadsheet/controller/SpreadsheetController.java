package com.symphony.ms.bot.sdk.spreadsheet.controller;

import com.symphony.ms.bot.sdk.internal.symphony.ConfigClient;
import com.symphony.ms.bot.sdk.internal.symphony.exception.SymphonyClientException;
import com.symphony.ms.bot.sdk.spreadsheet.model.RoomSpreadsheet;
import com.symphony.ms.bot.sdk.spreadsheet.model.SpreadsheetCell;
import com.symphony.ms.bot.sdk.spreadsheet.model.SpreadsheetRoom;
import com.symphony.ms.bot.sdk.spreadsheet.service.SpreadsheetService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

@RestController
public class SpreadsheetController {

  private static final String SPREADSHEET_PATH = "spreadsheet";

  private final SpreadsheetService spreadsheetService;
  private final String authPath;

  @Autowired
  @Qualifier("requestMappingHandlerMapping")
  private RequestMappingHandlerMapping handlerMapping;

  public SpreadsheetController(SpreadsheetService spreadsheetService, ConfigClient configClient) {
    this.spreadsheetService = spreadsheetService;
    this.authPath = configClient.getExtAppAuthPath();
  }

  @PostConstruct
  public void init() throws NoSuchMethodException {
    registerRoute(authPath.concat(SPREADSHEET_PATH));
  }

  private void registerRoute(String route) throws NoSuchMethodException {
    handlerMapping.registerMapping(
        RequestMappingInfo.paths(route)
            .methods(RequestMethod.GET)
            .build(),
        this,
        SpreadsheetController.class.getDeclaredMethod("getAllSpreadsheets", String.class));
    handlerMapping.registerMapping(
        RequestMappingInfo.paths(route.concat("/{streamId}"))
            .methods(RequestMethod.GET)
            .build(),
        this,
        SpreadsheetController.class.getDeclaredMethod("getSpreadsheet", String.class,
            String.class));
    handlerMapping.registerMapping(
        RequestMappingInfo.paths(route)
            .methods(RequestMethod.POST)
            .build(),
        this,
        SpreadsheetController.class.getDeclaredMethod("postSpreadsheet", String.class,
            RoomSpreadsheet.class));
    handlerMapping.registerMapping(
        RequestMappingInfo.paths(route.concat("/{streamId}"))
            .methods(RequestMethod.PUT)
            .build(),
        this,
        SpreadsheetController.class.getDeclaredMethod("putSpreadsheet", String.class,
            SpreadsheetCell.class, String.class));
    handlerMapping.registerMapping(
        RequestMappingInfo.paths(route.concat("/rooms"))
            .methods(RequestMethod.GET)
            .build(),
        this,
        SpreadsheetController.class.getDeclaredMethod("getRooms", String.class));
  }

  /**
   * Gets all spreadsheets
   *
   * @return all spreadsheets
   */
  public ResponseEntity<Map<String, Map>> getAllSpreadsheets(
      @RequestAttribute("userId") String userId) {
    Map<String, Map> currentSpreadsheets = spreadsheetService.getSpreadsheets();
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
  public ResponseEntity<Map> getSpreadsheet(@RequestAttribute("userId") String userId,
      @PathVariable String streamId) {
    Map currentSpreadsheet = spreadsheetService.getSpreadsheet(streamId);
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
  public ResponseEntity postSpreadsheet(@RequestAttribute("userId") String userId,
      @RequestBody RoomSpreadsheet roomSpreadsheet) {
    spreadsheetService.setSpreadsheet(roomSpreadsheet, userId);
    return ResponseEntity.ok().build();
  }

  /**
   * Updates a spreadsheet
   *
   * @param cell     the cell to be updated
   * @param streamId the id of the room the spreadsheet belongs to
   * @return the response success
   */
  public ResponseEntity putSpreadsheet(@RequestAttribute("userId") String userId,
      @RequestBody SpreadsheetCell cell, @PathVariable String streamId) {
    spreadsheetService.putCell(cell, streamId, userId);
    return ResponseEntity.ok().build();
  }

  /**
   * Gets the rooms that can have a spreadsheet
   *
   * @return the rooms with a flag signing if they have a spreadsheet
   */
  public ResponseEntity<List<SpreadsheetRoom>> getRooms(@RequestAttribute("userId") String userId) {
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
