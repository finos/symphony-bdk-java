package com.symphony.bdk.examples;

import static com.symphony.bdk.core.activity.command.SlashCommand.slash;
import static com.symphony.bdk.core.config.BdkConfigLoader.loadFromSymphonyDir;
import static java.util.Collections.singletonMap;

import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.service.message.model.Message;
import com.symphony.bdk.template.api.Template;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

@Slf4j
public class ComplexMessageExample {

  public static void main(String[] args) throws Exception {

    final SymphonyBdk bdk = new SymphonyBdk(loadFromSymphonyDir("config.yaml"));

    final Template template = bdk.messages().templates().newTemplateFromClasspath("/complex-template.ftl");

    bdk.activities().register(slash("/lenna", false, c ->
      bdk.messages().send(c.getStreamId(), buildMessage(template))));
    bdk.datafeed().start();
  }

  private static Message buildMessage(Template template) {
    return Message.builder()
        .template(template, singletonMap("name", "Lenna"))
        .addAttachment(
            loadAttachment("/lenna.png"),
            loadAttachment("/lenna-preview.png"),
            "lenna.png"
        )
        .addAttachment(
            loadAttachment("/lenna.png"),
            loadAttachment("/lenna-preview.png"),
            "lenna-2.png"
        )
        .build();
  }

  protected static InputStream loadAttachment(String path) {
    return ComplexMessageExample.class.getResourceAsStream(path);
  }
}
