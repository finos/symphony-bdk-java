package com.symphony.bdk.spring.config;

import com.symphony.bdk.core.auth.ExtensionAppAuthenticator;
import com.symphony.bdk.core.auth.AuthenticatorFactory;
import com.symphony.bdk.core.auth.OboAuthenticator;
import com.symphony.bdk.core.auth.exception.AuthInitializationException;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.retry.RetryWithRecoveryBuilder;
import com.symphony.bdk.core.service.connection.ConnectionService;
import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.core.service.presence.PresenceService;
import com.symphony.bdk.core.service.session.SessionService;
import com.symphony.bdk.core.service.signal.SignalService;
import com.symphony.bdk.core.service.stream.StreamService;
import com.symphony.bdk.core.service.user.UserService;
import com.symphony.bdk.gen.api.AttachmentsApi;
import com.symphony.bdk.gen.api.AuditTrailApi;
import com.symphony.bdk.gen.api.ConnectionApi;
import com.symphony.bdk.gen.api.DefaultApi;
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
import com.symphony.bdk.gen.api.UserApi;
import com.symphony.bdk.gen.api.UsersApi;
import com.symphony.bdk.template.api.TemplateEngine;

import org.apiguardian.api.API;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * Injection of OBO Core services within the Spring application context.
 */
@API(status = API.Status.EXPERIMENTAL)
@ConditionalOnProperty("bdk.app.appId")
public class BdkOboServiceConfig {

  @Bean
  @ConditionalOnMissingBean
  public ExtensionAppAuthenticator extensionAppAuthenticator(AuthenticatorFactory authenticatorFactory) {
    try {
      return authenticatorFactory.getExtensionAppAuthenticator();
    } catch (AuthInitializationException e) {
      throw new BeanInitializationException("Unable to authenticate app", e);
    }
  }

  @Bean
  @ConditionalOnMissingBean
  public OboAuthenticator oboAuthenticator(AuthenticatorFactory authenticatorFactory) {
    try {
      return authenticatorFactory.getOboAuthenticator();
    } catch (AuthInitializationException e) {
      throw new BeanInitializationException("Unable to use OBO authentication", e);
    }
  }

  @Bean
  @ConditionalOnMissingBean
  public SessionService oboSessionService(SessionApi sessionApi, BdkConfig config) {
    return new SessionService(sessionApi, new RetryWithRecoveryBuilder<>().retryConfig(config.getRetry()));
  }

  @Bean
  @ConditionalOnMissingBean
  public StreamService oboStreamService(StreamsApi streamsApi, RoomMembershipApi roomMembershipApi, ShareApi shareApi,
      BdkConfig config) {
    return new StreamService(streamsApi, roomMembershipApi, shareApi, new RetryWithRecoveryBuilder<>().retryConfig(config.getRetry()));
  }

  @Bean
  @ConditionalOnMissingBean
  public UserService oboUserService(UserApi userApi, UsersApi usersApi, AuditTrailApi auditTrailApi, BdkConfig config) {
    return new UserService(userApi, usersApi, auditTrailApi, new RetryWithRecoveryBuilder<>().retryConfig(config.getRetry()));
  }

  @Bean
  @ConditionalOnMissingBean
  public PresenceService oboPresenceService(PresenceApi presenceApi, BdkConfig config) {
    return new PresenceService(presenceApi, new RetryWithRecoveryBuilder<>().retryConfig(config.getRetry()));
  }

  @Bean
  @ConditionalOnMissingBean
  public ConnectionService oboConnectionService(ConnectionApi connectionApi, BdkConfig config) {
    return new ConnectionService(connectionApi, new RetryWithRecoveryBuilder<>().retryConfig(config.getRetry()));
  }

  @Bean
  @ConditionalOnMissingBean
  public SignalService oboSignalService(SignalsApi signalsApi, BdkConfig config) {
    return new SignalService(signalsApi, new RetryWithRecoveryBuilder<>().retryConfig(config.getRetry()));
  }

  @Bean
  @ConditionalOnMissingBean
  public MessageService oboMessageService(
      final MessagesApi messagesApi,
      final MessageApi messageApi,
      final MessageSuppressionApi messageSuppressionApi,
      final StreamsApi streamsApi,
      final PodApi podApi,
      final AttachmentsApi attachmentsApi,
      final DefaultApi defaultApi,
      final TemplateEngine templateEngine,
      final BdkConfig config
  ) {
    return new MessageService(messagesApi, messageApi, messageSuppressionApi, streamsApi, podApi, attachmentsApi,
        defaultApi, templateEngine, new RetryWithRecoveryBuilder<>().retryConfig(config.getRetry()));
  }
}
