package com.symphony.bdk.examples;

import static com.symphony.bdk.core.activity.command.SlashCommand.slash;
import static com.symphony.bdk.core.config.BdkConfigLoader.loadFromSymphonyDir;

import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
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

    Template template = bdk.messages().templates().newTemplateFromClasspath("/complex-template.ftl");
    bdk.datafeed().subscribe(new RealTimeEventListener() {
      @Override
      public void onMessageSent(V4Initiator initiator, V4MessageSent event) {
        bdk.messages().builder()
            .template(template,Collections.singletonMap("name", event.getMessage().getUser().getUsername()))
            .attachment(IOUtils.toInputStream("This is string", StandardCharsets.UTF_8), "test.doc")
            .send(event.getMessage().getStream());
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
