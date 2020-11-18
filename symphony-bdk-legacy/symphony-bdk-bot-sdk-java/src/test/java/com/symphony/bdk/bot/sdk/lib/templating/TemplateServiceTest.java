package com.symphony.bdk.bot.sdk.lib.templating;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.*;

@ExtendWith(MockitoExtension.class)
public class TemplateServiceTest {

  @Mock
  private Handlebars handlebars;

  @InjectMocks
  private TemplateService templateService = new TemplateServiceImpl(handlebars);

  @Mock
  private Template template;

  @Test
  public void shouldProcessTemplateFile() throws IOException {
    when(handlebars.compile(anyString())).thenReturn(template);

    templateService.processTemplateFile("templateFile", "data");

    verify(handlebars, times(1)).compile(anyString());
    verify(template, times(1)).apply(any(Object.class));
  }

  @Test
  public void shouldProcessTemplateString() throws IOException {
    when(handlebars.compileInline(anyString())).thenReturn(template);

    templateService.processTemplateString("templateFile", "data");

    verify(handlebars, times(1)).compileInline(anyString());
    verify(template, times(1)).apply(any(Object.class));
  }

  @SneakyThrows
  @Test
  public void processTemplateFileFailed() {
    when(handlebars.compile(anyString())).thenThrow(IOException.class);

    Assertions.assertThrows(NullPointerException.class, () -> {
      templateService.processTemplateFile("templateFile", "data");
    });
    verify(handlebars, times(1)).compile(anyString());
  }

  @SneakyThrows
  @Test
  public void processTemplateStringFailed() {
    when(handlebars.compileInline(anyString())).thenThrow(IOException.class);

    Assertions.assertThrows(NullPointerException.class, () -> {
      templateService.processTemplateString("templateString", "data");
    });
    verify(handlebars, times(1)).compileInline(anyString());
  }

  @Test
  public void applyDataToTemplate() throws IOException {
    when(template.apply(any(Object.class))).thenThrow(IOException.class);
    when(handlebars.compileInline(anyString())).thenReturn(template);

    Assertions.assertThrows(TemplateProcessingException.class, () -> {
      templateService.processTemplateString("templateString", "data");
    });
    verify(handlebars, times(1)).compileInline(anyString());
    verify(template, times(1)).apply(any(Object.class));
  }

  @Test
  public void processTemplateFileTest() {
    HashMap<String, Object> data = new HashMap<>();
    data.put("testList", Arrays.asList("Test 1", "Test 2", "Test 3"));

    TemplateLoader loader = new ClassPathTemplateLoader("/templates", ".hbs");
    TemplateService service = new TemplateServiceImpl(new Handlebars(loader));
    String result = service.processTemplateFile("testTemplate", data);
    assertEquals(result, "<ul><li>Test 1</li><li>Test 2</li><li>Test 3</li></ul>" + System.lineSeparator());
  }

  @Test
  public void processTemplateStringTest() {
    HashMap<String, Object> data = new HashMap<>();
    data.put("title", "Test Title");
    data.put("name", "Test");

    TemplateService service = new TemplateServiceImpl(new Handlebars());
    String result = service.processTemplateString("<h1>{{title}}</h1><p>Name: {{name}}</p>", data);
    assertEquals(result, "<h1>Test Title</h1><p>Name: Test</p>");
  }

}
