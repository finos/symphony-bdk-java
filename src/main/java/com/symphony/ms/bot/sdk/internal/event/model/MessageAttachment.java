package com.symphony.ms.bot.sdk.internal.event.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.Attachment;

/**
 * Symphony message attachment details
 *
 * @author Gabriel Berberian
 */
@Data
@NoArgsConstructor
public class MessageAttachment {

  private String id;
  private String name;
  private Long size;
  private AttachmentImageInfo image;

  public MessageAttachment(Attachment attachment) {
    this.id = attachment.getId();
    this.name = attachment.getName();
    this.size = attachment.getSize();
    this.image =
        attachment.getImage() != null ? new AttachmentImageInfo(attachment.getImage()) : null;
  }

}
