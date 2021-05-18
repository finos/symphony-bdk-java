package com.symphony.bdk.core.activity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.activity.command.CommandActivity;
import com.symphony.bdk.core.activity.command.CommandContext;
import com.symphony.bdk.core.activity.command.SlashCommand;
import com.symphony.bdk.core.service.datafeed.DatafeedLoop;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.gen.api.model.UserV2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Test class for the {@link ActivityRegistry}.
 */
@ExtendWith(MockitoExtension.class)
class ActivityRegistryTest {

  @Mock
  private DatafeedLoop datafeedService;

  @Mock
  private MessageService messageService;

  @Mock
  private UserV2 botSession;

  private ActivityRegistry registry;

  @BeforeEach
  void setUp() {
    when(botSession.getDisplayName()).thenReturn(UUID.randomUUID().toString());
    this.registry = new ActivityRegistry(this.botSession, this.datafeedService);
  }

  @Test
  void shouldReplaceActivity() {
    final CommandActivity<?> act = new TestCommandActivity("test");
    assertTrue(this.registry.getActivityList().isEmpty(), "Registry must be empty");

    this.registry.register(act);
    assertEquals(1, this.registry.getActivityList().size(), "Registry must contain only 1 activity");
    verify(this.datafeedService, times(1)).subscribe(any(RealTimeEventListener.class));
    assertEquals(this.botSession.getDisplayName(), act.getBotDisplayName());

    this.registry.register(new TestCommandActivity("test"));
    assertEquals(1, this.registry.getActivityList().size(), "Registry should still contain only 1 activity");
    verify(this.datafeedService, times(1)).unsubscribe(any(RealTimeEventListener.class));
  }

  @Test
  void shouldRegister_sameValue_differentMention(){
    final AtomicBoolean handlerCalled = new AtomicBoolean(false);
    final Consumer<CommandContext> handler = c -> handlerCalled.set(true);

    final CommandActivity<?> actMentionRequired = SlashCommand.slash("test", true, handler);
    final CommandActivity<?> actMentionNotRequired = SlashCommand.slash("test", false, handler);

    assertTrue(this.registry.getActivityList().isEmpty(), "Registry must be empty");

    this.registry.register(actMentionRequired);
    this.registry.register(actMentionNotRequired);

    verify(this.datafeedService, times(2)).subscribe(any(RealTimeEventListener.class));
    verify(this.datafeedService, never()).unsubscribe(any(RealTimeEventListener.class));

    assertEquals(2, this.registry.getActivityList().size(), "Both activities must have been registered");
  }

  @Test
  void shouldNotRegister_sameValue_sameMention(){
    final AtomicBoolean handlerCalled = new AtomicBoolean(false);
    final Consumer<CommandContext> handler = c -> handlerCalled.set(true);

    final CommandActivity<?> actMentionRequired = SlashCommand.slash("test", true, handler);
    final CommandActivity<?> actMentionNotRequired = SlashCommand.slash("test", true, handler);

    assertTrue(this.registry.getActivityList().isEmpty(), "Registry must be empty");

    this.registry.register(actMentionRequired);
    this.registry.register(actMentionNotRequired);

    verify(this.datafeedService, times(2)).subscribe(any(RealTimeEventListener.class));
    verify(this.datafeedService, times(1)).unsubscribe(any(RealTimeEventListener.class));

    assertEquals(1, this.registry.getActivityList().size(), "Only one activities must have been registered");
  }

}
