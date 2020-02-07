package com.symphony.ms.bot.sdk.spreadsheet.service;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.symphony.ms.bot.sdk.internal.sse.model.SseEvent;
import com.symphony.ms.bot.sdk.internal.symphony.StreamsClient;
import com.symphony.ms.bot.sdk.internal.symphony.exception.SymphonyClientException;
import com.symphony.ms.bot.sdk.internal.symphony.model.StreamType;
import com.symphony.ms.bot.sdk.spreadsheet.model.RoomSpreadsheet;
import com.symphony.ms.bot.sdk.spreadsheet.model.SpreadsheetCell;
import com.symphony.ms.bot.sdk.spreadsheet.model.SpreadsheetCellContent;
import com.symphony.ms.bot.sdk.spreadsheet.model.SpreadsheetResetEvent;
import com.symphony.ms.bot.sdk.spreadsheet.model.SpreadsheetRoom;
import com.symphony.ms.bot.sdk.spreadsheet.model.SpreadsheetUpdateEvent;
import com.symphony.ms.bot.sdk.sse.SpreadsheetPublisher;

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

  private Map<String, Map> spreadsheets;

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
  public void setSpreadsheet(RoomSpreadsheet roomSpreadsheet, String userId) {
    spreadsheets.put(roomSpreadsheet.getStreamId(), roomSpreadsheet.getSpreadsheet());
    spreadsheetPublisher.publishEvent(buildResetEvent(roomSpreadsheet, userId));
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

  private SseEvent buildResetEvent(RoomSpreadsheet roomSpreadsheet, String userId) {
    return SseEvent.builder()
        .id(Long.toString(id.getAndIncrement()))
        .retry(WAIT_INTERVAL)
        .event(SPREADSHEET_RESET_EVENT)
        .data(SpreadsheetResetEvent.builder()
            .spreadsheet(roomSpreadsheet.getSpreadsheet())
            .streamId(roomSpreadsheet.getStreamId())
            .userId(userId)
            .build()
        ).build();
  }

  private SseEvent buildUpdateEvent(List<SpreadsheetCell> cells, String streamId, String userId) {
    return SseEvent.builder()
        .id(Long.toString(id.getAndIncrement()))
        .retry(WAIT_INTERVAL)
        .event(SPREADSHEET_UPDATE_EVENT)
        .data(cells.stream().map(cell -> SpreadsheetUpdateEvent.builder()
            .streamId(streamId)
            .key(cell.getKey())
            .value(cell.getValue())
            .expr(cell.getExpr())
            .userId(userId)
            .build()).toArray())
        .metadata(Stream.of(
            new AbstractMap.SimpleEntry<>("streamId", streamId))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)))
        .build();
  }

}
