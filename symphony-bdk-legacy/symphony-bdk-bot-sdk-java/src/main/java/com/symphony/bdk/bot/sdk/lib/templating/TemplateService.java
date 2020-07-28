package com.symphony.bdk.bot.sdk.lib.templating;

/**
 * Interface which abstracts the underlying templating engine
 *
 * @author Marcus Secato
 *
 */
public interface TemplateService {

  /**
   * Process a template file
   *
   * @param templateFile the template file name
   * @param data the template data
   * @return processed content
   */
  String processTemplateFile(String templateFile, Object data);

  /**
   * Process a template string
   *
   * @param templateString the template string
   * @param data the template data
   * @return processed content
   */
  String processTemplateString(String templateString, Object data);

}
