package com.symphony.bot.sdk.internal.command;

import java.util.Collection;

import com.symphony.bot.sdk.internal.event.model.MessageAttachmentFile;

/**
 * Defines the streams or attachments to be linked to a message
 *
 * @author Gabriel Berberian
 */
public interface ComposerAttachmentOrStreamDefinition extends ComposerStreamsDefinition {

  /**
   * Defines the attachments to be linked to a message
   *
   * @param attachments
   * @return
   */
  ComposerStreamsDefinition withAttachments(MessageAttachmentFile... attachments);

  /**
   * Defines the attachments to be linked to a message
   *
   * @param attachments
   * @return
   */
  ComposerStreamsDefinition withAttachments(Collection<MessageAttachmentFile> attachments);

}
