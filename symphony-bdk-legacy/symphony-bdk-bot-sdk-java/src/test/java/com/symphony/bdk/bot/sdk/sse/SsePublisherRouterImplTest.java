package com.symphony.bdk.bot.sdk.sse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.symphony.bdk.bot.sdk.sse.config.SseSubscriberProps;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SsePublisherRouterImplTest {

  private SsePublisherRouterImpl ssePublisherRouterImpl;
  private SsePublisher<?> ssePublisher = Mockito.mock(SsePublisher.class);

  @Before
  public void initSsePublisher() {
    this.ssePublisherRouterImpl = new SsePublisherRouterImpl();
  }

  @Test
  public void testRegister() {
    final List<String> stringsList = this.initStringsList();
    Mockito.when(this.ssePublisher.getEventTypes()).thenReturn(stringsList);

    this.ssePublisherRouterImpl.register(this.ssePublisher);
    // We can't verify that we have added well the ssePublisher because the list is private and there no getter
  }

  @Test
  public void testFindPublishers() {
    final List<String> eventTypes = this.initStringsList();
    Mockito.when(this.ssePublisher.getEventTypes()).thenReturn(eventTypes);

    this.ssePublisherRouterImpl.register(this.ssePublisher);

    final List<SsePublisher<?>> publishers = this.ssePublisherRouterImpl.findPublishers(eventTypes);
    assertNotNull(publishers);
    assertEquals(1, publishers.size());

    final SsePublisher<?> publisher = publishers.get(0);
    assertEquals(eventTypes, publisher.getEventTypes());
  }

  @Test
  public void testFindPublishersWithEmptyList() {
    final List<String> eventTypes = new ArrayList<>();
    Mockito.when(this.ssePublisher.getEventTypes()).thenReturn(eventTypes);

    final List<SsePublisher<?>> publishers = this.ssePublisherRouterImpl.findPublishers(eventTypes);
    assertNotNull(publishers);
    assertTrue(publishers.isEmpty());
  }

  @Test
  public void testBind() {
    final SseSubscriber sseSubscriber = Mockito.mock(SseSubscriber.class);
    Mockito.doNothing().when(sseSubscriber).bindPublishers(any());
    Mockito.doNothing().when(sseSubscriber).startListening();


    final SsePublisherRouterImpl ssePublisherRouter = new SsePublisherRouterImpl();
    ssePublisherRouter.bind(sseSubscriber, Arrays.asList());

    verify(sseSubscriber, times(1)).bindPublishers(any());
    verify(sseSubscriber, times(1)).startListening();
  }


  private SseSubscriber initSseSubscriber() {
    final SseEmitter sseEmitter = this.initSseEmitter();
    final List<String> eventTypes = this.initStringsList();
    final Map<String, String> metadata = this.initMetadata();
    final String lastEventId = "lastEventId";
    final Long userId = 1L;
    final SseSubscriberProps sseSubscriberProps = this.initSseSubscriberPops();

    final SseSubscriber sseSubscriber =
        new SseSubscriber(sseEmitter, eventTypes, metadata, lastEventId, userId, sseSubscriberProps);
    return sseSubscriber;
  }

  private SseSubscriberProps initSseSubscriberPops() {
    final SseSubscriberProps sseSubscriberProps = new SseSubscriberProps();
    sseSubscriberProps.setQueueCapacity(Integer.valueOf(1));
    sseSubscriberProps.setQueueTimeout(3L);
    return sseSubscriberProps;
  }

  private Map<String, String> initMetadata() {
    final Map<String, String> metadata = new HashMap<>();
    metadata.put("key1", "value1");
    metadata.put("key2", "value2");
    metadata.put("key3", "value3");
    return metadata;
  }

  private SseEmitter initSseEmitter() {
    return new SseEmitter();
  }

  private List<String> initStringsList() {
    return Arrays.asList("string1", "string2", "string3");
  }
}
