package com.symphony.bdk.examples;

import static com.symphony.bdk.core.activity.command.SlashCommand.slash;
import static com.symphony.bdk.core.config.BdkConfigLoader.loadFromSymphonyDir;

import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.core.service.message.model.Attachment;
import com.symphony.bdk.core.service.message.model.Message;
import com.symphony.bdk.core.service.message.model.MessageBuilder;
import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4MessageSent;
import com.symphony.bdk.template.api.Template;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Slf4j
public class ComplexMessageExampleMain {

  public static void main(String[] args) throws Exception {

    final SymphonyBdk bdk = new SymphonyBdk(loadFromSymphonyDir("config.yaml"));

    Attachment attachment =
        new Attachment().inputStream(IOUtils.toInputStream("This is string", StandardCharsets.UTF_8))
            .fileName("test.doc");
    Template template = bdk.messages().templates().newTemplateFromClasspath("/complex-message.ftl");
    Message message = MessageBuilder.fromTemplate(template, Collections.singletonMap("name", "Freemarker"))
        .attachment(attachment)
        .build();
    bdk.datafeed().subscribe(new RealTimeEventListener() {
      @Override
      public void onMessageSent(V4Initiator initiator, V4MessageSent event) {
        bdk.messages().send(event.getMessage().getStream(), message);
      }
    });

    bdk.activities()
        .register(slash("/hello",
            context -> bdk.messages().send(context.getStreamId(), "<messageML>Hello, World!</messageML>")));

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      log.info("Stopping Datafeed...");
      bdk.datafeed().stop();
    }));

    bdk.datafeed().start();
  }
}
