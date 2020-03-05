package com.symphony.bot.sdk.spreadsheet.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.symphony.bot.sdk.internal.symphony.StreamsClient;
import com.symphony.bot.sdk.internal.symphony.exception.SymphonyClientException;
import com.symphony.bot.sdk.internal.symphony.model.StreamType;
import com.symphony.bot.sdk.spreadsheet.model.RoomSpreadsheet;
import com.symphony.bot.sdk.spreadsheet.model.SpreadsheetCell;
import com.symphony.bot.sdk.spreadsheet.model.SpreadsheetCellContent;
import com.symphony.bot.sdk.spreadsheet.model.SpreadsheetEvent;
import com.symphony.bot.sdk.spreadsheet.model.SpreadsheetRoom;
import com.symphony.bot.sdk.sse.SpreadsheetPublisher;

/**
 * The service that manages the shared spreadsheet
 *
 * @author Gabriel Berberian
 */
@Service
public class SpreadsheetServiceImpl implements SpreadsheetService {
  private static final Logger LOGGER = LoggerFactory.getLogger(SpreadsheetService.class);
  private static final String SPREADSHEET_UPDATE_EVENT = "spreadsheetUpdateEvent";

  private final SpreadsheetPublisher spreadsheetPublisher;
  private final StreamsClient streamsClient;
  private Map<String, Map> spreadsheets;
  private final AtomicLong eventId;

  public SpreadsheetServiceImpl(SpreadsheetPublisher spreadsheetPublisher,
      StreamsClient streamsClient) {
    this.spreadsheetPublisher = spreadsheetPublisher;
    this.spreadsheets = new HashMap<>();
    this.streamsClient = streamsClient;
    this.eventId = new AtomicLong(1);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<String, Map> getSpreadsheets() {
    return spreadsheets;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map getSpreadsheet(String streamId) {
    return spreadsheets.get(streamId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addSpreadsheet(RoomSpreadsheet roomSpreadsheet, String userId) {
    spreadsheets.put(roomSpreadsheet.getStreamId(), roomSpreadsheet.getSpreadsheet());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void putCells(List<SpreadsheetCell> cells, String streamId, String userId) {
    Map spreadsheet = spreadsheets.get(streamId);
    if (spreadsheet != null) {
      cells.forEach(cell -> spreadsheet.put(cell.getKey(), SpreadsheetCellContent.builder()
          .expr(cell.getExpr())
          .value(cell.getValue())
          .build()));
      spreadsheetPublisher.publishEvent(buildUpdateEvent(cells, streamId, userId));
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<SpreadsheetRoom> getBotRooms() throws SymphonyClientException {
    try {
      return streamsClient.getUserStreams(Collections.singletonList(StreamType.ROOM), true)
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

  private SpreadsheetEvent buildUpdateEvent(List<SpreadsheetCell> cells, String streamId, String userId) {
    return SpreadsheetEvent.builder()
        .type(SPREADSHEET_UPDATE_EVENT)
        .id(Long.toString(eventId.getAndIncrement()))
        .streamId(streamId)
        .userId(userId)
        .cells(cells)
        .build();
  }

}
