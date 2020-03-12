package com.symphony.bdk.bot.sdk.scan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

/**
 * Spring-based component scanning utility class
 *
 * @author Marcus Secato
 *
 */
public abstract class BaseBeanFactoryPostProcessor implements BeanFactoryPostProcessor, ApplicationContextAware {
  private static final String BASE_PACKAGE_SCAN = "com.symphony.bdk.bot.sdk";

  private List<String> basePackages;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    basePackages = new ArrayList<String>();
    basePackages.add(BASE_PACKAGE_SCAN);

    Map<String, Object> beans = applicationContext.getBeansWithAnnotation(SpringBootApplication.class);
    if (beans.keySet().size() == 1) {
      beans.values().forEach(bean ->
          basePackages.add(bean.getClass().getPackage().getName()));
    }
  }

  protected <T> Set<BeanDefinition> scanComponents(Class<?>... clazzes) {
    ClassPathScanningCandidateComponentProvider provider =
        new ClassPathScanningCandidateComponentProvider(false);

    for (Class<?> clazz : clazzes) {
      provider.addIncludeFilter(new AssignableTypeFilter(clazz));
    }

    Set<BeanDefinition> beanDefinitions = basePackages
        .stream()
        .map(pkg -> provider.findCandidateComponents(pkg))
        .flatMap(Collection::stream)
        .collect(Collectors.toSet());

    return beanDefinitions;
  }

  @Override
  public abstract void postProcessBeanFactory(
      ConfigurableListableBeanFactory beanFactory);
}
