package com.symphony.bdk.spring.config;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.service.MessageService;
import com.symphony.bdk.gen.api.MessagesApi;

import org.apiguardian.api.API;
import org.springframework.context.annotation.Bean;

/**
 * [EXPERIMENTAL] Injection of Core services within the Spring application context.
 *
 * <p>
 *   Note: Service layer will properly implemented later on.
 * </p>
 */
@API(status = API.Status.EXPERIMENTAL)
public class BdkServiceConfig {

  @Bean
  public MessageService messageService(MessagesApi messagesApi, AuthSession botSession) {
    return new MessageService(messagesApi, botSession);
  }
}
