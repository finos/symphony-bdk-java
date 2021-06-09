package com.symphony.bdk.core.activity.command;

import static com.symphony.bdk.core.activity.command.SlashCommand.slash;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.symphony.bdk.core.activity.AbstractActivity;
import com.symphony.bdk.core.activity.ActivityRegistry;
import com.symphony.bdk.core.activity.model.ActivityInfo;
import com.symphony.bdk.core.activity.model.ActivityType;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.core.service.message.model.Message;
import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4Message;
import com.symphony.bdk.gen.api.model.V4MessageSent;
import com.symphony.bdk.gen.api.model.V4Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@ExtendWith(MockitoExtension.class)
public class HelpCommandTest {

  @Mock
  private MessageService messageService;

  @Mock
  private ActivityRegistry activityRegistry;

  @Test
  void testHelpCommandSuccess() {
    List<AbstractActivity<?, ?>> activities = new ArrayList<>();
    activities.add(slash("/test", commandContext -> {}, "test command"));

    when(this.activityRegistry.getActivityList()).thenReturn(activities);

    final HelpCommand helpCommand = new HelpCommand(this.activityRegistry, this.messageService);
    final RealTimeEventsProvider provider = new RealTimeEventsProvider();
    helpCommand.setBotDisplayName("BotMention");
    helpCommand.bindToRealTimeEventsSource(provider::setListener);

    V4MessageSent event = createMessageSentEvent();
    provider.trigger(l -> l.onMessageSent(new V4Initiator(), event));

    verify(this.messageService).send(eq(event.getMessage().getStream().getStreamId()), any(Message.class));
  }

  @Test
  void testHelpCommandWithNoCommandActivityRegistry() {
    final HelpCommand helpCommand = new HelpCommand(this.activityRegistry, this.messageService);

    when(this.activityRegistry.getActivityList()).thenReturn(Collections.singletonList(helpCommand));

    final RealTimeEventsProvider provider = new RealTimeEventsProvider();
    helpCommand.setBotDisplayName("BotMention");
    helpCommand.bindToRealTimeEventsSource(provider::setListener);

    V4MessageSent event = createMessageSentEvent();
    provider.trigger(l -> l.onMessageSent(new V4Initiator(), event));

    ArgumentCaptor<Message> argumentCaptor = ArgumentCaptor.forClass(Message.class);

    // The build in /help command should be returned
    verify(this.messageService, times(1)).send(anyString(), argumentCaptor.capture());
    Message message = argumentCaptor.getValue();
    assertNotNull(argumentCaptor.getValue());
    assertEquals("<messageML><ul><li>/help - List available commands (mention required)</li></ul></messageML>",
        message.getContent(), "The help command infos should be sent in response");
  }

  @Test
  void verifyBotInfo() {
    final HelpCommand command = new HelpCommand(this.activityRegistry, this.messageService);
    final ActivityInfo info = command.getInfo();

    assertEquals(ActivityType.COMMAND, info.type());
    assertEquals("/help", info.name());
    assertEquals("List available commands (mention required)", info.description());
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

  private static V4MessageSent createMessageSentEvent() {
    final V4MessageSent event = new V4MessageSent().message(new V4Message().stream(new V4Stream()));
    event.getMessage().getStream().setStreamId(UUID.randomUUID().toString());
    event.getMessage().setMessageId(UUID.randomUUID().toString());
    String botMentionString = "<span>@BotMention</span> ";
    event.getMessage().setMessage("<div><p>" + botMentionString + "/help" + "</p></div>");
    return event;
  }
}
