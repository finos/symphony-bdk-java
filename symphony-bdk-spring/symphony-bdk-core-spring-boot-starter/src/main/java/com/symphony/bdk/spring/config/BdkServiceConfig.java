package com.symphony.bdk.spring.config;

import com.symphony.bdk.core.auth.BotAuthSession;
import com.symphony.bdk.core.auth.CustomEnhancedAuthSession;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.service.application.ApplicationService;
import com.symphony.bdk.core.service.connection.ConnectionService;
import com.symphony.bdk.core.service.datafeed.DatafeedLoop;
import com.symphony.bdk.core.service.disclaimer.DisclaimerService;
import com.symphony.bdk.core.service.health.HealthService;
import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.core.service.presence.PresenceService;
import com.symphony.bdk.core.service.session.SessionService;
import com.symphony.bdk.core.service.signal.SignalService;
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
import com.symphony.bdk.gen.api.SignalsApi;
import com.symphony.bdk.gen.api.StreamsApi;
import com.symphony.bdk.gen.api.SystemApi;
import com.symphony.bdk.gen.api.UserApi;
import com.symphony.bdk.gen.api.UsersApi;
import com.symphony.bdk.spring.service.BotInfoService;
import com.symphony.bdk.template.api.TemplateEngine;

import org.apiguardian.api.API;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

/**
 * Injection of Core services within the Spring application context.
 */
@API(status = API.Status.EXPERIMENTAL)
@ConditionalOnBean(name = "botSession")
public class BdkServiceConfig {

  private final RetryWithRecoveryBuilder<?> retryBuilder;

  public BdkServiceConfig(BdkConfig config, Optional<CustomEnhancedAuthSession> enhancedAuthSession) {
    retryBuilder = new RetryWithRecoveryBuilder<>().retryConfig(config.getRetry());
    enhancedAuthSession.ifPresent(
        session -> retryBuilder.recoveryStrategy((e) -> e.isUnauthorized() && session.isSessionExpired(e),
            session::refresh));
  }

  @Bean
  @ConditionalOnMissingBean(SessionService.class)
  public SessionService sessionService(SessionApi sessionApi, BotAuthSession botSession) {
    return new SessionService(sessionApi, botSession, retryBuilder);
  }

  @Bean
  @ConditionalOnMissingBean(StreamService.class)
  public StreamService streamService(StreamsApi streamsApi, RoomMembershipApi roomMembershipApi, ShareApi shareApi,
      BotAuthSession botSession) {
    return new StreamService(streamsApi, roomMembershipApi, shareApi, botSession, retryBuilder);
  }

  @Bean
  @ConditionalOnMissingBean(UserService.class)
  public UserService userService(UserApi userApi, UsersApi usersApi, AuditTrailApi auditTrailApi,
      BotAuthSession botSession) {
    return new UserService(userApi, usersApi, auditTrailApi, botSession, retryBuilder);
  }

  @Bean
  @ConditionalOnMissingBean(DisclaimerService.class)
  public DisclaimerService disclaimerService(DisclaimerApi disclaimerApi, BotAuthSession botSession) {
    return new DisclaimerService(disclaimerApi, botSession, retryBuilder);
  }

  @Bean
  @ConditionalOnMissingBean(PresenceService.class)
  public PresenceService presenceService(PresenceApi presenceApi, BotAuthSession botSession) {
    return new PresenceService(presenceApi, botSession, retryBuilder);
  }

  @Bean
  @ConditionalOnMissingBean(ConnectionService.class)
  public ConnectionService connectionService(ConnectionApi connectionApi, BotAuthSession botSession) {
    return new ConnectionService(connectionApi, botSession, retryBuilder);
  }

  @Bean
  @ConditionalOnMissingBean
  public SignalService signalService(SignalsApi signalsApi, BotAuthSession botSession) {
    return new SignalService(signalsApi, botSession, retryBuilder);
  }

  @Bean
  @ConditionalOnMissingBean(ApplicationService.class)
  public ApplicationService applicationService(ApplicationApi applicationApi, AppEntitlementApi appEntitlementApi,
      BotAuthSession botSession) {
    return new ApplicationService(applicationApi, appEntitlementApi, botSession, retryBuilder);
  }

  @Bean
  @ConditionalOnProperty(value = "bdk.datafeed.enabled", havingValue = "true", matchIfMissing = true)
  @ConditionalOnMissingBean
  public HealthService healthService(SystemApi systemApi, SignalsApi signalsApi, DatafeedLoop datafeedLoop) {
    return new HealthService(systemApi, signalsApi, datafeedLoop);
  }

  @Bean
  @ConditionalOnMissingBean(MessageService.class)
  public MessageService messageService(final MessagesApi messagesApi, final MessageApi messageApi,
      final MessageSuppressionApi messageSuppressionApi, final StreamsApi streamsApi, final PodApi podApi,
      final AttachmentsApi attachmentsApi, final DefaultApi defaultApi, final BotAuthSession botSession,
      final TemplateEngine templateEngine) {
    return new MessageService(messagesApi, messageApi, messageSuppressionApi, streamsApi, podApi, attachmentsApi,
        defaultApi, botSession, templateEngine, retryBuilder);
  }

  @Bean
  @ConditionalOnMissingBean
  public BotInfoService botInfoService(SessionService sessionService) {
    return new BotInfoService(sessionService);
  }
}
