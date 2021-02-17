package com.symphony.bdk.bot.sdk.sse;

import com.symphony.bdk.bot.sdk.sse.config.SseSubscriberProps;
import com.symphony.bdk.bot.sdk.sse.model.SseEvent;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class SseSubscriberTest {
  private static final String COMPLETION_EVENT = "_publisher_completion";
  private static final String COMPLETION_WITH_ERROR_EVENT = "_publisher_completion_error";

  private SseEmitter sseEmitter;
  private SseSubscriber sseSubscriber;
  private SsePublisher<?> publisher;

  @SneakyThrows
  @BeforeEach
  public void init() {
    this.sseEmitter = mock(SseEmitter.class);
    List<String> eventTypes = Collections.singletonList("eventTest");
    Map<String, String> metadata = Collections.singletonMap("Test 1", "Test");
    SseSubscriberProps props = new SseSubscriberProps();
    props.setQueueCapacity(1);
    props.setQueueTimeout(250L);
    this.publisher = mock(SsePublisher.class);
    doNothing().when(publisher).addSubscriber(any());
    doNothing().when(publisher).removeSubscriber(any());

    this.sseSubscriber = spy(new SseSubscriber(sseEmitter, eventTypes, metadata, "eventId", 12345L, props));
  }

  @Test
  public void getterTest() {
    assertEquals("eventTest", this.sseSubscriber.getEventTypes().get(0));
    assertEquals("Test", this.sseSubscriber.getMetadata().get("Test 1"));
    assertEquals("eventId", this.sseSubscriber.getLastEventId());
    assertEquals(12345L, this.sseSubscriber.getUserId());
  }

  @Test
  public void bindPublishersTest() {
    List<SsePublisher<?>> publishers = Collections.singletonList(this.publisher);
    this.sseSubscriber.bindPublishers(publishers);

    verify(this.publisher, times(1)).addSubscriber(any(SseSubscriber.class));
  }

  @SneakyThrows
  @Test
  public void listenEventQueueEmpty() {
    doNothing().doThrow(new IOException()).when(this.sseEmitter).send(any());
    List<SsePublisher<?>> publishers = Collections.singletonList(this.publisher);
    this.sseSubscriber.bindPublishers(publishers);
    this.sseSubscriber.startListening();
    verify(this.sseEmitter, times(2)).send(any(SseEmitter.SseEventBuilder.class));
  }

  @SneakyThrows
  @Test
  public void listenEventCompletion() {
    List<SsePublisher<?>> publishers = Collections.singletonList(this.publisher);
    this.sseSubscriber.bindPublishers(publishers);
    SseEvent event = this.createSseEvent(COMPLETION_EVENT);
    this.sseSubscriber.sendEvent(event);
    this.sseSubscriber.startListening();
    verify(this.sseEmitter, times(1)).complete();
  }

  @SneakyThrows
  @Test
  public void listenEventCompletionWithError() {
    List<SsePublisher<?>> publishers = Collections.singletonList(this.publisher);
    this.sseSubscriber.bindPublishers(publishers);
    SseEvent event = this.createSseEvent(COMPLETION_WITH_ERROR_EVENT);
    this.sseSubscriber.sendEvent(event);
    this.sseSubscriber.startListening();
    verify(this.sseEmitter, times(1)).completeWithError(any());
  }

  @SneakyThrows
  @Test
  public void listenEventDefault() {
    doNothing().doThrow(new IOException()).when(this.sseEmitter).send(any());
    List<SsePublisher<?>> publishers = Collections.singletonList(this.publisher);
    this.sseSubscriber.bindPublishers(publishers);
    SseEvent event = this.createSseEvent("test");
    this.sseSubscriber.sendEvent(event);
    this.sseSubscriber.startListening();
    verify(this.sseEmitter, times(2)).send(any(SseEmitter.SseEventBuilder.class));
  }

  @Test
  public void completeTest() {
    List<SsePublisher<?>> publishers = Collections.singletonList(this.publisher);
    this.sseSubscriber.bindPublishers(publishers);
    this.sseSubscriber.complete(this.publisher);
    SseEvent expectedEvent = SseEvent.builder().event(COMPLETION_EVENT).build();
    verify(this.sseSubscriber, times(1)).sendEvent(expectedEvent);
  }

  @Test
  public void completeWithError() {
    List<SsePublisher<?>> publishers = Collections.singletonList(this.publisher);
    this.sseSubscriber.bindPublishers(publishers);
    this.sseSubscriber.completeWithError(this.publisher, new Exception("test"));
    SseEvent expectedEvent = SseEvent.builder().event(COMPLETION_WITH_ERROR_EVENT).build();
    verify(this.sseSubscriber, times(1)).sendEvent(expectedEvent);
  }

  private SseEvent createSseEvent(String event) {
    return SseEvent.builder()
        .id("testId")
        .event(event)
        .data("testData")
        .retry(1L)
        .build();
  }
}
