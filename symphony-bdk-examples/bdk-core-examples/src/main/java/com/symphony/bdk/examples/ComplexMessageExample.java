package com.symphony.bdk.examples;

import static com.symphony.bdk.core.activity.command.SlashCommand.slash;
import static java.util.Collections.singletonMap;

import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.service.message.model.Message;
import com.symphony.bdk.template.api.Template;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ComplexMessageExample extends BdkExample {

  @Override
  protected void run(final SymphonyBdk bdk) throws Exception {

    final Template template = bdk.messages().templates().newTemplateFromClasspath("/complex-template.ftl");

    final Message message = Message.builder()
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

    bdk.activities().register(slash("/lenna", false, c -> bdk.messages().send(c.getStreamId(), message)));
    bdk.datafeed().start();
  }

  public static void main(String[] args) {
    BdkExample.run(ComplexMessageExample.class);
  }
}
