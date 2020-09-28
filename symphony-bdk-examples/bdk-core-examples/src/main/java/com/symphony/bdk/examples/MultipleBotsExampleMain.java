package com.symphony.bdk.examples;

import static com.symphony.bdk.core.config.BdkConfigLoader.loadFromSymphonyDir;

import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.config.exception.BdkConfigException;
import com.symphony.bdk.core.service.datafeed.DatafeedService;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.gen.api.model.Stream;
import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4MessageSent;

import com.symphony.bdk.http.api.ApiException;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;

/**
 * Mario and Luigi talk together.
 */
@Slf4j
public class MultipleBotsExampleMain {

  public static void main(String[] args)
      throws BdkConfigException, AuthInitializationException, AuthUnauthorizedException {

    final SymphonyBdk mario = new SymphonyBdk(loadFromSymphonyDir("config-mario.yaml"));
    final SymphonyBdk luigi = new SymphonyBdk(loadFromSymphonyDir("config-luigi.yaml"));

    mario.datafeed().subscribe(new RealTimeEventListener() {

      @Override
      public void onMessageSent(V4Initiator initiator, V4MessageSent event) {
        log.info("Mario received a message from {} in stream {}", initiator.getUser().getDisplayName(),
            event.getMessage().getStream().getStreamId());
      }
    });

    luigi.datafeed().subscribe(new RealTimeEventListener() {

      @Override
      public void onMessageSent(V4Initiator initiator, V4MessageSent event) {
        log.info("Luigi received a message from {} in stream {}", initiator.getUser().getDisplayName(),
            event.getMessage().getStream().getStreamId());
      }
    });

    startDatafeedAsync(mario.datafeed());
    startDatafeedAsync(luigi.datafeed());

    // mario creates an IM with luigi
    final Stream stream = mario.streams().create(luigi.botInfo().getId());
    log.info("Mario created IM ({}) with Luigi", stream.getId());

    // luigi sends an message in in IM with mario
    luigi.messages().send(stream.getId(), "<messageML>Hello Mario, how are you?</messageML>");

    // mario sends an message in in IM with luigi
    mario.messages().send(stream.getId(), "<messageML>Very well thank you! And you Luigi?</messageML>");
  }

  private static void startDatafeedAsync(DatafeedService datafeedService) {
    Executors.newSingleThreadExecutor().execute(() -> {
      try {
        datafeedService.start();
      } catch (AuthUnauthorizedException | ApiException e) {
        log.error("Failed to start datafeed loop", e);
      }
    });
  }
}
