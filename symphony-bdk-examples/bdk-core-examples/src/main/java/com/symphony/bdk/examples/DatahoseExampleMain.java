package com.symphony.bdk.examples;

import static com.symphony.bdk.core.config.BdkConfigLoader.loadFromSymphonyDir;

import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.auth.exception.AuthUnauthorizedException;
import com.symphony.bdk.core.config.exception.BdkConfigException;
import com.symphony.bdk.core.service.datafeed.EventException;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.gen.api.model.UserV2;
import com.symphony.bdk.gen.api.model.V4Event;
import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4MessageSent;
import com.symphony.bdk.gen.api.model.V4RoomUpdated;
import com.symphony.bdk.http.api.ApiException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DatahoseExampleMain {
  public static void main(String[] args)
      throws BdkConfigException, AuthInitializationException, AuthUnauthorizedException, ApiException {
    final SymphonyBdk bdk = new SymphonyBdk(loadFromSymphonyDir("config.yaml"));

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      log.info("Stopping Datahose...");
      bdk.datahose().stop();
    }));

    bdk.datahose().subscribe(new RealTimeEventListener() {

      @Override
      public boolean isAcceptingEvent(V4Event event, UserV2 botInfo) throws EventException {
        // if you want to listen to events from the same service account. Mind not to trigger new events in the listener
        return true;
      }

      @Override
      public void onRoomUpdated(V4Initiator initiator, V4RoomUpdated event) throws EventException {
        log.info("Room updated: {}", event.getNewRoomProperties());
      }

      @Override
      public void onMessageSent(V4Initiator initiator, V4MessageSent event) throws EventException {
        log.info("Message sent: {}", event.getMessage().getMessage());
      }
    });

    bdk.datahose().start();
  }
}
