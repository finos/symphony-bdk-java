package com.symphony.bdk.template.handlebars;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.symphony.bdk.template.api.Template;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

class HandlebarsEngineTest {

  private static final String EXPECTED_TEST_HBS = "<messageML>\nhello\n</messageML>\n";

  private HandlebarsEngine engine;

  @BeforeEach
  void init() {
    this.engine = new HandlebarsEngine();
  }

  @Test
  void should_load_template_from_classpath() {
    final Template template = this.engine.newTemplateFromClasspath("/test.hbs");
    final String content = template.process(Collections.singletonMap("message", "hello"));
    assertEquals(EXPECTED_TEST_HBS, content.replace("\r\n", "\n"));
  }

  @Test
  void should_load_complex_template_from_classpath() {
    final Template template = this.engine.newTemplateFromClasspath("/home.hbs");
    final String home = template.process(null);
    assertTrue(home.contains("Powered by Handlebars.java")); // which is contained in base.hbs
  }

  @Test
  void should_load_template_from_file(@TempDir Path tempDir) throws Exception {

    final Path templatePath = tempDir.resolve("test.hbs");
    Files.copy(this.getClass().getResourceAsStream("/test.hbs"), templatePath);

    final Template template = this.engine.newTemplateFromFile(tempDir.resolve("test.hbs").toAbsolutePath().toString());
    final String content = template.process(Collections.singletonMap("message", "hello"));
    assertEquals(EXPECTED_TEST_HBS, content.replace("\r\n", "\n"));
  }

  @Test
  void should_load_template_from_inline_string() throws Exception {
    final Template template = this.engine.newTemplateFromString("<messageML>\n{{message}}\n</messageML>\n");
    final String content = template.process(Collections.singletonMap("message", "hello"));
    assertEquals(EXPECTED_TEST_HBS, content);
  }

  @Test
  void should_load_complex_template_from_file(@TempDir Path tempDir) throws Exception {

    // copy /base.hbs and /home.hbs from classpath to tempDir
    Path templatePath = tempDir.resolve("home.hbs");
    Files.copy(this.getClass().getResourceAsStream("/home.hbs"), templatePath);
    templatePath = tempDir.resolve("base.hbs");
    Files.copy(this.getClass().getResourceAsStream("/base.hbs"), templatePath);

    final Template template = this.engine.newTemplateFromFile(tempDir.resolve("home.hbs").toAbsolutePath().toString());
    final String home = template.process(null);
    assertTrue(home.contains("Powered by Handlebars.java")); // which is contained in base.hbs
  }
}
