package com.symphony.bdk.bot.sdk.sse;

import static org.mockito.ArgumentMatchers.any;

import com.symphony.bdk.bot.sdk.sse.model.SsePublishable;
import com.symphony.bdk.bot.sdk.sse.model.SubscriptionEvent;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SsePublisherTest {

  private SsePublisher ssePublisher;

  @Before
  public void initSsePublisher(){
    this.ssePublisher = Mockito.mock(SsePublisher.class, Mockito.CALLS_REAL_METHODS);
  }

  @Test
  public void testInit(){
    this.ssePublisher.init();
  }

  @Test
  public void testAddSubscriberInANonEmptyList(){
    final SseSubscriber sseSubscriber = this.initSseSubscriber();

    final MySsePublisher mySsePublisher = new MySsePublisher();
    final ConcurrentHashMap<String, List<SseSubscriber>> subscribers = this.initSubscribersWithData();
    mySsePublisher.setSubscribers(subscribers);
    mySsePublisher.addSubscriber(sseSubscriber);
  }

  @Test
  public void testAddSubscriberInAnEmptyList(){
    final SseSubscriber sseSubscriber = this.initSseSubscriber();

    final MySsePublisher mySsePublisher = new MySsePublisher();
    final ConcurrentHashMap<String, List<SseSubscriber>> subscribers = this.initSubscribersWithoutData();
    mySsePublisher.setSubscribers(subscribers);
    mySsePublisher.addSubscriber(sseSubscriber);
  }

  @Test
  public void testRemoveSubscriberInANonEmptyList(){
    final SseSubscriber sseSubscriber = this.initSseSubscriber();

    final MySsePublisher mySsePublisher = new MySsePublisher();
    final ConcurrentHashMap<String, List<SseSubscriber>> subscribers = this.initSubscribersWithData();
    mySsePublisher.setSubscribers(subscribers);
    mySsePublisher.removeSubscriber(sseSubscriber);
  }

  @Test
  public void testRemoveSubscriberInAnEmptyList(){
    final SseSubscriber sseSubscriber = this.initSseSubscriber();

    final MySsePublisher mySsePublisher = new MySsePublisher();
    final ConcurrentHashMap<String, List<SseSubscriber>> subscribers = this.initSubscribersWithoutData();
    mySsePublisher.setSubscribers(subscribers);
    mySsePublisher.removeSubscriber(sseSubscriber);
  }

  @Test
  public void testPublishEventWithAnEmptyList(){
    final MySsePublisher mySsePublisher = Mockito.mock(MySsePublisher.class);
    Mockito.doNothing().when(mySsePublisher).publishEvent(any());
    final ConcurrentHashMap<String, List<SseSubscriber>> subscribers = this.initSubscribersWithoutData();
    mySsePublisher.setSubscribers(subscribers);

    final SsePublishable ssePublishable = this.initSsePublishable();
    final int subscribersSize = subscribers.size();

    mySsePublisher.publishEvent(ssePublishable);
    Mockito.verify(mySsePublisher, Mockito.times(subscribersSize)).handleEvent(any(SseSubscriber.class), any(SsePublishable.class));
  }

  @Test
  public void testPublishEventWithANonEmptyList(){
    final ConcurrentHashMap<String, List<SseSubscriber>> subscribers = this.initSubscribersWithData();

    final MySsePublisher mySsePublisher = Mockito.spy(new MySsePublisher());
    Mockito.doNothing().when(mySsePublisher).handleEvent(any(), any());
    mySsePublisher.setSubscribers(subscribers);

    final SsePublishable ssePublishable = this.initSsePublishable();

    final int subscribersSize = subscribers.get(ssePublishable.getType()).size();

    mySsePublisher.publishEvent(ssePublishable);
    Mockito.verify(mySsePublisher, Mockito.times(subscribersSize)).handleEvent(any(SseSubscriber.class), any(SsePublishable.class));
  }

  @Test
  public void testCompleteWithoutData(){
    final SseSubscriber sseSubscriber = Mockito.mock(SseSubscriber.class);
    Mockito.doNothing().when(sseSubscriber).complete(any());

    final ConcurrentHashMap<String, List<SseSubscriber>> subscribers = new ConcurrentHashMap<>();

    final MySsePublisher mySsePublisher = new MySsePublisher();
    mySsePublisher.setSubscribers(subscribers);
    mySsePublisher.complete();

    Mockito.verify(sseSubscriber, Mockito.times(0)).complete(any(SsePublisher.class));
  }

  @Test
  public void testCompleteWithData(){
    final SseSubscriber sseSubscriber = Mockito.mock(SseSubscriber.class);
    Mockito.doNothing().when(sseSubscriber).complete(any());

    final List<SseSubscriber> sseSubscribers = Collections.singletonList(sseSubscriber);
    final ConcurrentHashMap<String, List<SseSubscriber>> subscribers = new ConcurrentHashMap<>();
    subscribers.put("string1", sseSubscribers);

    final MySsePublisher mySsePublisher = new MySsePublisher();
    mySsePublisher.setSubscribers(subscribers);
    mySsePublisher.complete();

    Mockito.verify(sseSubscriber, Mockito.times(1)).complete(any(SsePublisher.class));
  }

  @Test
  public void testCompleteWithErrorWithoutData(){
    final SseSubscriber sseSubscriber = Mockito.mock(SseSubscriber.class);
    Mockito.doNothing().when(sseSubscriber).completeWithError(any(), any());

    final ConcurrentHashMap<String, List<SseSubscriber>> subscribers = new ConcurrentHashMap<>();

    final MySsePublisher mySsePublisher = new MySsePublisher();
    mySsePublisher.setSubscribers(subscribers);
    mySsePublisher.completeWithError(new Exception());

    Mockito.verify(sseSubscriber, Mockito.times(0)).completeWithError(any(SsePublisher.class), any(Exception.class));
  }

  @Test
  public void testCompleteWithErrorWithData(){
    final SseSubscriber sseSubscriber = Mockito.mock(SseSubscriber.class);
    Mockito.doNothing().when(sseSubscriber).completeWithError(any(), any());

    final List<SseSubscriber> sseSubscribers = Collections.singletonList(sseSubscriber);
    final ConcurrentHashMap<String, List<SseSubscriber>> subscribers = new ConcurrentHashMap<>();
    subscribers.put("string1", sseSubscribers);

    final MySsePublisher mySsePublisher = new MySsePublisher();
    mySsePublisher.setSubscribers(subscribers);
    mySsePublisher.completeWithError(new Exception());

    Mockito.verify(sseSubscriber, Mockito.times(1)).completeWithError(any(SsePublisher.class), any(Exception.class));
  }

  @Test
  public void testOnSubscriberAdded(){
    final MySsePublisher mySsePublisher = new MySsePublisher();

    final SubscriptionEvent subscriptionEvent = Mockito.mock(SubscriptionEvent.class);
    Mockito.when(subscriptionEvent.getUserId()).thenReturn(1L);

    mySsePublisher.onSubscriberAdded(subscriptionEvent);
  }

  @Test
  public void testOnSubscriberRemoved(){
    final MySsePublisher mySsePublisher = new MySsePublisher();

    final SubscriptionEvent subscriptionEvent = Mockito.mock(SubscriptionEvent.class);
    Mockito.when(subscriptionEvent.getUserId()).thenReturn(1L);

    mySsePublisher.onSubscriberRemoved(subscriptionEvent);
  }

  @Test
  public void testGetEventType(){
    final MySsePublisher mySsePublisher = new MySsePublisher();
    Assert.assertEquals(Arrays.asList("string1", "string2", "string3"), mySsePublisher.getEventTypes());
  }

  @Test
  public void testHandleEvent(){
    final ConcurrentHashMap<String, List<SseSubscriber>> subscribers = this.initSubscribersWithData();
    final SseSubscriber sseSubscriber = this.initSseSubscriber();

    final MySsePublisher mySsePublisher = Mockito.spy(new MySsePublisher());

    final SsePublishable ssePublishable = this.initSsePublishable();
    Mockito.doNothing().when(mySsePublisher).handleEvent(any(), any());
    mySsePublisher.setSubscribers(subscribers);

    mySsePublisher.handleEvent(sseSubscriber, ssePublishable);
  }

  private SsePublishable initSsePublishable() {
    final SsePublishable ssePublishable = Mockito.mock(SsePublishable.class);
    Mockito.when(ssePublishable.getType()).thenReturn("string1");
    Mockito.when(ssePublishable.getId()).thenReturn("1");
    return ssePublishable;
  }

  private ConcurrentHashMap<String, List<SseSubscriber>> initSubscribersWithoutData() {
    final ConcurrentHashMap<String, List<SseSubscriber>> subscribers = new ConcurrentHashMap<>();
    return subscribers;
  }

  private ConcurrentHashMap<String, List<SseSubscriber>> initSubscribersWithData() {
    final SseSubscriber sseSubscriber1 = this.initSseSubscriber();
    final SseSubscriber sseSubscriber2 = this.initSseSubscriber();
    final List<SseSubscriber> sseSubscribers = Arrays.asList(sseSubscriber1, sseSubscriber2);
    final String string = "string1";
    final ConcurrentHashMap<String, List<SseSubscriber>> subscribers = new ConcurrentHashMap<>();
    subscribers.put(string, sseSubscribers);
    return subscribers;
  }

  private SseSubscriber initSseSubscriber() {
    final SseSubscriber sseSubscriber = Mockito.mock(SseSubscriber.class);
    Mockito.doNothing().when(sseSubscriber).complete(any());
    final MySsePublisher mySsePublisher = new MySsePublisher();
    sseSubscriber.bindPublishers(Collections.singletonList(mySsePublisher));
    return sseSubscriber;
  }
}

class MySsePublisher extends SsePublisher {

  @Override
  public List<String> getEventTypes() {
    return Arrays.asList("string1", "string2", "string3");
  }

  @Override
  protected void handleEvent(SseSubscriber subscriber, SsePublishable event){}
}
