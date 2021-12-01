package com.symphony.bdk.spring.annotation;

import static org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation;
import static org.springframework.core.annotation.AnnotationUtils.isCandidateClass;

import com.symphony.bdk.core.activity.ActivityRegistry;
import com.symphony.bdk.core.activity.command.CommandContext;
import com.symphony.bdk.core.activity.command.SlashArgumentActivity;
import com.symphony.bdk.core.activity.command.SlashCommand;

import lombok.Generated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.autoproxy.AutoProxyUtils;
import org.springframework.aop.scope.ScopedObject;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.MethodIntrospector;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Registers {@link Slash} methods as individual {@link com.symphony.bdk.core.activity.command.SlashCommand} instances
 * within the {@link ActivityRegistry}.
 *
 * @see <a href="https://github.com/spring-projects/spring-framework/blob/master/spring-context/src/main/java/org/springframework/context/event/EventListenerMethodProcessor.java">EventListenerMethodProcessor.java</a>
 */
@Slf4j
public class SlashAnnotationProcessor implements SmartInitializingSingleton, BeanFactoryPostProcessor {

  /**
   * The list of packages to be ignored from application context scanning
   */
  private static final String[] IGNORED_PACKAGES = {
      "org.springframework.",
      "com.symphony.bdk.gen.",
      "com.symphony.bdk.core."
  };

