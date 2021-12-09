package com.symphony.bdk.core.activity.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.symphony.bdk.core.activity.model.ActivityInfo;
import com.symphony.bdk.core.activity.model.ActivityType;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4Message;
import com.symphony.bdk.gen.api.model.V4MessageSent;
import com.symphony.bdk.gen.api.model.V4Stream;

import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Test class from the {@link SlashCommand}.
 */
class SlashCommandTest {

  private static final long BOT_USER_ID = 12345678L;
  private static final String BOT_DISPLAY_NAME = "BotMention";

  @Test
  void testIllegalSlashCommandCreation() {
    assertThrows(IllegalArgumentException.class, () -> SlashCommand.slash("", c -> {}));
  }

  @Test
  void testSlashCommandWithBotMentionSuccess() {

    final AtomicBoolean handlerCalled = new AtomicBoolean(false);
    final Consumer<CommandContext> handler = c -> handlerCalled.set(true);

    final RealTimeEventsProvider provider = new RealTimeEventsProvider();
    final SlashCommand cmd = SlashCommand.slash("/test", handler);
    cmd.setBotDisplayName(BOT_DISPLAY_NAME);
    cmd.setBotUserId(BOT_USER_ID);
    cmd.bindToRealTimeEventsSource(provider::setListener);

    provider.trigger(l -> l.onMessageSent(new V4Initiator(), createMessageSentEvent(true, "/test")));
    assertTrue(handlerCalled.get());
  }

  @Test
  void testSlashCommandWithoutBotMentionSuccess() {

    final AtomicBoolean handlerCalled = new AtomicBoolean(false);
    final Consumer<CommandContext> handler = c -> handlerCalled.set(true);

    final RealTimeEventsProvider provider = new RealTimeEventsProvider();
    final SlashCommand cmd = SlashCommand.slash("/test", false, handler);
    cmd.setBotDisplayName(BOT_DISPLAY_NAME);
    cmd.setBotUserId(BOT_USER_ID);
    cmd.bindToRealTimeEventsSource(provider::setListener);

    provider.trigger(l -> l.onMessageSent(new V4Initiator(), createMessageSentEvent(false, "/test")));
    assertTrue(handlerCalled.get());
  }

  @Test
  void testSlashCommandNotTriggered() {

    final AtomicBoolean handlerCalled = new AtomicBoolean(false);
    final Consumer<CommandContext> handler = c -> handlerCalled.set(true);

    final RealTimeEventsProvider provider = new RealTimeEventsProvider();
    final SlashCommand cmd = SlashCommand.slash("/test", handler);
    cmd.setBotDisplayName(BOT_DISPLAY_NAME);
    cmd.setBotUserId(BOT_USER_ID);
    cmd.bindToRealTimeEventsSource(provider::setListener);

    provider.trigger(l -> l.onMessageSent(new V4Initiator(), createMessageSentEvent(true, "/foo")));
    assertFalse(handlerCalled.get());
  }

  @Test
  void testCommandNotTriggeredOnMismatchingBotId() {
    final AtomicBoolean handlerCalled = new AtomicBoolean(false);
    final Consumer<CommandContext> handler = c -> handlerCalled.set(true);

    final RealTimeEventsProvider provider = new RealTimeEventsProvider();
    final SlashCommand cmd = SlashCommand.slash("/test", handler);
    cmd.setBotDisplayName(BOT_DISPLAY_NAME);
    cmd.setBotUserId(BOT_USER_ID);
    cmd.bindToRealTimeEventsSource(provider::setListener);

    final V4MessageSent event = createMessageSentEvent("<div><p>"
            + "<span class=\"entity\" data-entity-id=\"0\">@" + BOT_DISPLAY_NAME + "</span>"
            + "/text"
            + "</p></div>",
        "{\"0\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"99999\"}],\"type\":\"com.symphony.user.mention\"}}");

    provider.trigger(l -> l.onMessageSent(new V4Initiator(), event));

    assertFalse(handlerCalled.get());
  }

  @Test
  void testCommandNotTriggeredWithCorrectTextButNotMention() {
    final AtomicBoolean handlerCalled = new AtomicBoolean(false);
    final Consumer<CommandContext> handler = c -> handlerCalled.set(true);

    final RealTimeEventsProvider provider = new RealTimeEventsProvider();
    final SlashCommand cmd = SlashCommand.slash("/test", handler);
    cmd.setBotDisplayName(BOT_DISPLAY_NAME);
    cmd.setBotUserId(BOT_USER_ID);
    cmd.bindToRealTimeEventsSource(provider::setListener);

    final V4MessageSent event = createMessageSentEvent("<div><p>"
            + "@" + BOT_DISPLAY_NAME + " " + "/text"
            + "</p></div>",
        "{}");

    provider.trigger(l -> l.onMessageSent(new V4Initiator(), event));

    assertFalse(handlerCalled.get());
  }

  @Test
  void testVerifyBotInfo() {
    final SlashCommand cmd = SlashCommand.slash("/test", c -> {
    });
    final ActivityInfo info = cmd.getInfo();
    assertEquals(ActivityType.COMMAND, info.type());
    assertEquals("/test", info.name());
  }

  private static class RealTimeEventsProvider {

    private RealTimeEventListener listener;

    public void setListener(RealTimeEventListener listener) {
      this.listener = listener;
    }

    public void trigger(Consumer<RealTimeEventListener> consumer) {
      consumer.accept(this.listener);
    }
  }

  private static V4MessageSent createMessageSentEvent(boolean botMention, String slashCommand) {
    String botMentionString =
        botMention ? "<span class=\"entity\" data-entity-id=\"0\">@" + BOT_DISPLAY_NAME + "</span> " : "";
    String message = "<div><p>" + botMentionString + slashCommand + "</p></div>";

    String data = botMention ? "{\"0\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"" + BOT_USER_ID
        + "\"}],\"type\":\"com.symphony.user.mention\"}}" : "{}";

    return createMessageSentEvent(message, data);
  }

  private static V4MessageSent createMessageSentEvent(String message, String data) {
    final V4MessageSent event = new V4MessageSent().message(new V4Message().stream(new V4Stream()));
    event.getMessage().getStream().setStreamId(UUID.randomUUID().toString());
    event.getMessage().setMessageId(UUID.randomUUID().toString());

    event.getMessage().setMessage(message);
    event.getMessage().setData(data);
    return event;
  }
}
