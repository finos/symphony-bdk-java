package com.symphony.bdk.bot.sdk.command.config;

import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.AnnotationMetadata;

import com.symphony.bdk.bot.sdk.command.AuthenticatedCommandHandler;
import com.symphony.bdk.bot.sdk.command.CommandHandler;
import com.symphony.bdk.bot.sdk.scan.BaseBeanFactoryPostProcessor;

/**
 * Automatically scans for {@link CommandHandler}, instantiates them, injects
 * all dependencies and registers to Spring bean registry.
 *
 * @author Marcus Secato
 *
 */
@Configuration
public class CommandBeanFactoryProcessor extends BaseBeanFactoryPostProcessor {
  private static final Logger LOGGER = LoggerFactory.getLogger(CommandBeanFactoryProcessor.class);

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
    final BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) beanFactory;
    Set<BeanDefinition> beanDefinitionSet = scanComponents(CommandHandler.class);
    LOGGER.info("Scanning for command handlers found {} beans", beanDefinitionSet.size());

    for (BeanDefinition beanDefinition : beanDefinitionSet) {
      BeanDefinitionBuilder builder = BeanDefinitionBuilder
          .genericBeanDefinition(beanDefinition.getBeanClassName())
          .setInitMethodName("register")
          .addPropertyReference("commandDispatcher", "commandDispatcherImpl")
          .addPropertyReference("commandFilter", "commandFilterImpl")
          .addPropertyReference("messageClient", "messageClientImpl")
          .addPropertyReference("featureManager", "featureManager")
          .addPropertyReference("usersClient", "usersClientImpl");

      // Inject AuthenticationProvider to AuthenticatedCommandHandler
      AnnotationMetadata beanMetadata = getBeanMetadata(beanDefinition);
      if (beanMetadata != null && isAuthenticatedCommandHandler(beanMetadata)) {
        if (isAuthProviderAnnotated(beanMetadata)) {
          Map<String, Object> annotationParams =
              getAnnotationParams(beanMetadata);
          builder.addPropertyReference("authenticationProvider",
              (String)annotationParams.get("name"));
        } else {
          builder.addPropertyReference("authenticationProvider",
              AuthProviderBeanFactoryProcessor.AUTH_PROVIDER_REGISTRY_KEY);
        }
      }

      beanDefinitionRegistry.registerBeanDefinition(
          beanDefinition.getBeanClassName(), builder.getBeanDefinition());
    }
  }

  private AnnotationMetadata getBeanMetadata(BeanDefinition beanDefinition) {
    AnnotationMetadata metadata = null;
    if (beanDefinition instanceof AnnotatedBeanDefinition) {
      AnnotatedBeanDefinition abd = (AnnotatedBeanDefinition) beanDefinition;
      metadata = abd.getMetadata();
    }

    return metadata;
  }

  private boolean isAuthenticatedCommandHandler(
      AnnotationMetadata beanMetadata) {
    return beanMetadata.getSuperClassName().equals(
        AuthenticatedCommandHandler.class.getCanonicalName());
  }

  private boolean isAuthProviderAnnotated(AnnotationMetadata beanMetadata) {
    return beanMetadata.isAnnotated(CommandAuthenticationProvider.class.getName());
  }

  private Map<String, Object> getAnnotationParams(
      AnnotationMetadata beanMetadata) {
    return beanMetadata.getAnnotationAttributes(
        CommandAuthenticationProvider.class.getName());
  }

}
