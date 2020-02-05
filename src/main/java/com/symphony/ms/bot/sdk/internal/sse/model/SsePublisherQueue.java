package com.symphony.ms.bot.sdk.internal.sse.model;

import com.symphony.ms.bot.sdk.internal.sse.SsePublisher;

import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * The queue managed by {@link SsePublisher}
 */
@Data
public class SsePublisherQueue {

  private List<String> streams;
  private Queue<SseEvent> queue;

  public SsePublisherQueue() {
    this.streams = new ArrayList<>();
    this.queue = new LinkedList<>();
  }

  public SsePublisherQueue(List<String> streams) {
    this.streams = streams;
    this.queue = new LinkedList<>();
  }

  public void addEvent(SseEvent event) {
    queue.add(event);
  }

  public boolean hasStream(String stream) {
    return streams == null || stream.isEmpty() || streams.contains(stream);
  }

  public boolean isEmpty() {
    return queue.isEmpty();
  }

  public SseEvent poll() {
    return queue.poll();
  }

}
