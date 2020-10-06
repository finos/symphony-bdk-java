package com.symphony.bdk.template.freemarker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.symphony.bdk.template.api.Template;
import com.symphony.bdk.template.api.TemplateEngine;
import com.symphony.bdk.template.api.TemplateException;

import org.junit.jupiter.api.Test;

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
  public void testFromFile() throws TemplateException {
    Template freeMarkerTemplate = new FreeMarkerEngine().newTemplateFromFile("./src/test/resources/subFolder/test.ftl");
    assertTemplateProducesOutput(freeMarkerTemplate);
  }

  @Test
  public void testFromFileWithInclude() throws TemplateException {
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
  public void testFromClasspath() throws TemplateException {
    Template freeMarkerTemplate = new FreeMarkerEngine().newTemplateFromClasspath("/subFolder/test.ftl");
    assertTemplateProducesOutput(freeMarkerTemplate);
  }

  @Test
  public void testFromClasspathRelativePath() throws TemplateException {
    Template freeMarkerTemplate = new FreeMarkerEngine().newTemplateFromClasspath("subFolder/test.ftl");
    assertTemplateProducesOutput(freeMarkerTemplate);
  }

  @Test
  public void testFromClasspathRelativePathNoSlash() throws TemplateException {
    Template freeMarkerTemplate = new FreeMarkerEngine().newTemplateFromClasspath("subFolder/test.ftl");
    assertTemplateProducesOutput(freeMarkerTemplate);
  }

  @Test
  public void testFromClasspathWithInclude() throws TemplateException {
    Template freeMarkerTemplate = new FreeMarkerEngine().newTemplateFromClasspath("/subFolder/testWithInclude.ftl");
    assertTemplateProducesOutput(freeMarkerTemplate, new HashMap<>(),
        "Template with include\nHello from included file!\n");
  }

  @Test
  public void testWithNotFoundResource() {
    assertThrows(TemplateException.class, () -> new FreeMarkerEngine().newTemplateFromClasspath("./not/found.ftl"));
  }

  private void assertTemplateProducesOutput(Template freeMarkerTemplate) throws TemplateException {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("message", "Hello World!");

    assertTemplateProducesOutput(freeMarkerTemplate, parameters, "<messageML>Hello World!</messageML>\n");
  }

  private void assertTemplateProducesOutput(Template freeMarkerTemplate, Object parameters, String expectedOutput)
      throws TemplateException {
    assertEquals(FreeMarkerTemplate.class, freeMarkerTemplate.getClass());
    assertEquals(expectedOutput, freeMarkerTemplate.process(parameters));
  }

}
