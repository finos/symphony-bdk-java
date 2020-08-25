package com.symphony.bdk.bot.sdk.event.model;

import lombok.Data;
import model.FileAttachment;

/**
 * Symphony message attachment file
 */
@Data
public class MessageAttachmentFile {

  private byte[] fileContent;
  private String fileName;
  private Long size;

  public MessageAttachmentFile(FileAttachment attachment) {
    this.fileContent = attachment.getFileContent();
    this.fileName = attachment.getFileName();
    this.size = attachment.getSize();
  }

}
