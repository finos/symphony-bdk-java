package com.symphony.bdk.test;

import static org.mockito.Mockito.when;

import com.symphony.bdk.core.activity.AbstractActivity;
import com.symphony.bdk.core.activity.ActivityRegistry;
import com.symphony.bdk.core.auth.AuthSession;
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
import com.symphony.bdk.gen.api.model.UserV2;
import com.symphony.bdk.spring.SymphonyBdkAutoConfiguration;
import com.symphony.bdk.spring.annotation.SlashAnnotationProcessor;
import com.symphony.bdk.template.api.TemplateEngine;
import com.symphony.bdk.template.freemarker.FreeMarkerEngine;

import lombok.Generated;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

import java.util.List;

@TestConfiguration
@EnableAutoConfiguration(exclude = {SymphonyBdkAutoConfiguration.class})
@EnableConfigurationProperties(BdkSpringTestProperties.class)
@Profile("integration-test")
@Generated
public class SymphonyBdkSpringTestConfig {

  private final SymphonyBdkTestMock bdkTestMock = new SymphonyBdkTestMock();

  @Bean
  public DatafeedLoop datafeedLoop() {
    return bdkTestMock.getDatafeedLoop();
  }

  @Bean
  public UserV2 botInfo(BdkSpringTestProperties properties) {
    return new UserV2().id(properties.getId()).username(properties.getUsername()).displayName(
        properties.getDisplayName());
  }

  @Bean
  public AuthSession botSession() {
    AuthSession authSession = bdkTestMock.getBotSession();
    when(authSession.getSessionToken()).thenReturn("sessionToken");
    when(authSession.getKeyManagerToken()).thenReturn("kmToken");
    return authSession;
  }

  @Bean
  public MessageService messageService(TemplateEngine templateEngine) {
    MessageService messageService = bdkTestMock.getMessageService();
    when(messageService.templates()).thenReturn(templateEngine);
    return messageService;
  }

  @Bean
  @ConditionalOnMissingBean
  public TemplateEngine templateEngine() {
    return new FreeMarkerEngine();
  }

  @Bean
  public SessionService sessionService(UserV2 botInfo) {
    SessionService sessionService = bdkTestMock.getSessionService();
    when(sessionService.getSession()).thenReturn(botInfo);
    return sessionService;
  }

  @Bean
  public StreamService streamService() {
    return bdkTestMock.getStreamService();
  }

  @Bean
  public UserService userService() {
    return bdkTestMock.getUserService();
  }

  @Bean
  public DisclaimerService disclaimerService() {
    return bdkTestMock.getDisclaimerService();
  }

  @Bean
  public PresenceService presenceService() {
    return bdkTestMock.getPresenceService();
  }

  @Bean
  public ConnectionService connectionService() {
    return bdkTestMock.getConnectionService();
  }

  @Bean
  public SignalService signalService() {
    return bdkTestMock.getSignalService();
  }

  @Bean
  public ApplicationService applicationService() {
    return bdkTestMock.getApplicationService();
  }

  @Bean
  public HealthService healthService() {
    return bdkTestMock.getHealthService();
  }

  @Bean
  public ActivityRegistry activityRegistry(SessionService sessionService, DatafeedLoop datafeedLoop,
      List<AbstractActivity<?, ?>> activities) {
    final UserV2 botSessionInfo = sessionService.getSession();
    final ActivityRegistry activityRegistry = new ActivityRegistry(botSessionInfo, datafeedLoop);
    activities.forEach(activityRegistry::register);
    return activityRegistry;
  }

  @Bean
  public SlashAnnotationProcessor slashAnnotationProcessor() {
    return new SlashAnnotationProcessor();
  }

}
