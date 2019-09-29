package com.symphony.ms.songwriter.internal.message.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

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

  /*public static MessageBuilder builder() {
   // https://javatechnicalwealth.com/blog/cascading-lambdas-and-builder-pattern-in-java-when-1-1-3-but-not-4/
  }*/

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
        || templateFile != null;
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
