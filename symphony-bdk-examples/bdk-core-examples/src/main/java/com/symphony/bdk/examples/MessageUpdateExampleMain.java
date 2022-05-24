package com.symphony.bdk.examples;

import static com.symphony.bdk.core.activity.command.SlashCommand.slash;
import static com.symphony.bdk.core.config.BdkConfigLoader.loadFromSymphonyDir;

import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.service.message.model.Message;
import com.symphony.bdk.gen.api.model.V3RoomAttributes;
import com.symphony.bdk.gen.api.model.V4Message;
import com.symphony.bdk.http.api.ApiRuntimeException;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Function;

/**
 * This demonstrates a basic usage of the message update feature.
 */
@Slf4j
public class MessageUpdateExampleMain {

  public static void main(String[] args) throws Exception {

    final SymphonyBdk bdk = new SymphonyBdk(loadFromSymphonyDir("config.yaml"));

    bdk.activities().register(slash("/go", false, context -> startTheShow(bdk, context.getStreamId())));

    bdk.datafeed().start();
  }

  @SneakyThrows
  private static void startTheShow(SymphonyBdk bdk, String streamId) {

    // just for this example, obviously!
    Function<Integer, String> pickEmoji = index -> new String[] {
        "cat", "dromedary_camel", "dolphin", "dog", "hamster", "goat", "panda_face", "koala", "frog", "penguin"
    }[index];

    V4Message previousMessage = bdk.messages().send(streamId, "Let the show begin!");
    pinMessage(bdk, previousMessage);

    for (int i = 0; i < 10; i++) {
      Thread.sleep((long) (Math.random() * (5000L - 500L)));
      String mml = "<emoji shortcode=\"" + pickEmoji.apply(i) + "\" /><br/><br/>Update <b>#" + (i + 1) + "</b>";
      previousMessage = bdk.messages().update(previousMessage, Message.builder().content(mml).silent(false).build());
    }
  }

  private static void pinMessage(SymphonyBdk bdk, V4Message previousMessage) {
    try {
      V3RoomAttributes attrs = new V3RoomAttributes().pinnedMessageId(previousMessage.getMessageId());
      bdk.streams().updateRoom(previousMessage.getStream().getStreamId(), attrs);
    } catch (ApiRuntimeException ex) {
      log.info("Message can only be pinned in room");
    }
  }
}
