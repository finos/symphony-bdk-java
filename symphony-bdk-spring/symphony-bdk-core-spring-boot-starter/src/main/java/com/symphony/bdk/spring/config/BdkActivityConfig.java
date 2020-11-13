package com.symphony.bdk.spring.config;

import com.symphony.bdk.core.activity.AbstractActivity;
import com.symphony.bdk.core.activity.ActivityRegistry;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.service.session.SessionService;
import com.symphony.bdk.core.service.datafeed.DatafeedService;
import com.symphony.bdk.gen.api.model.UserV2;
import com.symphony.bdk.spring.annotation.Slash;
import com.symphony.bdk.spring.annotation.SlashAnnotationProcessor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * Configuration for Activity API:
 * <ul>
 *   <li>configuration and injection of the {@link ActivityRegistry}</li>
 *   <li>registering of any bean of type {@link AbstractActivity}</li>
 *   <li>process methods annotated by {@link Slash} annotation and register them</li>
 * </ul>
 */
@Slf4j
@ConditionalOnBean(BdkDatafeedConfig.class)
public class BdkActivityConfig {

  @Bean
  public ActivityRegistry activityRegistry(
      final AuthSession botSession,
      final SessionService sessionService,
      final DatafeedService datafeedService,
      final List<AbstractActivity<?, ?>> activities
  ) {
    log.debug("Retrieving bot session info");
    final UserV2 botSessionInfo = sessionService.getSession(botSession);
    final ActivityRegistry activityRegistry = new ActivityRegistry(botSessionInfo, datafeedService::subscribe);
    log.debug("{} activities found from context", activities.size());
    activities.forEach(activityRegistry::register);
    return activityRegistry;
  }

  @Bean
  public SlashAnnotationProcessor slashAnnotationProcessor(ActivityRegistry registry) {
    return new SlashAnnotationProcessor(registry);
  }
}
