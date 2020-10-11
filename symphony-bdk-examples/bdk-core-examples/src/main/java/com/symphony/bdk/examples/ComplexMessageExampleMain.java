package com.symphony.bdk.examples;

import static com.symphony.bdk.core.config.BdkConfigLoader.loadFromSymphonyDir;
import static java.util.Collections.singletonMap;

import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.service.message.model.Message;
import com.symphony.bdk.template.api.Template;

import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

@Slf4j
public class ComplexMessageExampleMain {

  public static void main(String[] args) throws Exception {

    final SymphonyBdk bdk = new SymphonyBdk(loadFromSymphonyDir("config.yaml"));

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

    bdk.messages().send("jQGvjAVdoo9kXXz_KwijC3___orpavPPdA", message);
  }

  private static InputStream loadAttachment(String path) {
    return ComplexMessageExampleMain.class.getResourceAsStream(path);
  }
}
