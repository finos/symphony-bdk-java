package com.symphony.bdk.examples.template;

import com.symphony.bdk.template.api.Template;
import com.symphony.bdk.template.api.TemplateEngine;
import com.symphony.bdk.template.api.TemplateException;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

/**
 * Sample class showing usage of FreeMarker templates.
 */
@Slf4j
public class FreeMarkerExample {
  public static void main(String[] args) throws TemplateException {
    log.info("String generated from built-in template: {}", getStringFromBuiltInTemplate());
    log.info("String generated from custom template: {}", getStringFromCustomTemplate());
  }

  private static String getStringFromBuiltInTemplate() throws TemplateException {
    Template template = TemplateEngine.getDefaultImplementation().newBuiltInTemplate("simpleMML");
    final String message = template.process(new HashMap<String, String>() {{
      put("message", "Hello World !");
    }});
    return message;
  }

  private static String getStringFromCustomTemplate() throws TemplateException {
    Template template = TemplateEngine.getDefaultImplementation()
        .newTemplateFromClasspath("templates/customTemplate.ftl");
    final String message = template.process(new HashMap<String, String>() {{
      put("message", "Hello World");
    }});
    return message;
  }
}
