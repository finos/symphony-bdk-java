package com.symphony.bdk.template.handlebars;

import com.symphony.bdk.template.api.Template;
import com.symphony.bdk.template.api.TemplateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HandlebarsEngineTest {

  private static final String EXPECTED_TEST_HBS = "<messageML>\nhello\n</messageML>\n";

  private HandlebarsEngine engine;

  @BeforeEach
  void init() {
    this.engine = new HandlebarsEngine();
  }

  @Test
  void should_load_template_from_classpath() throws TemplateException {
    final Template template = this.engine.newTemplateFromClasspath("/test");
    final String content = template.process(Collections.singletonMap("message", "hello"));
    assertEquals(EXPECTED_TEST_HBS, content);
  }

  @Test
  void should_load_template_from_file(@TempDir Path tempDir) throws Exception {

    final Path templatePath = tempDir.resolve("test.hbs");
    Files.copy(this.getClass().getResourceAsStream("/test.hbs"), templatePath);

    final Template template = this.engine.newTemplateFromFile(tempDir.resolve("test").toAbsolutePath().toString());
    final String content = template.process(Collections.singletonMap("message", "hello"));
    assertEquals(EXPECTED_TEST_HBS, content);
  }
}
