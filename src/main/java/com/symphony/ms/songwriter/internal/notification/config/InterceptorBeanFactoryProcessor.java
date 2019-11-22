package com.symphony.ms.songwriter.internal.notification.config;

import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.stereotype.Component;
import com.symphony.ms.songwriter.internal.notification.NotificationInterceptor;
import com.symphony.ms.songwriter.internal.scan.BaseBeanFactoryPostProcessor;

/**
 * Automatically scans for {@link NotificationInterceptor}, instantiates them,
 * injects all dependencies and registers to Spring bean registry.
 *
 * @author Marcus Secato
 *
 */
@Component
public class InterceptorBeanFactoryProcessor extends BaseBeanFactoryPostProcessor {
  private static final Logger LOGGER = LoggerFactory.getLogger(InterceptorBeanFactoryProcessor.class);

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
    final BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) beanFactory;
    Set<BeanDefinition> beanDefinitionSet = scanComponents(NotificationInterceptor.class);
    LOGGER.info("Scanning for notification interceptors found {} beans", beanDefinitionSet.size());

    for (BeanDefinition beanDefinition : beanDefinitionSet) {
      BeanDefinition notificationInterceptor = BeanDefinitionBuilder
          .rootBeanDefinition(beanDefinition.getBeanClassName())
          .setInitMethodName("register")
          .addPropertyReference("interceptorChain", "interceptorChainImpl")
          .getBeanDefinition();

      beanDefinitionRegistry.registerBeanDefinition(
          beanDefinition.getBeanClassName(), notificationInterceptor);
    }
  }

}
