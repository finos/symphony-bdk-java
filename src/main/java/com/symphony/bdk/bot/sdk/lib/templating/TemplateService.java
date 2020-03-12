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
   * @param templateFile
   * @param data
   * @return processed content
   */
  String processTemplateFile(String templateFile, Object data);

  /**
   * Process a template string
   *
   * @param templateString
   * @param data
   * @return processed content
   */
  String processTemplateString(String templateString, Object data);

}
