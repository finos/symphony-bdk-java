package com.symphony.ms.bot.sdk.internal.command;

import com.symphony.ms.bot.sdk.internal.event.model.MessageAttachmentFile;
import com.symphony.ms.bot.sdk.internal.symphony.model.SymphonyMessage;

import lombok.Getter;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of {@link MultiResponseComposer}
 *
 * @author Gabriel Berberian
 */
public class MultiResponseComposerImpl implements MultiResponseComposer, ComposerMessageDefinition,
    ComposerAttachmentOrStreamDefinition, ComposerStreamsDefinition {

  @Getter private boolean complete;

  @Getter private Map<SymphonyMessage, Set<String>> composedResponse;
  private SymphonyMessage message;

  public MultiResponseComposerImpl() {
    this.complete = true;
  }

  @Override
  public ComposerMessageDefinition compose() {
    this.composedResponse = new HashMap<>();
    this.complete = false;
    return this;
  }

  @Override
  public ComposerAttachmentOrStreamDefinition withMessage(String message) {
    this.message = new SymphonyMessage(message);
    return this;
  }

  @Override
  public ComposerAttachmentOrStreamDefinition withEnrichedMessage(String message, String entityName,
      Object entity, String version) {
    this.message = new SymphonyMessage();
    this.message.setEnrichedMessage(message, entityName, entity, version);
    return this;
  }

  @Override
  public ComposerAttachmentOrStreamDefinition withTemplateMessage(String templateMessage,
      Object templateData) {
    this.message = new SymphonyMessage();
    this.message.setTemplateMessage(templateMessage, templateData);
    return this;
  }

  @Override
  public ComposerAttachmentOrStreamDefinition withEnrichedTemplateMessage(String templateMessage,
      Object templateData, String entityName, Object entity, String version) {
    this.message = new SymphonyMessage();
    this.message.setEnrichedTemplateMessage(
        templateMessage, templateData, entityName, entity, version);
    return this;
  }

  @Override
  public ComposerAttachmentOrStreamDefinition withTemplateFile(String templateFile,
      Object templateData) {
    this.message = new SymphonyMessage();
    this.message.setTemplateFile(templateFile, templateData);
    return this;
  }

  @Override
  public ComposerAttachmentOrStreamDefinition withEnrichedTemplateFile(String templateFile,
      Object templateData, String entityName, Object entity, String version) {
    this.message = new SymphonyMessage();
    this.message.setEnrichedTemplateFile(templateFile, templateData, entityName, entity, version);
    return this;
  }

  @Override
  public ComposerMessageDefinition toStreams(String... streamIds) {
    composedResponse.put(message, Arrays.stream(streamIds).collect(Collectors.toSet()));
    return this;
  }

  @Override
  public ComposerMessageDefinition toStreams(Collection<String> streamIds) {
    composedResponse.put(message, new HashSet<>(streamIds));
    return this;
  }

  @Override
  public ComposerStreamsDefinition withAttachments(MessageAttachmentFile... attachments) {
    this.message.setAttachments(Arrays.asList(attachments));
    return this;
  }

  @Override
  public ComposerStreamsDefinition withAttachments(Collection<MessageAttachmentFile> attachments) {
    this.message.setAttachments(attachments.stream().collect(Collectors.toList()));
    return this;
  }

  @Override
  public void complete() {
    this.complete = true;
  }

  protected boolean hasContent() {
    return composedResponse != null && !composedResponse.isEmpty();
  }
}
