package com.symphony.bdk.core.activity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.symphony.bdk.core.activity.command.CommandActivity;
import com.symphony.bdk.core.service.datafeed.DatafeedService;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

/**
 * Test class for the {@link ActivityRegistry}.
 */
@ExtendWith(MockitoExtension.class)
class ActivityRegistryTest {

  private ActivityRegistry registry;
  private DatafeedService datafeedServiceMock;
  private String botDisplayName;

  @BeforeEach
  void setUp() {
    this.botDisplayName = UUID.randomUUID().toString();
    this.datafeedServiceMock = mock(DatafeedService.class);
    this.registry = new ActivityRegistry(this.botDisplayName, this.datafeedServiceMock::subscribe);
  }

  @Test
  void shouldRegisterActivity() {
    final CommandActivity<?> act = new TestCommandActivity();
    assertTrue(this.registry.getActivityList().isEmpty(), "Registry must be empty");
    this.registry.register(act);
    assertEquals(1, this.registry.getActivityList().size(), "Registry must contain only 1 activity");
    verify(this.datafeedServiceMock, times(1)).subscribe(any(RealTimeEventListener.class));
    assertEquals(this.botDisplayName, act.getBotDisplayName());
  }
}
