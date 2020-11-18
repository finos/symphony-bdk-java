package com.symphony.bdk.spring.annotation;

import static com.symphony.bdk.core.activity.command.SlashCommand.slash;

import com.symphony.bdk.core.activity.ActivityRegistry;
import com.symphony.bdk.core.activity.command.CommandContext;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.autoproxy.AutoProxyUtils;
import org.springframework.aop.scope.ScopedObject;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 *
 * @see <a href="https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-extension-bpp">BeanPostProcessor</a>
 * @see <a href="https://github.com/spring-projects/spring-framework/blob/master/spring-context/src/main/java/org/springframework/context/event/EventListenerMethodProcessor.java">EventListenerMethodProcessor.java</a>
 */
@Slf4j
public class SlashAnnotationProcessor implements BeanPostProcessor, ApplicationContextAware {

  /** The {@link ActivityRegistry} is used here to register the slash activities */
  private final ActivityRegistry registry;

  private ConfigurableApplicationContext applicationContext;

  public SlashAnnotationProcessor(ActivityRegistry registry) {
    this.registry = registry;
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = (ConfigurableApplicationContext) applicationContext;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

    if (!ScopedProxyUtils.isScopedTarget(beanName)) {
      Class<?> type = null;
      try {
        type = AutoProxyUtils.determineTargetClass(this.applicationContext.getBeanFactory(), beanName);
      }
      catch (Throwable ex) {
        // An unresolvable bean type, probably from a lazy bean - let's ignore it.
        log.debug("Could not resolve target class for bean with name '" + beanName + "'", ex);
      }
      if (type != null) {
        if (ScopedObject.class.isAssignableFrom(type)) {
          try {
            Class<?> targetClass = AutoProxyUtils.determineTargetClass(this.applicationContext.getBeanFactory(), ScopedProxyUtils.getTargetBeanName(beanName));
            if (targetClass != null) {
              type = targetClass;
            }
          }
          catch (Throwable ex) {
            // An invalid scoped proxy arrangement - let's ignore it.
            log.debug("Could not resolve target bean for scoped proxy '" + beanName + "'", ex);
          }
        }
        try {
          this.processBean(bean, beanName, type);
        }
        catch (Throwable ex) {
          throw new BeanInitializationException(
              "Failed to process @Slash annotation on bean with name '" + beanName + "'", ex);
        }
      }
    }

    return bean;
  }

  private void processBean(final Object bean, final String beanName, final Class<?> targetType) {
    if (AnnotationUtils.isCandidateClass(targetType, Slash.class)
        && !isSpringContainerClass(targetType)
        && !isBdkCoreClass(targetType)) {

      Map<Method, Slash> annotatedMethods = null;

      try {
        annotatedMethods = MethodIntrospector.selectMethods(targetType,
            (MethodIntrospector.MetadataLookup<Slash>) method ->
                AnnotatedElementUtils.findMergedAnnotation(method, Slash.class));
      }
      catch (Throwable ex) {
        // An unresolvable type in a method signature, probably from a lazy bean - let's ignore it.
        log.debug("Could not resolve methods for bean with name '" + beanName + "'", ex);
      }

      if (!CollectionUtils.isEmpty(annotatedMethods)) {
        for (final Method m : annotatedMethods.keySet()) {

          final Slash annotation = AnnotationUtils.getAnnotation(m, Slash.class);

          if (annotation != null) {
            if (isMethodPrototypeValid(m)) {
              this.registerSlashMethod(bean, m, annotation);
            } else {
              log.warn("Method '{}' is annotated by @Slash but does not respect the expected prototype. "
                  + "It must accept a single argument of type '{}'", m, CommandContext.class);
            }
          }
        }
      }
    }
  }

  private void registerSlashMethod(Object bean, Method method, Slash annotation) {
    this.registry.register(slash(annotation.value(), annotation.mentionBot(), c -> {
      try {
        method.invoke(bean, c);
      } catch (IllegalAccessException | InvocationTargetException e) {
        log.error("Unable to invoke @Slash method {} from bean {}", method.getName(), bean.getClass(), e);
      }
    }));
  }

  private static boolean isMethodPrototypeValid(Method m) {
    return m.getParameterCount() == 1 && m.getParameters()[0].getType().equals(CommandContext.class);
  }

  /**
   * Determine whether the given class is an {@code org.springframework}
   * bean class that is not annotated as a user or test {@link Component}...
   * which indicates that there is no {@link Slash} to be found there.
   */
  private static boolean isSpringContainerClass(Class<?> clazz) {
    return clazz.getName().startsWith("org.springframework.")
        && !AnnotatedElementUtils.isAnnotated(ClassUtils.getUserClass(clazz), Component.class);
  }

  /**
   * Ignore BDK core and gen classes, that don't need to be scanned.
   */
  private static boolean isBdkCoreClass(Class<?> clazz) {
    return (clazz.getName().startsWith("com.symphony.bdk.gen.") || clazz.getName().startsWith("com.symphony.bdk.core."))
        && !AnnotatedElementUtils.isAnnotated(ClassUtils.getUserClass(clazz), Component.class);
  }
}
