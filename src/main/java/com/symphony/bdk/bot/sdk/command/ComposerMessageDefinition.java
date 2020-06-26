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
   * @param message the message
   * @return an instance of ComposerAttachmentOrStreamDefinition 
   */
  ComposerAttachmentOrStreamDefinition withMessage(String message);

  /**
   * Adds an enriched message to the response composition
   *
   * @param message the message
   * @param entityName the entity name
   * @param entity the data entity
   * @param version the entity version
   * @return an instance of ComposerAttachmentOrStreamDefinition
   */
  ComposerAttachmentOrStreamDefinition withEnrichedMessage(
      String message, String entityName, Object entity, String version);

  /**
   * Adds a template message to the response composition
   *
   * @param templateMessage the template message
   * @param templateData the template data
   * @return an instance of ComposerAttachmentOrStreamDefinition
   */
  ComposerAttachmentOrStreamDefinition withTemplateMessage(String templateMessage, Object templateData);

  /**
   * Adds an enriched template message to the response composition
   *
   * @param templateMessage the template message
   * @param templateData the template data
   * @param entityName the entity name
   * @param entity the data entity 
   * @param version the entity version 
   * @return an instance of ComposerAttachmentOrStreamDefinition
   */
  ComposerAttachmentOrStreamDefinition withEnrichedTemplateMessage(
      String templateMessage, Object templateData, String entityName, Object entity,
      String version);

  /**
   * Adds an enriched template message to the response composition
   *
   * @param templateFile the template file name
   * @param templateData the template data
   * @return an instance of ComposerAttachmentOrStreamDefinition
   */
  ComposerAttachmentOrStreamDefinition withTemplateFile(String templateFile, Object templateData);

  /**
   * Adds an enriched template file to the response composition
   *
   * @param templateFile the template file name
   * @param templateData the template data
   * @param entityName the entity name
   * @param entity the data entity
   * @param version the entity version 
   * @return an instance of ComposerAttachmentOrStreamDefinition
   */
  ComposerAttachmentOrStreamDefinition withEnrichedTemplateFile(
      String templateFile, Object templateData, String entityName, Object entity, String version);

  /**
   * Completes response composition
   */
  void complete();

}
