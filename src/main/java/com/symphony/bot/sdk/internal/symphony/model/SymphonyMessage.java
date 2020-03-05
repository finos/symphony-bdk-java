package com.symphony.bot.sdk.internal.symphony.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

import com.symphony.bot.sdk.internal.event.model.MessageAttachmentFile;

/**
 * A message to be sent to Symphony
 *
 * @author Marcus Secato
 *
 */
@Getter
@NoArgsConstructor
public class SymphonyMessage {

  private String message;
  private String templateString;
  private String templateFile;
  private Object templateData;
  private String entityName;
  private Object entity;
  private String version;
  @Setter private List<MessageAttachmentFile> attachments;

  public SymphonyMessage(String message) {
    this.message = message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public void setEnrichedMessage(String message, String entityName,
      Object entity, String version) {
    this.message = message;
    this.entityName = entityName;
    this.entity = entity;
    this.version = version;
  }

  public void setTemplateMessage(String templateMessage, Object templateData) {
    this.templateString = templateMessage;
    this.templateData = templateData;
  }

  public void setEnrichedTemplateMessage(String templateMessage,
      Object templateData, String entityName, Object entity, String version) {
    this.templateString = templateMessage;
    this.templateData = templateData;
    this.entityName = entityName;
    this.entity = entity;
    this.version = version;
  }

  public void setTemplateFile(String templateFile, Object templateData) {
    this.templateFile = templateFile;
    this.templateData = templateData;
  }

  public void setEnrichedTemplateFile(String templateFile,
      Object templateData, String entityName, Object entity, String version) {
    this.templateFile = templateFile;
    this.templateData = templateData;
    this.entityName = entityName;
    this.entity = entity;
    this.version = version;
  }

  public boolean hasContent() {
    return message != null
        || templateString != null
        || templateFile != null
        || attachments != null;
  }

  public boolean hasTemplate() {
    return templateFile != null
        || templateString != null;
  }

  public boolean usesTemplateFile() {
    return templateFile != null;
  }

  public boolean isEnrichedMessage() {
    return entityName != null
        && entity != null
        && version != null;
  }

}
