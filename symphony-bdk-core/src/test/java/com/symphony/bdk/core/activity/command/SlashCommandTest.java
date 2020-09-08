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

  @Test
  void testIllegalSlashCommandCreation() {
    assertThrows(IllegalArgumentException.class, () -> new SlashCommand("", c -> {}));
  }

  @Test
  void testSlashCommandWithBotMentionSuccess() {

    final AtomicBoolean handlerCalled = new AtomicBoolean(false);
    final Consumer<CommandContext> handler = c -> handlerCalled.set(true);

    final RealTimeEventsProvider provider = new RealTimeEventsProvider();
    final SlashCommand cmd = new SlashCommand("/test", handler);
    cmd.setBotDisplayName("BotMention");
    cmd.bindToRealTimeEventsSource(provider::setListener);

    provider.trigger(l -> l.onMessageSent(new V4Initiator(), createMessageSentEvent(true, "/test")));
    assertTrue(handlerCalled.get());
  }

  @Test
  void testSlashCommandWithoutBotMentionSuccess() {

    final AtomicBoolean handlerCalled = new AtomicBoolean(false);
    final Consumer<CommandContext> handler = c -> handlerCalled.set(true);

    final RealTimeEventsProvider provider = new RealTimeEventsProvider();
    final SlashCommand cmd = new SlashCommand("/test", false, handler);
    cmd.setBotDisplayName("BotMention");
    cmd.bindToRealTimeEventsSource(provider::setListener);

    provider.trigger(l -> l.onMessageSent(new V4Initiator(), createMessageSentEvent(false, "/test")));
    assertTrue(handlerCalled.get());
  }

  @Test
  void testSlashCommandNotTriggered() {

    final AtomicBoolean handlerCalled = new AtomicBoolean(false);
    final Consumer<CommandContext> handler = c -> handlerCalled.set(true);

    final RealTimeEventsProvider provider = new RealTimeEventsProvider();
    final SlashCommand cmd = new SlashCommand("/test", handler);
    cmd.setBotDisplayName("BotMention");
    cmd.bindToRealTimeEventsSource(provider::setListener);

    provider.trigger(l -> l.onMessageSent(new V4Initiator(), createMessageSentEvent(true, "/foo")));
    assertFalse(handlerCalled.get());
  }

  @Test
  void testVerifyBotInfo() {
    final SlashCommand cmd = new SlashCommand("/test", c -> {});
    final ActivityInfo info = cmd.getInfo();
    assertEquals(ActivityType.command, info.getType());
    assertEquals("Slash command '/test'", info.getName());
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
    final V4MessageSent event = new V4MessageSent().message(new V4Message().stream(new V4Stream()));
    event.getMessage().getStream().setStreamId(UUID.randomUUID().toString());
    event.getMessage().setMessageId(UUID.randomUUID().toString());
    String botMentionString = botMention ? "<span>@BotMention</span> " : "";
    event.getMessage().setMessage("<div><p>" + botMentionString + slashCommand + "</p></div>");
    return event;
  }
}
