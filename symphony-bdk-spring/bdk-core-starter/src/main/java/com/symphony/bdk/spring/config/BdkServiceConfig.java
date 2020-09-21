package com.symphony.bdk.spring.config;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.service.MessageService;
import com.symphony.bdk.core.service.SessionService;
import com.symphony.bdk.core.service.stream.StreamService;
import com.symphony.bdk.core.service.user.UserService;
import com.symphony.bdk.gen.api.AttachmentsApi;
import com.symphony.bdk.gen.api.DefaultApi;
import com.symphony.bdk.gen.api.MessageApi;
import com.symphony.bdk.gen.api.MessageSuppressionApi;
import com.symphony.bdk.gen.api.MessagesApi;
import com.symphony.bdk.gen.api.PodApi;
import com.symphony.bdk.gen.api.SessionApi;
import com.symphony.bdk.gen.api.StreamsApi;
import com.symphony.bdk.gen.api.UserApi;
import com.symphony.bdk.gen.api.UsersApi;
import com.symphony.bdk.template.api.TemplateEngine;
import com.symphony.bdk.template.freemarker.FreeMarkerEngine;

import org.apiguardian.api.API;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * Injection of Core services within the Spring application context.
 */
@API(status = API.Status.EXPERIMENTAL)
public class BdkServiceConfig {

  @Bean
  @ConditionalOnMissingBean
  public SessionService sessionService(SessionApi sessionApi) {
    return new SessionService(sessionApi);
  }

  @Bean
  @ConditionalOnMissingBean
  public StreamService streamService(StreamsApi streamsApi, AuthSession botSession) {
    return new StreamService(streamsApi, botSession);
  }

  @Bean
  @ConditionalOnMissingBean
  public UserService userService(UserApi userApi, UsersApi usersApi, AuthSession botSession) {
    return new UserService(userApi, usersApi, botSession);
  }

  @Bean
  @ConditionalOnMissingBean
  public TemplateEngine templateEngine() {
    return new FreeMarkerEngine();
  }

  @Bean
  @ConditionalOnMissingBean
  public MessageService messageService(
      final MessagesApi messagesApi,
      final MessageApi messageApi,
      final MessageSuppressionApi messageSuppressionApi,
      final StreamsApi streamsApi,
      final PodApi podApi,
      final AttachmentsApi attachmentsApi,
      final DefaultApi defaultApi,
      final AuthSession botSession,
      final TemplateEngine templateEngine
  ) {
    return new MessageService(messagesApi, messageApi, messageSuppressionApi, streamsApi, podApi, attachmentsApi,
        defaultApi, botSession, templateEngine);
  }
}
