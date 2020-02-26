package com.symphony.ms.bot.sdk.internal.command;

/**
 * Defines a message to be linked to streams
 *
 * @author Gabriel Berberian
 */
public interface ComposerMessageDefinition {

  /**
   * Adds a message to the response composition
   *
   * @param message
   * @return
   */
  ComposerStreamsDefinition withMessage(String message);

  /**
   * Adds an enriched message to the response composition
   *
   * @param message
   * @param entityName
   * @param entity
   * @param version
   * @return
   */
  ComposerStreamsDefinition withEnrichedMessage(
      String message, String entityName, Object entity, String version);

  /**
   * Adds a template message to the response composition
   *
   * @param templateMessage
   * @param templateData
   * @return
   */
  ComposerStreamsDefinition withTemplateMessage(String templateMessage, Object templateData);

  /**
   * Adds an enriched template message to the response composition
   *
   * @param templateMessage
   * @param templateData
   * @param entityName
   * @param entity
   * @param version
   * @return
   */
  ComposerStreamsDefinition withEnrichedTemplateMessage(
      String templateMessage, Object templateData, String entityName, Object entity,
      String version);

  /**
   * Adds an enriched template message to the response composition
   *
   * @param templateFile
   * @param templateData
   * @return
   */
  ComposerStreamsDefinition withTemplateFile(String templateFile, Object templateData);

  /**
   * Adds an enriched template file to the response composition
   *
   * @param templateFile
   * @param templateData
   * @param entityName
   * @param entity
   * @param version
   * @return
   */
  ComposerStreamsDefinition withEnrichedTemplateFile(
      String templateFile, Object templateData, String entityName, Object entity, String version);

  /**
   * Completes response composition
   */
  void complete();

}
