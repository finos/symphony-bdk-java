package com.symphony.bdk.core.template.freemarker;



import com.symphony.bdk.core.template.api.TemplateException;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FreeMarkerTemplateTest {

  @Test
  public void testFreeMarker() throws IOException, TemplateException {
    FreeMarkerTemplate freeMarkerTemplate = new FreeMarkerTemplate("./src/test/resources", "test.ftlh");

    Map<String, String> root = new HashMap<>();
    root.put("message", "Hello World!");

    assertEquals("<messageML>Hello World!</messageML>\n", freeMarkerTemplate.getOutput(root));
  }
}
