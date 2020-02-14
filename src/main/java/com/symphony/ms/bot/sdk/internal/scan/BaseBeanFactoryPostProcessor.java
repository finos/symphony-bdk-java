package com.symphony.ms.bot.sdk.internal.scan;

import java.util.Set;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

/**
 * Spring-based component scanning utility class
 *
 * @author Marcus Secato
 *
 */
public abstract class BaseBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

  private static final String BASE_PACKAGE_SCAN = "com.symphony.ms.bot.sdk";

  protected <T> Set<BeanDefinition> scanComponents(Class<?>... clazzes) {
    ClassPathScanningCandidateComponentProvider provider =
        new ClassPathScanningCandidateComponentProvider(false);

    for (Class<?> clazz : clazzes) {
      provider.addIncludeFilter(new AssignableTypeFilter(clazz));
    }

    return provider.findCandidateComponents(BASE_PACKAGE_SCAN);
  }

  @Override
  public abstract void postProcessBeanFactory(
      ConfigurableListableBeanFactory beanFactory);
}
