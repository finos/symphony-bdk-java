package com.symphony.ms.songwriter.internal.monitoring.config;

import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
import com.symphony.ms.songwriter.internal.scan.BaseBeanFactoryPostProcessor;

/**
 * Automatically scans for {@link HealthIndicator}, instantiates them and
 * registers to Spring bean registry.
 *
 * @author Marcus Secato
 *
 */
@Component
public class MonitoringBeanFactoryProcessor extends BaseBeanFactoryPostProcessor {
  private static final Logger LOGGER = LoggerFactory.getLogger(MonitoringBeanFactoryProcessor.class);

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
    final BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) beanFactory;
    Set<BeanDefinition> beanDefinitionSet = scanComponents(HealthIndicator.class);
    LOGGER.info("Scanning for health indicators found {} beans", beanDefinitionSet.size());

    for (BeanDefinition beanDefinition : beanDefinitionSet) {
      BeanDefinition healthIndicator = BeanDefinitionBuilder
          .rootBeanDefinition(beanDefinition.getBeanClassName())
          .getBeanDefinition();

      beanDefinitionRegistry.registerBeanDefinition(getBeanSimpleClassName(
          beanDefinition.getBeanClassName()), healthIndicator);
    }
  }

  private String getBeanSimpleClassName(String beanClassName) {
    int splitIndex = beanClassName.lastIndexOf('.') + 1;
    String simpleName = beanClassName.substring(splitIndex);

    return simpleName.substring(0, 1)
        .toLowerCase()
        .concat(simpleName.substring(1));
  }
}
