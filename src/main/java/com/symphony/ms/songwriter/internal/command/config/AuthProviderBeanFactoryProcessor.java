package com.symphony.ms.songwriter.internal.command.config;

import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.Configuration;
import com.symphony.ms.songwriter.internal.command.AuthenticationProvider;
import com.symphony.ms.songwriter.internal.scan.BaseBeanFactoryPostProcessor;

/**
 * Automatically scans for {@link AuthenticationProvider}, instantiates them
 * and registers to Spring bean registry.
 *
 * @author Marcus Secato
 *
 */
@Configuration
public class AuthProviderBeanFactoryProcessor extends BaseBeanFactoryPostProcessor {
  private static final Logger LOGGER = LoggerFactory.getLogger(AuthProviderBeanFactoryProcessor.class);

  public static final String AUTH_PROVIDER_REGISTRY_KEY = "AuthenticationProvider";

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
    final BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) beanFactory;
    Set<BeanDefinition> beanDefinitionSet = scanComponents(AuthenticationProvider.class);
    LOGGER.info("Scanning for authentication providers found {} beans", beanDefinitionSet.size());

    if (beanDefinitionSet.size() == 1) {
      registerProvider(beanDefinitionRegistry,
          beanDefinitionSet.iterator().next(), true);
    } else {
      for (BeanDefinition beanDefinition : beanDefinitionSet) {
        registerProvider(beanDefinitionRegistry, beanDefinition, false);
      }
    }
  }

  private void registerProvider(BeanDefinitionRegistry registry,
      BeanDefinition definition, boolean useDefaultKey) {
    String beanKey = getBeanSimpleClassName(definition.getBeanClassName());
    if (useDefaultKey) {
      LOGGER.debug("Default authentication provider: {}", beanKey);
      beanKey = AUTH_PROVIDER_REGISTRY_KEY;
    }

    BeanDefinition authProvider = BeanDefinitionBuilder
        .genericBeanDefinition(definition.getBeanClassName())
        .getBeanDefinition();

    registry.registerBeanDefinition(beanKey, authProvider);
  }

  private String getBeanSimpleClassName(String beanClassName) {
    int splitIndex = beanClassName.lastIndexOf('.') + 1;
    return beanClassName.substring(splitIndex);
  }

}
