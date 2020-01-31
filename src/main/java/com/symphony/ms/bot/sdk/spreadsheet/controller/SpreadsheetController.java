package com.symphony.ms.bot.sdk.spreadsheet.controller;

import com.symphony.ms.bot.sdk.spreadsheet.model.SpreadsheetCell;
import com.symphony.ms.bot.sdk.spreadsheet.service.SpreadsheetService;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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

  @GetMapping
  public ResponseEntity<String[][]> getSpreadsheet() {
    String[][] currentSpreadsheet = spreadsheetService.getCurrentSpreadsheet();
    if (currentSpreadsheet != null) {
      return ResponseEntity.ok().body(currentSpreadsheet);
    }
    return ResponseEntity.noContent().build();
  }

  @PostMapping
  public ResponseEntity postSpreadsheet(@RequestBody String[][] spreadSheet) {
    spreadsheetService.setCurrentSpreadsheet(spreadSheet);
    return ResponseEntity.ok().build();
  }

  @PutMapping
  public ResponseEntity putSpreadsheet(@RequestBody SpreadsheetCell cell) {
    spreadsheetService.putCell(cell);
    return ResponseEntity.ok().build();
  }

}
