package com.symphony.bdk.template.freemarker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.symphony.bdk.template.api.Template;
import com.symphony.bdk.template.api.TemplateEngine;
import com.symphony.bdk.template.api.TemplateException;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Test class for {@link FreeMarkerEngine} and {@link FreeMarkerTemplate}
 */
public class FreeMarkerTemplateTest {

  @Test
  public void testDefaultImplementation() {
    TemplateEngine defaultImplementation = TemplateEngine.getDefaultImplementation();
    assertEquals(FreeMarkerEngine.class, defaultImplementation.getClass());
  }

  @Test
  public void testFromFile() {
    Template freeMarkerTemplate = new FreeMarkerEngine().newTemplateFromFile("./src/test/resources/subFolder/test.ftl");
    assertTemplateProducesOutput(freeMarkerTemplate);
  }

  @Test
  public void testFromFileWithInclude() {
    Template freeMarkerTemplate = new FreeMarkerEngine()
        .newTemplateFromFile("./src/test/resources/subFolder/testWithInclude.ftl");
    assertTemplateProducesOutput(freeMarkerTemplate, new HashMap<>(),
        "Template with include\nHello from included file!\n");
  }

  @Test
  public void testFromNotFoundFile() {
    assertThrows(TemplateException.class, () -> new FreeMarkerEngine().newTemplateFromFile("./not/found.ftl"));
  }

  @Test
  public void testFromClasspath() {
    Template freeMarkerTemplate = new FreeMarkerEngine().newTemplateFromClasspath("/subFolder/test.ftl");
    assertTemplateProducesOutput(freeMarkerTemplate);
  }

  @Test
  public void testFromClasspathRelativePath() {
    Template freeMarkerTemplate = new FreeMarkerEngine().newTemplateFromClasspath("subFolder/test.ftl");
    assertTemplateProducesOutput(freeMarkerTemplate);
  }

  @Test
  public void testFromClasspathRelativePathNoSlash() {
    Template freeMarkerTemplate = new FreeMarkerEngine().newTemplateFromClasspath("subFolder/test.ftl");
    assertTemplateProducesOutput(freeMarkerTemplate);
  }

  @Test
  public void testFromClasspathWithInclude() {
    Template freeMarkerTemplate = new FreeMarkerEngine().newTemplateFromClasspath("/subFolder/testWithInclude.ftl");
    assertTemplateProducesOutput(freeMarkerTemplate, new HashMap<>(),
        "Template with include\nHello from included file!\n");
  }

  @Test
  public void testFromClasspathWithObjects() {
    Class1 class1 = new Class1();
    class1.setParameter1(Collections.singletonMap("message", "Hello"));
    Map<String, Object> params = new HashMap<>();
    params.put("test", class1);

    Template freeMarkerTemplate = new FreeMarkerEngine().newTemplateFromClasspath("/subFolder/testWithObject.ftl");
    assertTemplateProducesOutput(freeMarkerTemplate, params, "Hello world!\n");
  }

  @Test
  public void testFromClasspathWithLong() {
    Long userId = 12345678L;
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("userId", userId);

    Template freeMarkerTemplate = new FreeMarkerEngine().newTemplateFromClasspath("/subFolder/testWithLong.ftl");
    assertTemplateProducesOutput(freeMarkerTemplate, parameters,
        "<messageML>Hello <mention uid='" + userId + "'/></messageML>\n");
  }

  @Test
  public void testWithNotFoundResource() {
    assertThrows(TemplateException.class, () -> new FreeMarkerEngine().newTemplateFromClasspath("./not/found.ftl"));
  }

  private void assertTemplateProducesOutput(Template freeMarkerTemplate) {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("message", "Hello World!");

    assertTemplateProducesOutput(freeMarkerTemplate, parameters, "<messageML>Hello World!</messageML>\n");
  }

  private void assertTemplateProducesOutput(Template freeMarkerTemplate, Object parameters, String expectedOutput)
      {
    assertEquals(FreeMarkerTemplate.class, freeMarkerTemplate.getClass());
    assertEquals(expectedOutput, freeMarkerTemplate.process(parameters));
  }

  public static class Class1 {
    private Map<String, String>  parameter;

    public Map<String, String> getParameter() {
      return parameter;
    }

    public void setParameter1(Map<String, String> parameter) {
      this.parameter = parameter;
    }
  }

}
