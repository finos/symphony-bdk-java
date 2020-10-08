package com.symphony.bdk.core.service.message.model;

import org.apiguardian.api.API;

@API(status = API.Status.EXPERIMENTAL)
public enum AttachmentType {
  BMP(".bmp"),
  DOC(".doc"),
  PNG(".png"),
  MPEG(".mpeg"),
  CDI(".cdi"),
  JPG(".jpg"),
  MPG(".mpg"),
  XLS(".xls"),
  PDF(".pdf"),
  AVI(".avi"),
  CSV(".csv"),
  PPTX(".pptx"),
  TIFF(".tiff"),
  X_TIFF(".x-tiff"),
  WBMP(".wbmp"),
  DOCX(".docx"),
  TIF(".tif"),
  PPT(".ppt"),
  GIF(".gif"),
  MOV(".mov"),
  JPEG(".jpeg"),
  XLSX(".xlsx"),
  M4V(".m4v"),
  MP4(".mp4");

  private final String type;

  AttachmentType(String type) {
    this.type = type;
  }

  public String type() {
    return type;
  }
}
