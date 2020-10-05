package com.symphony.bdk.template.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for {@link TemplateResolver}
 */
@ExtendWith(MockitoExtension.class)
class TemplateResolverTest {

  public static final Template TEMPLATE = parameters -> "";
  public static final String TEMPLATE_NAME = "template";

  @Mock
  private TemplateEngine templateEngine;

  private TemplateResolver templateResolver;

  @BeforeEach
  void setUp() {
    templateResolver = new TemplateResolver(templateEngine);
  }

  @Test
  @DisplayName("Template is in file system but absent from classpath")
  void no_cp_in_fs() throws TemplateException {
    when(templateEngine.newTemplateFromClasspath(anyString())).thenThrow(new TemplateException(""));
    when(templateEngine.newTemplateFromFile(anyString())).thenReturn(TEMPLATE);

    assertEquals(TEMPLATE, templateResolver.resolve(TEMPLATE_NAME));
    verify(templateEngine).newTemplateFromClasspath(eq(TEMPLATE_NAME));
    verify(templateEngine).newTemplateFromFile(eq(TEMPLATE_NAME));
    verifyNoMoreInteractions(templateEngine);
  }

  @Test
  @DisplayName("Template is in classpath but absent from file system")
  void no_fs_in_cp() throws TemplateException {
    when(templateEngine.newTemplateFromClasspath(anyString())).thenReturn(TEMPLATE);

    assertEquals(TEMPLATE, templateResolver.resolve(TEMPLATE_NAME));
    verify(templateEngine).newTemplateFromClasspath(eq(TEMPLATE_NAME));
    verifyNoMoreInteractions(templateEngine);
  }

  @Test
  void testTemplateNotFound() throws TemplateException {
    when(templateEngine.newTemplateFromClasspath(anyString())).thenThrow(new TemplateException(""));
    when(templateEngine.newTemplateFromFile(anyString())).thenThrow(new TemplateException(""));
    assertThrows(TemplateException.class, () -> templateResolver.resolve(TEMPLATE_NAME));
    verify(templateEngine).newTemplateFromClasspath(eq(TEMPLATE_NAME));
    verify(templateEngine).newTemplateFromFile(eq(TEMPLATE_NAME));
    verifyNoMoreInteractions(templateEngine);
  }
}
