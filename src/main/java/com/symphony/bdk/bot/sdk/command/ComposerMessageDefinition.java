package com.symphony.bdk.bot.sdk.command;

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
  ComposerAttachmentOrStreamDefinition withMessage(String message);

  /**
   * Adds an enriched message to the response composition
   *
   * @param message
   * @param entityName
   * @param entity
   * @param version
   * @return
   */
  ComposerAttachmentOrStreamDefinition withEnrichedMessage(
      String message, String entityName, Object entity, String version);

  /**
   * Adds a template message to the response composition
   *
   * @param templateMessage
   * @param templateData
   * @return
   */
  ComposerAttachmentOrStreamDefinition withTemplateMessage(String templateMessage, Object templateData);

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
  ComposerAttachmentOrStreamDefinition withEnrichedTemplateMessage(
      String templateMessage, Object templateData, String entityName, Object entity,
      String version);

  /**
   * Adds an enriched template message to the response composition
   *
   * @param templateFile
   * @param templateData
   * @return
   */
  ComposerAttachmentOrStreamDefinition withTemplateFile(String templateFile, Object templateData);

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
  ComposerAttachmentOrStreamDefinition withEnrichedTemplateFile(
      String templateFile, Object templateData, String entityName, Object entity, String version);

  /**
   * Completes response composition
   */
  void complete();

}
