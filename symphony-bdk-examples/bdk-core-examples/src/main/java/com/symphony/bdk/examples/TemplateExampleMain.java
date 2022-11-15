package com.symphony.bdk.examples;

import static com.symphony.bdk.core.config.BdkConfigLoader.loadFromSymphonyDir;

import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.template.api.Template;

import lombok.extern.slf4j.Slf4j;

import java.util.Collections;

/**
 * Template API usage.
 */
@Slf4j
public class TemplateExampleMain {

  public static void main(String[] args) throws Exception {

    // load TemplateEngine implementation using SPI
    final SymphonyBdk bdk = new SymphonyBdk(loadFromSymphonyDir("config.yaml"));

    // load template from classpath location
    final Template template = bdk.messages().templates().newTemplateFromClasspath("/complex-message.ftl");

    // process template with some vars and retrieve content
    final String content = template.process(Collections.singletonMap("name", "Freemarker"));

    // display processed template content
    log.info(content);

    // load inline template
    final Template inlineTemplate = bdk.messages().templates().newTemplateFromString("<messageML>\n"
        + "    This is a complex message, that supports ${name} templating\n"
        + "</messageML>");

    // process template with some vars and retrieve content
    final String inlineContent = inlineTemplate.process(Collections.singletonMap("name", "Freemarker"));

    // display processed template content
    log.info(inlineContent);
  }
}
