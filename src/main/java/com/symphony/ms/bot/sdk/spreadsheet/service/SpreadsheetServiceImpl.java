package com.symphony.ms.bot.sdk.spreadsheet.service;

import com.symphony.ms.bot.sdk.internal.sse.model.SseEvent;
import com.symphony.ms.bot.sdk.internal.symphony.StreamsClient;
import com.symphony.ms.bot.sdk.internal.symphony.exception.SymphonyClientException;
import com.symphony.ms.bot.sdk.spreadsheet.model.RoomSpreadsheet;
import com.symphony.ms.bot.sdk.spreadsheet.model.RoomSpreadsheetCell;
import com.symphony.ms.bot.sdk.spreadsheet.model.SpreadsheetCell;
import com.symphony.ms.bot.sdk.spreadsheet.model.SpreadsheetRoom;
import com.symphony.ms.bot.sdk.sse.SpreadsheetPublisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * The service that manages the shared spreadsheet
 *
 * @author Gabriel Berberian
 */
@Service
public class SpreadsheetServiceImpl implements SpreadsheetService {
  private static final Logger LOGGER = LoggerFactory.getLogger(SpreadsheetService.class);
  private static final String SPREADSHEET_UPDATE_EVENT = "spreadsheetUpdateEvent";
  private static final String SPREADSHEET_RESET_EVENT = "spreadsheetRestEvent";
  private static final long WAIT_INTERVAL = 1000L;

  private final SpreadsheetPublisher spreadsheetPublisher;
  private final AtomicLong id;
  private final StreamsClient streamsClient;

  private Map<String, Object[][]> spreadsheets;

  public SpreadsheetServiceImpl(SpreadsheetPublisher spreadsheetPublisher,
      StreamsClient streamsClient) {
    this.spreadsheetPublisher = spreadsheetPublisher;
    this.id = new AtomicLong(0);
    this.spreadsheets = new HashMap<>();
    this.streamsClient = streamsClient;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, Object[][]> getSpreadsheets() {
    return spreadsheets;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object[][] getSpreadsheet(String streamId) {
    return spreadsheets.get(streamId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setSpreadsheet(RoomSpreadsheet roomSpreadsheet) {
    spreadsheets.put(roomSpreadsheet.getStreamId(), roomSpreadsheet.getSpreadsheet());
    spreadsheetPublisher.broadcast(buildResetEvent(roomSpreadsheet), roomSpreadsheet.getStreamId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void putCell(SpreadsheetCell cell, String streamId) {
    Object[][] spreadsheet = spreadsheets.get(streamId);
    if (spreadsheet != null) {
      spreadsheet[cell.getLine()][cell.getColumn()] = cell.getValue();
      spreadsheetPublisher.broadcast(buildUpdateEvent(cell, streamId), streamId);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<SpreadsheetRoom> getBotRooms() throws SymphonyClientException {
    try {
      return streamsClient.getUserStreams(null, true)
          .stream()
          .map(stream -> SpreadsheetRoom.builder()
              .stream(stream)
              .hasSpreadsheet(spreadsheets.containsKey(stream.getStreamId()))
              .build())
          .collect(Collectors.toList());
    } catch (SymphonyClientException e) {
      LOGGER.error("Exception thrown getting bot streams", e);
      throw e;
    }
  }

  private SseEvent buildResetEvent(RoomSpreadsheet roomSpreadsheet) {
    return SseEvent.builder()
        .id(Long.toString(id.getAndIncrement()))
        .retry(WAIT_INTERVAL)
        .event(SPREADSHEET_RESET_EVENT)
        .data(roomSpreadsheet)
        .build();
  }

  private SseEvent buildUpdateEvent(SpreadsheetCell cell, String streamId) {
    return SseEvent.builder()
        .id(Long.toString(id.getAndIncrement()))
        .retry(WAIT_INTERVAL)
        .event(SPREADSHEET_UPDATE_EVENT)
        .data(new RoomSpreadsheetCell(streamId, cell))
        .build();
  }

}
