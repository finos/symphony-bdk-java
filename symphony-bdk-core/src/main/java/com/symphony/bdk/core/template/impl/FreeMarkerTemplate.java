package com.symphony.bdk.core.template.impl;

import com.symphony.bdk.core.template.Template;
import com.symphony.bdk.core.template.TemplateException;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

public class FreeMarkerTemplate implements Template {

  private final freemarker.template.Template template;

  public FreeMarkerTemplate(String templateDirectory, String templateFile) throws IOException {
    Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);
    cfg.setDirectoryForTemplateLoading(new File(templateDirectory));
    cfg.setDefaultEncoding("UTF-8");
    cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    cfg.setLogTemplateExceptions(false);
    cfg.setWrapUncheckedExceptions(true);
    cfg.setFallbackOnNullLoopVariable(false);

    template = cfg.getTemplate(templateFile);
  }

  @Override
  public String getOutput(Map<String, String> parameterValues) throws TemplateException {
    try {
      Writer out = new StringWriter();
      template.process(parameterValues, out);
      return out.toString();
    } catch (freemarker.template.TemplateException | IOException e) {
      throw new TemplateException("Could not generate string from template", e);
    }
  }
}
