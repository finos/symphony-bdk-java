package com.symphony.bdk.core.activity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.symphony.bdk.core.activity.command.CommandActivity;
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
    this.registry = new ActivityRegistry(this.botSession, this.datafeedService::subscribe);
  }

  @Test
  void shouldRegisterActivity() {
    final CommandActivity<?> act = new TestCommandActivity();
    assertEquals(0, this.registry.getActivityList().size());
    this.registry.register(act);
    assertEquals(1, this.registry.getActivityList().size(), "Registry must contain 2 activity");
    verify(this.datafeedService, times(1)).subscribe(any(RealTimeEventListener.class));
    assertEquals(this.botSession.getDisplayName(), act.getBotDisplayName());
  }
}
