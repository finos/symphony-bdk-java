package com.symphony.template.examples;

import com.symphony.bdk.template.api.Template;
import com.symphony.bdk.template.api.TemplateException;
import com.symphony.bdk.template.freemarker.FreeMarkerEngine;

import java.util.HashMap;

/**
 * Sample class showing usage of FreeMarker templates.
 */
public class FreeMarkerExample {
  public static void main(String[] args) throws TemplateException {
    System.out.println(getStringFromBuiltInTemplate());
    System.out.println(getStringFromCustomTemplate());
  }

  private static String getStringFromBuiltInTemplate() throws TemplateException {
    Template template = new FreeMarkerEngine().newBuiltInTemplate("simpleMML");
    final String message = template.process(new HashMap<String, String>() {{
      put("message", "Hello World !");
    }});
    return message;
  }

  private static String getStringFromCustomTemplate() throws TemplateException {
    Template template = new FreeMarkerEngine().newTemplateFromClasspath("templates/customTemplate.ftl");
    final String message = template.process(new HashMap<String, String>() {{
      put("message", "Hello World");
    }});
    return message;
  }
}
