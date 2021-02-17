package com.symphony.bdk.bot.sdk.sse;

import com.symphony.bdk.bot.sdk.sse.config.SseSubscriberProps;
import com.symphony.bdk.bot.sdk.symphony.ConfigClient;
import org.junit.jupiter.api.*;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SseControllerTest {
  private SseController sseController;
  private SsePublisherRouter ssePublisherRouter;
  private SseSubscriberProps subscriberConfig;
  private ConfigClient configClient;

  @BeforeEach
  public void init() {
    this.ssePublisherRouter = mock(SsePublisherRouter.class);
    this.subscriberConfig = mock(SseSubscriberProps.class);
    this.configClient = mock(ConfigClient.class);
    when(this.configClient.getExtAppAuthPath()).thenReturn("localhost/");
    when(this.subscriberConfig.getQueueCapacity()).thenReturn(5);

    this.sseController = new SseController(this.ssePublisherRouter, this.configClient, this.subscriberConfig);
  }

  @Test
  public void subscribeSseTest() {
    List<String> eventTypes = Collections.singletonList("eventTest");
    Map<String, String> metadata = Collections.singletonMap("Test 1", "Test");
    HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
    List<SsePublisher<?>> ssePublishers = new ArrayList<>();
    ssePublishers.add(mock(SsePublisher.class));
    when(this.ssePublisherRouter.findPublishers(any())).thenReturn(ssePublishers);
    SseEmitter emitter = this.sseController.subscribeSse(eventTypes, metadata, "eventId", "123456", httpServletResponse);
    assertNotNull(emitter);
  }

  @Test
  public void subscribeSseNoPublisherFoundTest() {
    List<String> eventTypes = Collections.singletonList("eventTest");
    Map<String, String> metadata = Collections.singletonMap("Test 1", "Test");
    HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
    List<SsePublisher<?>> ssePublishers = new ArrayList<>();
    when(this.ssePublisherRouter.findPublishers(any())).thenReturn(ssePublishers);

    SseEmitter emitter = this.sseController.subscribeSse(eventTypes, metadata, "eventId", "123456", httpServletResponse);
    assertNull(emitter);
  }

  @Test
  public void subscribeSseBadUserIdTest() {
    List<String> eventTypes = Collections.singletonList("eventTest");
    Map<String, String> metadata = Collections.singletonMap("Test 1", "Test");
    HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
    List<SsePublisher<?>> ssePublishers = new ArrayList<>();
    ssePublishers.add(mock(SsePublisher.class));
    when(this.ssePublisherRouter.findPublishers(any())).thenReturn(ssePublishers);

    assertThrows(ResponseStatusException.class, () -> {
      this.sseController.subscribeSse(eventTypes, metadata, "eventId", "userId", httpServletResponse);
    });
  }

  @Test
  public void subscribeSseBindFailedTest() {
    List<String> eventTypes = Collections.singletonList("eventTest");
    Map<String, String> metadata = Collections.singletonMap("Test 1", "Test");
    HttpServletResponse httpServletResponse = mock(HttpServletResponse.class);
    List<SsePublisher<?>> ssePublishers = new ArrayList<>();
    ssePublishers.add(mock(SsePublisher.class));
    when(this.ssePublisherRouter.findPublishers(any())).thenReturn(ssePublishers);
    doThrow(TaskRejectedException.class).when(this.ssePublisherRouter).bind(any(), anyList());

    assertThrows(ResponseStatusException.class, () -> {
      this.sseController.subscribeSse(eventTypes, metadata, "eventId", "123456", httpServletResponse);
    });
  }
}
