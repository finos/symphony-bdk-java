package com.symphony.bdk.spring.config;

import com.symphony.bdk.core.auth.BotAuthSession;
import com.symphony.bdk.core.auth.CustomEnhancedAuthSession;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.service.application.ApplicationService;
import com.symphony.bdk.core.service.connection.ConnectionService;
import com.symphony.bdk.core.service.disclaimer.DisclaimerService;
import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.core.service.presence.PresenceService;
import com.symphony.bdk.core.service.session.SessionService;
import com.symphony.bdk.core.service.stream.StreamService;
import com.symphony.bdk.core.service.user.UserService;
import com.symphony.bdk.gen.api.AppEntitlementApi;
import com.symphony.bdk.gen.api.ApplicationApi;
import com.symphony.bdk.gen.api.AttachmentsApi;
import com.symphony.bdk.gen.api.AuditTrailApi;
import com.symphony.bdk.gen.api.ConnectionApi;
import com.symphony.bdk.gen.api.DefaultApi;
import com.symphony.bdk.gen.api.DisclaimerApi;
import com.symphony.bdk.gen.api.MessageApi;
import com.symphony.bdk.gen.api.MessageSuppressionApi;
import com.symphony.bdk.gen.api.MessagesApi;
import com.symphony.bdk.gen.api.PodApi;
import com.symphony.bdk.gen.api.PresenceApi;
import com.symphony.bdk.gen.api.RoomMembershipApi;
import com.symphony.bdk.gen.api.SessionApi;
import com.symphony.bdk.gen.api.ShareApi;
import com.symphony.bdk.gen.api.StreamsApi;
import com.symphony.bdk.gen.api.UserApi;
import com.symphony.bdk.gen.api.UsersApi;
import com.symphony.bdk.template.api.TemplateEngine;

import org.apiguardian.api.API;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;


@API(status = API.Status.EXPERIMENTAL)
@ConditionalOnBean(name = "enhancedAuthSession")
public class BdkAuthEnhancedServiceConfig {

  @Bean
  @ConditionalOnMissingBean
  public SessionService sessionService(SessionApi sessionApi, BotAuthSession botSession,
      CustomEnhancedAuthSession enhancedAuthSession, BdkConfig config) {

    return new SessionService(sessionApi, botSession, new RetryWithRecoveryBuilder<>().retryConfig(config.getRetry())
        .recoveryStrategy((e) -> e.isUnauthorized() && enhancedAuthSession.isSessionExpired(e),
            enhancedAuthSession::refresh));
  }

  @Bean
  @ConditionalOnMissingBean
  public StreamService streamService(StreamsApi streamsApi, RoomMembershipApi roomMembershipApi, ShareApi shareApi,
      BotAuthSession botSession, CustomEnhancedAuthSession enhancedAuthSession, BdkConfig config) {

    return new StreamService(streamsApi, roomMembershipApi, shareApi, botSession,
        new RetryWithRecoveryBuilder<>().retryConfig(config.getRetry())
            .recoveryStrategy((e) -> e.isUnauthorized() && enhancedAuthSession.isSessionExpired(e),
                enhancedAuthSession::refresh));
  }

  @Bean
  @ConditionalOnMissingBean
  public UserService userService(UserApi userApi, UsersApi usersApi, AuditTrailApi auditTrailApi,
      BotAuthSession botSession, CustomEnhancedAuthSession enhancedAuthSession, BdkConfig config) {

    return new UserService(userApi, usersApi, auditTrailApi, botSession,
        new RetryWithRecoveryBuilder<>().retryConfig(config.getRetry())
            .recoveryStrategy((e) -> e.isUnauthorized() && enhancedAuthSession.isSessionExpired(e),
                enhancedAuthSession::refresh));
  }

  @Bean
  @ConditionalOnMissingBean
  public DisclaimerService disclaimerService(DisclaimerApi disclaimerApi, BotAuthSession botSession,
      CustomEnhancedAuthSession enhancedAuthSession, BdkConfig config) {
    return new DisclaimerService(disclaimerApi, botSession,
        new RetryWithRecoveryBuilder<>().retryConfig(config.getRetry()));
  }

  @Bean
  @ConditionalOnMissingBean
  public PresenceService presenceService(PresenceApi presenceApi, BotAuthSession botSession,
      CustomEnhancedAuthSession enhancedAuthSession, BdkConfig config) {

    return new PresenceService(presenceApi, botSession, new RetryWithRecoveryBuilder<>().retryConfig(config.getRetry())
        .recoveryStrategy((e) -> e.isUnauthorized() && enhancedAuthSession.isSessionExpired(e),
            enhancedAuthSession::refresh));
  }

  @Bean
  @ConditionalOnMissingBean
  public ConnectionService connectionService(ConnectionApi connectionApi, BotAuthSession botSession,
      CustomEnhancedAuthSession enhancedAuthSession, BdkConfig config) {

    return new ConnectionService(connectionApi, botSession,
        new RetryWithRecoveryBuilder<>().retryConfig(config.getRetry())
            .recoveryStrategy((e) -> e.isUnauthorized() && enhancedAuthSession.isSessionExpired(e),
                enhancedAuthSession::refresh));
  }

  @Bean
  @ConditionalOnMissingBean
  public ApplicationService applicationService(ApplicationApi applicationApi, AppEntitlementApi appEntitlementApi,
      BotAuthSession botSession, CustomEnhancedAuthSession enhancedAuthSession, BdkConfig config) {

    return new ApplicationService(applicationApi, appEntitlementApi, botSession,
        new RetryWithRecoveryBuilder<>().retryConfig(config.getRetry())
            .recoveryStrategy((e) -> e.isUnauthorized() && enhancedAuthSession.isSessionExpired(e),
                enhancedAuthSession::refresh));
  }

  @Bean
  @ConditionalOnMissingBean
  public MessageService messageService(final MessagesApi messagesApi, final MessageApi messageApi,
      final MessageSuppressionApi messageSuppressionApi, final StreamsApi streamsApi, final PodApi podApi,
      final AttachmentsApi attachmentsApi, final DefaultApi defaultApi, final BotAuthSession botSession,
      final TemplateEngine templateEngine, final CustomEnhancedAuthSession enhancedAuthSession, final BdkConfig config) {

    return new MessageService(messagesApi, messageApi, messageSuppressionApi, streamsApi, podApi, attachmentsApi,
        defaultApi, botSession, templateEngine, new RetryWithRecoveryBuilder<>().retryConfig(config.getRetry())
        .recoveryStrategy((e) -> e.isUnauthorized() && enhancedAuthSession.isSessionExpired(e),
            enhancedAuthSession::refresh));
  }

}
