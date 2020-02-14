package com.symphony.ms.bot.sdk.spreadsheet.service;

import com.symphony.ms.bot.sdk.internal.sse.SsePublisher;
import com.symphony.ms.bot.sdk.internal.sse.model.SseEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Service to control all {@link SpreadsheetPresenceSender}
 */
@Service
public class SpreadsheetPresenceService {
  private static final Logger LOGGER = LoggerFactory.getLogger(SpreadsheetPresenceService.class);
  private static long TIME_TO_CHECK_SENDERS = 3000;
  private static long SENDERS_INTERVAL = 9000;

  private List<SpreadsheetPresenceSender> presenceSenders;

  public SpreadsheetPresenceService() {
    presenceSenders = new ArrayList<>();
    new Thread(() -> sendPresences()).start();
  }

  /**
   * Add a new {@link SpreadsheetPresenceSender} for the presence sending process
   *
   * @param eventId
   * @param presenceEvent
   * @param publisher
   * @param streamId
   * @param userId
   */
  public void beginSending(AtomicLong eventId, SseEvent presenceEvent,
      SsePublisher publisher, String streamId, Long userId) {
    SpreadsheetPresenceSender presenceSender = SpreadsheetPresenceSender.builder()
        .eventId(eventId)
        .presenceEvent(presenceEvent)
        .publisher(publisher)
        .streamId(streamId)
        .userId(userId)
        .build();
    presenceSender.send();
    presenceSenders.add(presenceSender);
  }

  /**
   * Removes the {@link SpreadsheetPresenceSender} from the presence sending process
   *
   * @param streamId
   * @param userId
   */
  public void finishSending(String streamId, Long userId) {
    Iterator<SpreadsheetPresenceSender> iterator = presenceSenders.iterator();
    while (iterator.hasNext()) {
      SpreadsheetPresenceSender presenceSender = iterator.next();
      if ((streamId == null && presenceSender.getStreamId() == null && presenceSender.getUserId()
          .equals(userId)) || (streamId != null && presenceSender.getStreamId().equals(streamId)
          && presenceSender.getUserId().equals(userId))) {
        iterator.remove();
        break;
      }
    }
  }

  private void sendPresences() {
    while (true) {
      long currentTime = System.currentTimeMillis();
      for (SpreadsheetPresenceSender presenceSender : presenceSenders) {
        if (currentTime >= presenceSender.getTimeLastEvent() + SENDERS_INTERVAL) {
          presenceSender.send();
        }
      }
      waitTime(TIME_TO_CHECK_SENDERS);
    }
  }

  private void waitTime(long milliseconds) {
    try {
      Thread.sleep(milliseconds);
    } catch (InterruptedException ie) {
      LOGGER.debug("Error waiting for next events");
    }
  }

}
