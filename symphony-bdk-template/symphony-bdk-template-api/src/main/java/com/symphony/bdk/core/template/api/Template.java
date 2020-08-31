package com.symphony.bdk.core.template.api;

import java.util.Map;

public interface Template {

  String getOutput(Map<String, String> parameterValues) throws TemplateException;
}
