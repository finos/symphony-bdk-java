package com.symphony.ms.songwriter.internal.lib.templating;

public interface TemplateService {

  String processTemplateFile(String templateFile, Object data);

  String processTemplateString(String templateString, Object data);

}
