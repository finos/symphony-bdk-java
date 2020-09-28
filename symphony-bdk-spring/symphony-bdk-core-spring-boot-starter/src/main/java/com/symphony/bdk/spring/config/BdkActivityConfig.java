package com.symphony.bdk.spring.config;

import static com.symphony.bdk.core.activity.command.SlashCommand.slash;

import com.symphony.bdk.core.activity.AbstractActivity;
import com.symphony.bdk.core.activity.ActivityRegistry;
import com.symphony.bdk.core.activity.command.CommandContext;
import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.service.SessionService;
import com.symphony.bdk.core.service.datafeed.DatafeedService;
import com.symphony.bdk.gen.api.model.UserV2;
import com.symphony.bdk.spring.annotation.Slash;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

  @Slf4j
  @RequiredArgsConstructor
  private static class SlashAnnotationProcessor implements ApplicationContextAware {

    private final ActivityRegistry registry;

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {

      for (final String beanName : applicationContext.getBeanDefinitionNames()) {
        final Object bean = applicationContext.getBean(beanName);

        for (final Method m : getClass(bean).getDeclaredMethods()) {

          final Slash annotation = AnnotationUtils.getAnnotation(m, Slash.class);

          if (annotation != null) {
            if (isMethodPrototypeValid(m)) {
              this.registry.register(slash(annotation.value(), annotation.mentionBot(), c -> {
                try {
                  m.invoke(bean, c);
                } catch (IllegalAccessException | InvocationTargetException e) {
                  log.error("Unable to invoke @Slash method {} from bean {}", m.getName(), bean.getClass(), e);
                }
              }));
            } else {
              log.warn("Method '{}' is annotated by @Slash but does not respect the expected prototype. "
                  + "It must accept a single argument of type '{}'", m, CommandContext.class);
            }
          }
        }
      }
    }

    private static boolean isMethodPrototypeValid(Method m) {
      return m.getParameterCount() == 1 && m.getParameters()[0].getType().equals(CommandContext.class);
    }

    private static Class<?> getClass(Object bean) {
      return AopUtils.isAopProxy(bean) ? AopUtils.getTargetClass(bean) : bean.getClass();
    }
  }
}
