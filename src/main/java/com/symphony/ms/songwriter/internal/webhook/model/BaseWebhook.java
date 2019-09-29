package com.symphony.ms.songwriter.internal.webhook.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@NoArgsConstructor
public class BaseWebhook {
  @Id
  private String id;

  @Indexed(unique = true)
  private String hash;

  private String streamId;

  private boolean isRoom;

  private String userId;

}
