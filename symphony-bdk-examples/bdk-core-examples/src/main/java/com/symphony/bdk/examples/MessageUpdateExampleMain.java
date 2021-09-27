package com.symphony.bdk.examples;

import static com.symphony.bdk.core.activity.command.SlashCommand.slash;
import static com.symphony.bdk.core.config.BdkConfigLoader.loadFromSymphonyDir;
import static java.util.concurrent.Executors.newSingleThreadExecutor;

import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.service.message.model.Message;
import com.symphony.bdk.gen.api.model.V4Message;

import lombok.SneakyThrows;

/**
 * This demonstrates a basic usage of the message update feature.
 */
public class MessageUpdateExampleMain {

  public static void main(String[] args) throws Exception {

    final SymphonyBdk bdk = new SymphonyBdk(loadFromSymphonyDir("config.yaml"));

    bdk.activities().register(slash("/go", false, context -> {
      newSingleThreadExecutor().submit(() -> startTheShow(bdk, context.getStreamId()));
    }));

    bdk.datafeed().start();
  }

  @SneakyThrows
  private static void startTheShow(SymphonyBdk bdk, String streamId) {
    V4Message initialMessage = bdk.messages().send(streamId, "This is an initial message");
    Thread.sleep(2000L);
    bdk.messages().update(streamId, initialMessage.getMessageId(), Message.builder().content("This is a message update").build());
  }
}
