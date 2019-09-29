package com.symphony.ms.songwriter.internal.command.config;

import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.stereotype.Component;
import com.symphony.ms.songwriter.internal.command.CommandHandler;

@Component
public class CommandBeanFactoryProcessor implements BeanFactoryPostProcessor {
  private static final Logger LOGGER = LoggerFactory.getLogger(CommandBeanFactoryProcessor.class);

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
    final BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) beanFactory;
    ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
    provider.addIncludeFilter(new AssignableTypeFilter(CommandHandler.class));
    Set<BeanDefinition> beanDefinitionSet = provider.findCandidateComponents("com.symphony.ms");
    LOGGER.info("Scanning for command handlers found {} beans", beanDefinitionSet.size());

    for(BeanDefinition beanDefinition : beanDefinitionSet) {
      BeanDefinition commandHandler = BeanDefinitionBuilder
          .rootBeanDefinition(beanDefinition.getBeanClassName())
          .setInitMethodName("register")
          .addPropertyReference("commandDispatcher", "commandDispatcherImpl")
          .addPropertyReference("commandFilter", "commandFilterImpl")
          .addPropertyReference("messageService", "messageServiceImpl")
          .addPropertyReference("featureManager", "featureManager")
          .addPropertyReference("symphonyService", "symphonyServiceImpl")
          .getBeanDefinition();

      beanDefinitionRegistry.registerBeanDefinition(beanDefinition.getBeanClassName(), commandHandler);
    }
  }

}
