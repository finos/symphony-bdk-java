package com.symphony.bot.sdk.internal.lib.templating;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.symphony.bot.sdk.internal.lib.templating.TemplateService;
import com.symphony.bot.sdk.internal.lib.templating.TemplateServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

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

}
