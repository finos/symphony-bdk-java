package com.symphony.bdk.bot.sdk.event.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import model.ImageInfo;

/**
 * Symphony attachment information
 *
 * @author Gabriel Berberian
 */
@Data
@NoArgsConstructor
public class AttachmentImageInfo {

  private String id;
  private String dimension;

  public AttachmentImageInfo(ImageInfo imageInfo) {
    this.id = imageInfo.getId();
    this.dimension = imageInfo.getDimension();
  }
}
