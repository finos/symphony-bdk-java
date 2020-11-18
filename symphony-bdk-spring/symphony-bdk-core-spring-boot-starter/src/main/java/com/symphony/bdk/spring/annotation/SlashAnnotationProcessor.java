package com.symphony.bdk.spring.annotation;

import static org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation;
import static org.springframework.core.annotation.AnnotationUtils.isCandidateClass;

import com.symphony.bdk.core.activity.ActivityRegistry;
import com.symphony.bdk.core.activity.command.CommandContext;
import com.symphony.bdk.core.activity.command.SlashCommand;

import lombok.Generated;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.autoproxy.AutoProxyUtils;
import org.springframework.aop.scope.ScopedObject;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.MethodIntrospector;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Registers {@link Slash} methods as individual {@link com.symphony.bdk.core.activity.command.SlashCommand} instances
 * within the {@link ActivityRegistry}.
 *
 * @see <a href="https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-extension-bpp">BeanPostProcessor</a>
 * @see <a href="https://github.com/spring-projects/spring-framework/blob/master/spring-context/src/main/java/org/springframework/context/event/EventListenerMethodProcessor.java">EventListenerMethodProcessor.java</a>
 */
@Slf4j
@RequiredArgsConstructor
public class SlashAnnotationProcessor implements BeanPostProcessor, ApplicationContextAware {

  /**
   * The list of packages to be ignored from application context scanning
   */
  private static final String[] IGNORED_PACKAGES = {
      "org.springframework.",
      "com.symphony.bdk.gen.",
      "com.symphony.bdk.core."
  };

  /**
   * The {@link ActivityRegistry} is used here to register the slash activities
   */
  private final ActivityRegistry registry;

  private ConfigurableApplicationContext applicationContext;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = (ConfigurableApplicationContext) applicationContext;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

    if (!ScopedProxyUtils.isScopedTarget(beanName)) {

      final Class<?> type = this.determineTargetClass(beanName);

      if (type != null && isCandidateClass(type, Slash.class) && !isClassLocatedInPackages(type, IGNORED_PACKAGES)) {
        try {
          this.processBean(bean, beanName, type);
        } catch (Throwable ex) {
          // just alert the developer
          log.warn("Failed to process @Slash annotation on bean with name '{}'", beanName, ex);
        }
      }
    }

    return bean;
  }

  @Generated // means excluded from test coverage (hard to test error cases)
  private Class<?> determineTargetClass(String beanName) {
    Class<?> type = null;
    try {
      type = AutoProxyUtils.determineTargetClass(this.applicationContext.getBeanFactory(), beanName);
    } catch (Throwable ex) {
      // An unresolvable bean type, probably from a lazy bean - let's ignore it.
      log.debug("Could not resolve target class for bean with name '{}'", beanName, ex);
    }

    if (type != null && ScopedObject.class.isAssignableFrom(type)) {
      try {
        Class<?> targetClass = AutoProxyUtils.determineTargetClass(
            this.applicationContext.getBeanFactory(),
            ScopedProxyUtils.getTargetBeanName(beanName)
        );
        if (targetClass != null) {
          type = targetClass;
        }
      } catch (Throwable ex) {
        // An invalid scoped proxy arrangement - let's ignore it.
        log.debug("Could not resolve target bean for scoped proxy '{}'", beanName, ex);
      }
    }

    return type;
  }

  private void processBean(final Object bean, final String beanName, final Class<?> targetType) {

    final Map<Method, Slash> annotatedMethods = this.getSlashAnnotatedMethods(beanName, targetType);

    for (final Method m : annotatedMethods.keySet()) {

      final Slash annotation = annotatedMethods.get(m);

      if (isMethodPrototypeValid(m)) {
        this.registerSlashMethod(bean, m, annotation);
      } else {
        log.warn("Method '{}' is annotated by @Slash but does not respect the expected prototype. "
            + "It must accept a single argument of type '{}'", m, CommandContext.class);
      }
    }
  }

  @Generated // means excluded from test coverage (hard to test error cases)
  private Map<Method, Slash> getSlashAnnotatedMethods(String beanName, Class<?> targetType) {
    Map<Method, Slash> annotatedMethods = null;

    try {
      annotatedMethods = MethodIntrospector.selectMethods(
          targetType,
          (MethodIntrospector.MetadataLookup<Slash>) method -> findMergedAnnotation(method, Slash.class)
      );
    } catch (Throwable ex) {
      // An unresolvable type in a method signature, probably from a lazy bean - let's ignore it.
      log.debug("Could not resolve methods for bean with name '{}'", beanName, ex);
    }
    return annotatedMethods == null ? Collections.emptyMap() : annotatedMethods;
  }

  private void registerSlashMethod(Object bean, Method method, Slash annotation) {
    this.registry.register(
        SlashCommand.slash(annotation.value(), annotation.mentionBot(), createSlashCommandCallback(bean, method))
    );
  }

  // visible for testing
  protected static Consumer<CommandContext> createSlashCommandCallback(Object bean, Method method) {
    return c -> {
      try {
        method.invoke(bean, c);
      } catch (Throwable e) {
        log.error("Unable to invoke @Slash method {} from bean {}", method.getName(), bean.getClass(), e);
      }
    };
  }

  private static boolean isMethodPrototypeValid(Method m) {
    return m.getParameterCount() == 1 && m.getParameters()[0].getType().equals(CommandContext.class);
  }

  private static boolean isClassLocatedInPackages(Class<?> clazz, String... packagePrefixes) {
    return Stream.of(packagePrefixes).anyMatch(clazz.getName()::startsWith);
  }
}
