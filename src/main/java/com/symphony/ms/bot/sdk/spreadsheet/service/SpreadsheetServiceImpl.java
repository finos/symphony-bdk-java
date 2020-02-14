package com.symphony.ms.bot.sdk.spreadsheet.service;

import com.symphony.ms.bot.sdk.internal.sse.model.SseEvent;
import com.symphony.ms.bot.sdk.internal.symphony.StreamsClient;
import com.symphony.ms.bot.sdk.internal.symphony.exception.SymphonyClientException;
import com.symphony.ms.bot.sdk.internal.symphony.model.StreamType;
import com.symphony.ms.bot.sdk.spreadsheet.model.RoomSpreadsheet;
import com.symphony.ms.bot.sdk.spreadsheet.model.SpreadsheetCell;
import com.symphony.ms.bot.sdk.spreadsheet.model.SpreadsheetCellContent;
import com.symphony.ms.bot.sdk.spreadsheet.model.SpreadsheetRoom;
import com.symphony.ms.bot.sdk.spreadsheet.model.SpreadsheetUpdateEvent;
import com.symphony.ms.bot.sdk.sse.SpreadsheetPublisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The service that manages the shared spreadsheet
 *
 * @author Gabriel Berberian
 */
@Service
public class SpreadsheetServiceImpl implements SpreadsheetService {
  private static final Logger LOGGER = LoggerFactory.getLogger(SpreadsheetService.class);
  private static final String SPREADSHEET_UPDATE_EVENT = "spreadsheetUpdateEvent";
  private static final long WAIT_INTERVAL = 1000L;

  private final SpreadsheetPublisher spreadsheetPublisher;
  private final StreamsClient streamsClient;
  private Map<String, Map> spreadsheets;

  public SpreadsheetServiceImpl(SpreadsheetPublisher spreadsheetPublisher,
      StreamsClient streamsClient) {
    this.spreadsheetPublisher = spreadsheetPublisher;
    this.spreadsheets = new HashMap<>();
    this.streamsClient = streamsClient;
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

  private SseEvent buildUpdateEvent(List<SpreadsheetCell> cells, String streamId, String userId) {
    return SseEvent.builder()
        .id(Long.toString(spreadsheetPublisher.getIdAndIncrement()))
        .retry(WAIT_INTERVAL)
        .event(SPREADSHEET_UPDATE_EVENT)
        .data(SpreadsheetUpdateEvent.builder()
            .streamId(streamId)
            .userId(userId)
            .cells(cells)
            .build())
        .metadata(Stream.of(
            new AbstractMap.SimpleEntry<>("streamId", streamId))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
        .build();
  }

}
