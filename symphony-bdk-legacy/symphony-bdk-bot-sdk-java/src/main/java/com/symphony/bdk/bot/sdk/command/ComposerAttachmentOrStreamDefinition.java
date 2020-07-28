package com.symphony.bdk.bot.sdk.command;

import java.util.Collection;

import com.symphony.bdk.bot.sdk.event.model.MessageAttachmentFile;

/**
 * Defines the streams or attachments to be linked to a message
 *
 * @author Gabriel Berberian
 */
public interface ComposerAttachmentOrStreamDefinition extends ComposerStreamsDefinition {

  /**
   * Defines the attachments to be linked to a message
   *
   * @param attachments the attachments list
   * @return an instance of ComposerStreamsDefinition
   */
  ComposerStreamsDefinition withAttachments(MessageAttachmentFile... attachments);

  /**
   * Defines the attachments to be linked to a message
   *
   * @param attachments the attachments list
   * @return and instance of ComposerStreamsDefinition
   */
  ComposerStreamsDefinition withAttachments(Collection<MessageAttachmentFile> attachments);

}
