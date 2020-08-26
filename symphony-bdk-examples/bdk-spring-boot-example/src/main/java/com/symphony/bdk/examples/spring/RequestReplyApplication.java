package com.symphony.bdk.examples.spring;

import com.symphony.bdk.core.service.MessageService;
import com.symphony.bdk.core.service.datafeed.DatafeedEventListener;
import com.symphony.bdk.gen.api.model.V4MessageSent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Sample Request/Reply Application.
 */
@SpringBootApplication
public class RequestReplyApplication implements DatafeedEventListener {

  @Autowired
  private MessageService messageService;

  @Override
  public void onMessageSent(V4MessageSent event) {
    this.messageService.send(event.getMessage().getStream(),"<messageML>Hello, from Spring!</messageML>");
  }

  public static void main(String[] args) {
    SpringApplication.run(RequestReplyApplication.class, args);
  }
}
