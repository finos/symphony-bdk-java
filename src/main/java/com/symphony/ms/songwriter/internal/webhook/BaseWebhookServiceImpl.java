package com.symphony.ms.songwriter.internal.webhook;

import com.symphony.ms.songwriter.internal.webhook.model.BaseWebhook;

public class BaseWebhookServiceImpl<T extends BaseWebhook> implements BaseWebhookService {

  private BaseWebhookRepository<T> webhookRepository;

  @Override
  public String getStreamIdFromIdentifier(String identifier) {
    //Optional<T> webhook = webhookRepository.findByHash(hash);
    //return webhook.isPresent() ? webhook.get().getStreamId() : null;
    return identifier;
  }

}
