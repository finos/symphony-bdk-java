package com.symphony.ms.bot.sdk.extapp;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.symphony.ms.bot.sdk.internal.symphony.StreamsClient;
import com.symphony.ms.bot.sdk.internal.symphony.exception.SymphonyClientException;
import com.symphony.ms.bot.sdk.internal.symphony.model.SymphonyStream;

/**
 * Sample code. Implementation of an extension app endpoint for stream
 *
 * @author Gabriel Berberian
 */
@RestController
@RequestMapping("/secure/stream")
public class StreamController {

  private final StreamsClient streamsClient;

  public StreamController(StreamsClient streamsClient) {
    this.streamsClient = streamsClient;
  }

  /**
   * Gets streams of all types from the application bot
   *
   * @return the bot streams
   */
  @GetMapping
  public ResponseEntity<List<SymphonyStream>> getUserStreams() {
    try {
      return ResponseEntity.ok(streamsClient.getUserStreams(null, true));
    } catch (SymphonyClientException sce) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

}
