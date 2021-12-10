package com.symphony.bdk.core.activity.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.symphony.bdk.core.activity.model.ActivityInfo;
import com.symphony.bdk.core.activity.model.ActivityType;
import com.symphony.bdk.core.activity.parsing.Cashtag;
import com.symphony.bdk.core.activity.parsing.Hashtag;
import com.symphony.bdk.core.activity.parsing.Mention;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4Message;
import com.symphony.bdk.gen.api.model.V4MessageSent;
import com.symphony.bdk.gen.api.model.V4Stream;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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
    assertThrows(IllegalArgumentException.class, () -> SlashCommand.slash("", c -> {
    }));
  }

  @Test
  void testSlashCommandWithBotMentionSuccess() {

    final AtomicBoolean handlerCalled = new AtomicBoolean(false);
    final Consumer<CommandContext> handler = c -> handlerCalled.set(true);

    final RealTimeEventsProvider provider = new RealTimeEventsProvider();
    final SlashCommand cmd = SlashCommand.slash("/test", handler);
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
    final String slashCommandName = "/test";

    final SlashCommand cmd = SlashCommand.slash(slashCommandName, handler);
    cmd.setBotUserId(BOT_USER_ID);
    cmd.bindToRealTimeEventsSource(provider::setListener);

    final V4MessageSent event = createMessageSentEvent("<div><p>"
            + "<span class=\"entity\" data-entity-id=\"0\">@" + BOT_DISPLAY_NAME + "</span>"
            + slashCommandName
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
    final String slashCommandName = "/test";
    final SlashCommand cmd = SlashCommand.slash(slashCommandName, handler);
    cmd.setBotUserId(BOT_USER_ID);
    cmd.bindToRealTimeEventsSource(provider::setListener);

    final V4MessageSent event = createMessageSentEvent("<div><p>"
            + "@" + BOT_DISPLAY_NAME + " " + slashCommandName
            + "</p></div>",
        "{}");

    provider.trigger(l -> l.onMessageSent(new V4Initiator(), event));

    assertFalse(handlerCalled.get());
  }

  @Test
  void testSlashCommandOneStringArgument() {
    final AtomicBoolean handlerCalled = new AtomicBoolean(false);
    final Map<String, Object> arguments = new HashMap<>();

    final Consumer<CommandContext> handler = c -> {
      handlerCalled.set(true);
      copyArguments(c, arguments);
    };

    final RealTimeEventsProvider provider = new RealTimeEventsProvider();
    final String slashCommandName = "/test";
    String argName = "myarg";
    final SlashCommand cmd = SlashCommand.slash(slashCommandName + " {" + argName + "}", handler);
    cmd.setBotDisplayName(BOT_DISPLAY_NAME);
    cmd.setBotUserId(BOT_USER_ID);
    cmd.bindToRealTimeEventsSource(provider::setListener);

    final String argValue = "value";
    final V4MessageSent event = createMessageSentEvent(true, slashCommandName + " " + argValue);

    provider.trigger(l -> l.onMessageSent(new V4Initiator(), event));

    assertTrue(handlerCalled.get());
    assertEquals(Collections.singletonMap(argName, argValue), arguments);
  }

  @Test
  void testSlashCommandStringMentionCashtagHashtagArguments() {
    final AtomicBoolean handlerCalled = new AtomicBoolean(false);
    final Map<String, Object> arguments = new HashMap<>();

    final Consumer<CommandContext> handler = c -> {
      handlerCalled.set(true);
      copyArguments(c, arguments);
    };

    final RealTimeEventsProvider provider = new RealTimeEventsProvider();

    final String slashCommandName = "/test";
    String stringArgName = "stringArg";
    String mentionArgName = "mentionArg";
    String hashtagArgName = "hashtagArg";
    String cashtagArgName = "cashtagArg";

    final SlashCommand cmd = SlashCommand.slash(
        slashCommandName + " {" + stringArgName + "} {@" + mentionArgName + "} {#" + hashtagArgName + "} {$" + cashtagArgName + "}",
        handler);
    cmd.setBotDisplayName(BOT_DISPLAY_NAME);
    cmd.setBotUserId(BOT_USER_ID);
    cmd.bindToRealTimeEventsSource(provider::setListener);

    final V4MessageSent event = createMessageSentEvent("<div><p>"
            + "<span class=\"entity\" data-entity-id=\"0\">@" + BOT_DISPLAY_NAME + "</span>"
            + slashCommandName + " "
            + "value "
            + "<span class=\"entity\" data-entity-id=\"1\">@John Doe</span> "
            + "<span class=\"entity\" data-entity-id=\"2\">#myhashtag</span> "
            + "<span class=\"entity\" data-entity-id=\"3\">$mycashtag</span>"
            + "</p></div>",
        "{\"0\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"" + BOT_USER_ID + "\"}],\"type\":\"com.symphony.user.mention\"},"
            + "\"1\":{\"id\":[{\"type\":\"com.symphony.user.userId\",\"value\":\"12345679\"}],\"type\":\"com.symphony.user.mention\"},"
            + "\"2\":{\"id\":[{\"type\":\"org.symphonyoss.taxonomy.hashtag\",\"value\":\"myhashtag\"}],\"type\":\"org.symphonyoss.taxonomy\"},"
            + "\"3\":{\"id\":[{\"type\":\"org.symphonyoss.fin.security.id.ticker\",\"value\":\"mycashtag\"}],\"type\":\"org.symphonyoss.fin.security\"}}");

    provider.trigger(l -> l.onMessageSent(new V4Initiator(), event));

    assertTrue(handlerCalled.get());
    assertEquals(4, arguments.size());
    assertEquals("value", arguments.get(stringArgName));
    assertEquals(new Mention("@John Doe", 12345679L), arguments.get(mentionArgName));
    assertEquals(new Hashtag("#myhashtag", "myhashtag"), arguments.get(hashtagArgName));
    assertEquals(new Cashtag("$mycashtag", "mycashtag"), arguments.get(cashtagArgName));
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

  private static void copyArguments(CommandContext c, Map<String, Object> arguments) {
    for (String arg : c.getArguments().getArgumentNames()) {
      arguments.put(arg, c.getArguments().get(arg));
    }
  }
}
