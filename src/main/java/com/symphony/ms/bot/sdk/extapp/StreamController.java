package com.symphony.ms.bot.sdk.extapp;

import com.symphony.ms.bot.sdk.internal.symphony.SymphonyService;
import com.symphony.ms.bot.sdk.internal.symphony.model.SymphonyStream;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Sample code. Implementation of an extension app endpoint for stream
 *
 * @author Gabriel Berberian
 */
@RestController
@RequestMapping("/secure/stream")
public class StreamController {

  private final SymphonyService symphonyService;

  public StreamController(SymphonyService symphonyService) {
    this.symphonyService = symphonyService;
  }

  /**
   * Gets streams of all types from the application bot
   *
   * @return the bot streams
   */
  @GetMapping
  public List<SymphonyStream> getUserStreams() {
    return symphonyService.getUserStreams(null, true);
  }

}
