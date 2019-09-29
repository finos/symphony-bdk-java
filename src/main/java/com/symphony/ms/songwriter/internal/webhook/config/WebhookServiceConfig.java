package com.symphony.ms.songwriter.internal.webhook.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.symphony.ms.songwriter.internal.webhook.BaseWebhookService;
import com.symphony.ms.songwriter.internal.webhook.BaseWebhookServiceImpl;
import com.symphony.ms.songwriter.internal.webhook.model.BaseWebhook;

@Configuration
public class WebhookServiceConfig {

  @Bean(name="baseWebhookServiceImpl")
  @ConditionalOnMissingBean
  public BaseWebhookService webhookService() {
    return new BaseWebhookServiceImpl<BaseWebhook>();
  }
}