  /**
   * The {@link ActivityRegistry} is used here to register the slash activities.
   * Lazy-loaded from application context to make sure the processor is registered first before starting to create beans.
   */
  private ActivityRegistry activityRegistry;
  private ConfigurableListableBeanFactory beanFactory;

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    this.beanFactory = beanFactory;
  }

  @Override
  public void afterSingletonsInstantiated() {
    Assert.state(this.beanFactory != null, "No ConfigurableListableBeanFactory set");

    final String[] beanNames = this.beanFactory.getBeanNamesForType(Object.class);

    for (String beanName : beanNames) {
      if (!ScopedProxyUtils.isScopedTarget(beanName)) {

        final Class<?> type = this.determineTargetClass(beanName);

        if (type != null && isCandidateClass(type, Slash.class) && !isClassLocatedInPackages(type, IGNORED_PACKAGES)) {
          try {
            this.processBean(beanName, type);
          } catch (Throwable ex) {
            // just alert the developer
            log.warn("Failed to process @Slash annotation on bean with name '{}'", beanName, ex);
          }
        }
      }
    }
  }

  private ActivityRegistry getActivityRegistry() {
    if (this.activityRegistry == null) {
      this.activityRegistry = this.beanFactory.getBean(ActivityRegistry.class);
    }
    return this.activityRegistry;
  }

  @Generated // means excluded from test coverage (hard to test error cases)
  private Class<?> determineTargetClass(String beanName) {
    Class<?> type = null;
    try {
      type = AutoProxyUtils.determineTargetClass(this.beanFactory, beanName);
    } catch (Throwable ex) {
      // An unresolvable bean type, probably from a lazy bean - let's ignore it.
      log.trace("Could not resolve target class for bean with name '{}'", beanName, ex);
    }

    if (type != null && ScopedObject.class.isAssignableFrom(type)) {
      try {
        Class<?> targetClass = AutoProxyUtils.determineTargetClass(
            this.beanFactory,
            ScopedProxyUtils.getTargetBeanName(beanName)
        );
        if (targetClass != null) {
          type = targetClass;
        }
      } catch (Throwable ex) {
        // An invalid scoped proxy arrangement - let's ignore it.
        log.trace("Could not resolve target bean for scoped proxy '{}'", beanName, ex);
      }
    }

    return type;
  }

  private void processBean(final String beanName, final Class<?> targetType) {

    final Map<Method, Slash> annotatedMethods = this.getSlashAnnotatedMethods(beanName, targetType);

    for (final Method m : annotatedMethods.keySet()) {

      final Slash annotation = annotatedMethods.get(m);

      if (annotation.value().contains("{") && annotation.value().contains("}")) {
        processSlashCommandsWithArguments(beanName, m, annotation);
      } else {
        processSlashCommandsWithoutArgument(beanName, m, annotation);
      }
    }
  }

  private void processSlashCommandsWithArguments(String beanName, Method m, Slash annotation) {
    if (isMethodPrototypeValidForSlashArguments(m)) {
      registerSlashArgumentActivity(beanName, m, annotation);
    } else {
      log.warn("Method '{}' is annotated by @Slash but does not respect the expected prototype. "
          + "It must accept a first argument of type '{}' and following arguments of type String", m, CommandContext.class);
    }
  }

  private void registerSlashArgumentActivity(String beanName, Method m, Slash annotation) {
    final Object bean = this.beanFactory.getBean(beanName);

    getActivityRegistry().register(
        new SlashArgumentActivity(annotation.value(), annotation.mentionBot(), createSlashCommandWithArgumentsCallback(bean, m)));
  }

  private void processSlashCommandsWithoutArgument(String beanName, Method m, Slash annotation) {
    if (isMethodPrototypeValid(m)) {
      this.registerSlashMethod(beanName, m, annotation);
    } else {
      log.warn("Method '{}' is annotated by @Slash but does not respect the expected prototype. "
          + "It must accept a single argument of type '{}'", m, CommandContext.class);
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

  private void registerSlashMethod(String beanName, Method method, Slash annotation) {
    final Object bean = this.beanFactory.getBean(beanName);

    getActivityRegistry().register(
        SlashCommand.slash(annotation.value(), annotation.mentionBot(), createSlashCommandCallback(bean, method),
            annotation.description())
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

  protected static BiConsumer<CommandContext, Map<String, String>> createSlashCommandWithArgumentsCallback(Object bean, Method method) {
    Map<String, Integer> methodArgsNameToIndex = buildArgNameToIndexMap(method);

    return (c, args) -> {
      try {
        if (!args.keySet().equals(methodArgsNameToIndex.keySet())) {
          log.error("Unable to invoke @Slash method {} from bean {} because of argument mismatch. Slash command arguments in command pattern: {} vs arguments in the method: {}", method.getName(), bean.getClass(), args.keySet(), methodArgsNameToIndex.keySet());
          return;
        }

        Object[] methodArguments = buildSlashMethodParameters(methodArgsNameToIndex, c, args);

        method.invoke(bean, methodArguments);
      } catch (Throwable e) {
        log.error("Unable to invoke @Slash method {} from bean {}", method.getName(), bean.getClass(), e);
      }
    };
  }

  private static Object[] buildSlashMethodParameters(Map<String, Integer> methodArgsNameToIndex, CommandContext c,
      Map<String, String> args) {
    Object[] methodArguments = new Object[methodArgsNameToIndex.size() + 1];
    methodArguments[0] = c;
    methodArgsNameToIndex.forEach((k, v) -> methodArguments[v] = args.get(k));
    return methodArguments;
  }

  private static Map<String, Integer> buildArgNameToIndexMap(Method method) {
    Map<String, Integer> argsNameToIndex = new HashMap<>();

    final Parameter[] parameters = method.getParameters();
    for (int i = 1; i < parameters.length; i++) {
      argsNameToIndex.put(parameters[i].getName(), i);
    }
    return argsNameToIndex;
  }

  private static boolean isMethodPrototypeValid(Method m) {
    return m.getParameterCount() == 1 && m.getParameters()[0].getType().equals(CommandContext.class);
  }

  private static boolean isMethodPrototypeValidForSlashArguments(Method m) {
    final Parameter[] parameters = m.getParameters();

    if (!(m.getParameterCount() >= 1 && parameters[0].getType().equals(CommandContext.class))) {
      return false;
    }
    for (int i = 1; i < parameters.length; i++) {
      if (!parameters[i].getType().equals(String.class)) {
        return false;
      }
    }
    return true;
  }

  private static boolean isClassLocatedInPackages(Class<?> clazz, String... packagePrefixes) {
    return Stream.of(packagePrefixes).anyMatch(clazz.getName()::startsWith);
  }
}
