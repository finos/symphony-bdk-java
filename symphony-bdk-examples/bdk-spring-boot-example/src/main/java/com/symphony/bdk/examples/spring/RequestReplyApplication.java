package com.symphony.bdk.examples.spring;

import com.symphony.bdk.core.service.MessageService;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4MessageSent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Sample Request/Reply Application.
 */
@SpringBootApplication
public class RequestReplyApplication implements RealTimeEventListener {

  private final MessageService messageService;

  public RequestReplyApplication(MessageService messageService) {
    this.messageService = messageService;
  }

  @Override
  public void onMessageSent(V4Initiator initiator, V4MessageSent event) {
    this.messageService.send(event.getMessage().getStream(),"<messageML>Hello, from Spring!</messageML>");
  }

  public static void main(String[] args) {
    SpringApplication.run(RequestReplyApplication.class, args);
  }
}
