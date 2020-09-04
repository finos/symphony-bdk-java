package com.symphony.bdk.core.activity.command;

import static org.junit.jupiter.api.Assertions.*;

import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4Message;
import com.symphony.bdk.gen.api.model.V4MessageSent;
import com.symphony.bdk.gen.api.model.V4Stream;

import org.junit.jupiter.api.Test;

import java.util.UUID;

/**
 * Test class for the {@link CommandContext}.
 */
class CommandContextTest {

  @Test
  void testCreateCommandContext() {

    final String messageId = UUID.randomUUID().toString();
    final String streamId = UUID.randomUUID().toString();

    final V4MessageSent event = new V4MessageSent().message(new V4Message().stream(new V4Stream()));

    event.getMessage().getStream().setStreamId(streamId);
    event.getMessage().setMessageId(messageId);

    final CommandContext commandContext = new CommandContext(new V4Initiator(), event);

    assertEquals(streamId, commandContext.getStreamId(), "Wrong streamId");
    assertEquals(messageId, commandContext.getMessageId(), "Wrong messageId");
  }
}
