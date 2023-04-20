package com.symphony.bdk.examples.spring;

import static java.lang.Thread.sleep;

import com.symphony.bdk.core.activity.command.CommandContext;
import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.spring.annotation.Slash;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AsyncActivity {

  @Autowired
  private MessageService messageService;

  @Slash(value = "/async", asynchronous = true)
  public void async(CommandContext context) throws InterruptedException {
    this.messageService.send(context.getStreamId(),
        "I will simulate a heavy process that takes time but this should not block next commands");

    sleep(30000);

    this.messageService.send(context.getStreamId(), "Heavy async process is done");
  }
}
