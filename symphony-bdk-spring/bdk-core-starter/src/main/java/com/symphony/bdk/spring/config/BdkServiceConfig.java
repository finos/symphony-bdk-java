package com.symphony.bdk.spring.config;

import com.symphony.bdk.core.api.invoker.ApiException;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.service.MessageService;
import com.symphony.bdk.gen.api.AttachmentsApi;
import com.symphony.bdk.gen.api.DefaultApi;
import com.symphony.bdk.gen.api.MessageApi;
import com.symphony.bdk.gen.api.MessageSuppressionApi;
import com.symphony.bdk.gen.api.MessagesApi;

import com.symphony.bdk.gen.api.PodApi;
import com.symphony.bdk.gen.api.StreamsApi;

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
  public MessageService messageService(MessagesApi messagesApi, MessageApi messageApi, MessageSuppressionApi messageSuppressionApi,
      StreamsApi streamsApi, PodApi podApi, AttachmentsApi attachmentsApi, DefaultApi defaultApi, AuthSession botSession,
      BdkConfig config) {
    final RetryWithRecoveryBuilder<Object> retryWithRecoveryBuilder = new RetryWithRecoveryBuilder<>()
        .retryConfig(config.getRetry())
        .recoveryStrategy(ApiException::isUnauthorized, botSession::refresh);

    return new MessageService(messagesApi, messageApi, messageSuppressionApi, streamsApi, podApi, attachmentsApi,
        defaultApi, botSession, retryWithRecoveryBuilder);
  }
}
