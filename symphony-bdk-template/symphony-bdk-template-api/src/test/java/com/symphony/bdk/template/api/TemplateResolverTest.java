package com.symphony.bdk.template.api;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

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
  void testBuiltInTemplate() throws TemplateException {
    when(templateEngine.getBuiltInTemplates()).thenReturn(Collections.singleton(TEMPLATE_NAME));
    when(templateEngine.newBuiltInTemplate(anyString())).thenReturn(TEMPLATE);

    assertEquals(TEMPLATE, templateResolver.resolve(TEMPLATE_NAME));
    verify(templateEngine).getBuiltInTemplates();
    verify(templateEngine).newBuiltInTemplate(eq(TEMPLATE_NAME));
    verifyNoMoreInteractions(templateEngine);
  }

  @Test
  void testNotInBuiltInTemplatesButInClasspath() throws TemplateException {
    when(templateEngine.getBuiltInTemplates()).thenReturn(Collections.emptySet());
    when(templateEngine.newTemplateFromClasspath(anyString())).thenReturn(TEMPLATE);

    assertEquals(TEMPLATE, templateResolver.resolve(TEMPLATE_NAME));
    verify(templateEngine).getBuiltInTemplates();
    verify(templateEngine).newTemplateFromClasspath(eq(TEMPLATE_NAME));
    verifyNoMoreInteractions(templateEngine);
  }

  @Test
  void testNewBuiltInTemplatesThrowsTemplateExceptionButTemplateInClasspath() throws TemplateException {
    when(templateEngine.getBuiltInTemplates()).thenReturn(Collections.singleton(TEMPLATE_NAME));
    when(templateEngine.newBuiltInTemplate(anyString())).thenThrow(new TemplateException(""));
    when(templateEngine.newTemplateFromClasspath(anyString())).thenReturn(TEMPLATE);

    assertEquals(TEMPLATE, templateResolver.resolve(TEMPLATE_NAME));
    verify(templateEngine).getBuiltInTemplates();
    verify(templateEngine).newBuiltInTemplate(eq(TEMPLATE_NAME));
    verify(templateEngine).newTemplateFromClasspath(eq(TEMPLATE_NAME));
    verifyNoMoreInteractions(templateEngine);
  }

  @Test
  void testNotBuiltInNotInClasspathButInFileSystem() throws TemplateException {
    when(templateEngine.getBuiltInTemplates()).thenReturn(Collections.emptySet());
    when(templateEngine.newTemplateFromClasspath(anyString())).thenThrow(new TemplateException(""));
    when(templateEngine.newTemplateFromFile(anyString())).thenReturn(TEMPLATE);

    assertEquals(TEMPLATE, templateResolver.resolve(TEMPLATE_NAME));
    verify(templateEngine).getBuiltInTemplates();
    verify(templateEngine).newTemplateFromClasspath(eq(TEMPLATE_NAME));
    verify(templateEngine).newTemplateFromFile(eq(TEMPLATE_NAME));
    verifyNoMoreInteractions(templateEngine);
  }

  @Test
  void testNotBuiltInNotInClasspathNotInFileSystemButFromUrl() throws TemplateException {
    when(templateEngine.getBuiltInTemplates()).thenReturn(Collections.emptySet());
    when(templateEngine.newTemplateFromClasspath(anyString())).thenThrow(new TemplateException(""));
    when(templateEngine.newTemplateFromFile(anyString())).thenThrow(new TemplateException(""));
    when(templateEngine.newTemplateFromUrl(anyString())).thenReturn(TEMPLATE);

    assertEquals(TEMPLATE, templateResolver.resolve(TEMPLATE_NAME));
    verify(templateEngine).getBuiltInTemplates();
    verify(templateEngine).newTemplateFromClasspath(eq(TEMPLATE_NAME));
    verify(templateEngine).newTemplateFromFile(eq(TEMPLATE_NAME));
    verify(templateEngine).newTemplateFromUrl(eq(TEMPLATE_NAME));
    verifyNoMoreInteractions(templateEngine);
  }

  @Test
  void testTemplateNotFound() throws TemplateException {
    when(templateEngine.getBuiltInTemplates()).thenReturn(Collections.emptySet());
    when(templateEngine.newTemplateFromClasspath(anyString())).thenThrow(new TemplateException(""));
    when(templateEngine.newTemplateFromFile(anyString())).thenThrow(new TemplateException(""));
    when(templateEngine.newTemplateFromUrl(anyString())).thenThrow(new TemplateException(""));

    assertThrows(TemplateException.class, () -> templateResolver.resolve(TEMPLATE_NAME));
    verify(templateEngine).getBuiltInTemplates();
    verify(templateEngine).newTemplateFromClasspath(eq(TEMPLATE_NAME));
    verify(templateEngine).newTemplateFromFile(eq(TEMPLATE_NAME));
    verify(templateEngine).newTemplateFromUrl(eq(TEMPLATE_NAME));
    verifyNoMoreInteractions(templateEngine);
  }
}
