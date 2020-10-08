package com.symphony.bdk.core.service.message.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apiguardian.api.API;

import java.io.InputStream;

@Getter
@Setter
@Accessors(fluent = true)
@API(status = API.Status.EXPERIMENTAL)
public class Attachment {

  private InputStream inputStream;
  private AttachmentType attachmentType;
}
