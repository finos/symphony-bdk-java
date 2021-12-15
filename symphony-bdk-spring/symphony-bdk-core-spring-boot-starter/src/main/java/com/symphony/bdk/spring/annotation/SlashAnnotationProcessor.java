package com.symphony.bdk.spring.annotation;

import static org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation;
import static org.springframework.core.annotation.AnnotationUtils.isCandidateClass;

import com.symphony.bdk.core.activity.ActivityRegistry;
import com.symphony.bdk.core.activity.command.CommandContext;
import com.symphony.bdk.core.activity.command.SlashCommand;
import com.symphony.bdk.core.activity.exception.SlashCommandSyntaxException;
import com.symphony.bdk.core.activity.parsing.SlashCommandPattern;

import lombok.Generated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.autoproxy.AutoProxyUtils;
import org.springframework.aop.scope.ScopedObject;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.util.Assert;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Registers {@link Slash} methods as individual {@link com.symphony.bdk.core.activity.command.SlashCommand} instances
 * within the {@link ActivityRegistry}.
 *
 * @see <a href="https://github.com/spring-projects/spring-framework/blob/master/spring-context/src/main/java/org/springframework/context/event/EventListenerMethodProcessor.java">EventListenerMethodProcessor.java</a>
 */
@Slf4j
@Generated // means excluded from test coverage: error cases hard to test,
// createSlashCommandCallback not testable with arguments because DefaultParameterNameDiscoverer not working with Mockito mocks/spies
public class SlashAnnotationProcessor implements SmartInitializingSingleton, BeanFactoryPostProcessor {

  /**
   * The list of packages to be ignored from application context scanning
   */
  private static final String[] IGNORED_PACKAGES = {
      "org.springframework.",
      "com.symphony.bdk.gen.",
      "com.symphony.bdk.core."
  };

  private static final ParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

  /**
   * Key is the annotated slash method, value is the map of (parameter name, index)
   */
  private static final Map<Method, Map<String, Integer>> METHOD_TO_ARGUMENT_INDEXES = new HashMap<>();

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

      if (isMethodPrototypeValid(m, annotation.value())) {
        this.registerSlashMethod(beanName, m, annotation);
      } else {
        log.warn("Method '{}' is annotated by @Slash but does not respect the expected prototype. "
            + "It must accept a first argument of type '{}' and (potential) other arguments based on the slash command pattern.",
            m, CommandContext.class);
      }
    }
  }

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
        final Map<String, Integer> methodParameterIndexes = METHOD_TO_ARGUMENT_INDEXES.get(method);
        final Object[] slashMethodParameters = buildSlashMethodParameters(methodParameterIndexes, c);

        method.invoke(bean, slashMethodParameters);
      } catch (Throwable e) {
        log.error("Unable to invoke @Slash method {} from bean {}", method.getName(), bean.getClass(), e);
      }
    };
  }

  private static Object[] buildSlashMethodParameters(Map<String, Integer> methodParameterIndexes, CommandContext c) {
    Object[] methodArguments = new Object[methodParameterIndexes.size() + 1];
    methodArguments[0] = c; // first method argument is always the CommandContext
    methodParameterIndexes.forEach((k, v) -> methodArguments[v] = c.getArguments().get(k));
    return methodArguments;
  }

  private static boolean isMethodPrototypeValid(Method m, String slashCommandDefinition) {
    try {
      final Map<String, ? extends Class<?>> slashArgumentDefinitions =
          new SlashCommandPattern(slashCommandDefinition).getArgumentDefinitions();

      return hasCorrectNumberOfParameters(m, slashArgumentDefinitions) && hasFirstParameterOfTypeCommandContext(m)
          && hasMatchingParameterNamesAndTypes(m, slashArgumentDefinitions);
    } catch (SlashCommandSyntaxException e) {
      log.warn("Unable to invoke @Slash method {} from bean {} due to invalid slash command value: {}", m.getName(),
          m.getDeclaringClass(), slashCommandDefinition, e);
      return false;
    }
  }

  private static boolean hasMatchingParameterNamesAndTypes(Method m, Map<String, ? extends Class<?>> slashArgumentDefinitions) {
    final String[] parameterNames = PARAMETER_NAME_DISCOVERER.getParameterNames(m);
    if (parameterNames == null) {
      log.warn("Unable to retrieve method parameter names, cannot register slash method");
      return false;
    }

    final HashMap<String, Integer> argumentIndexes = new HashMap<>(); // to be potentially cached for later
    for (int i = 1; i < m.getParameters().length; i++) { // we skip first parameter as it should be of CommandContext type (checked before)
      if (!areNameAndTypeInSlashArguments(parameterNames[i], m.getParameters()[i].getType(), slashArgumentDefinitions)) {
        return false;
      }
      argumentIndexes.put(parameterNames[i], i);
    }

    // Signature is correct: cache results
    METHOD_TO_ARGUMENT_INDEXES.put(m, argumentIndexes);

    return true;
  }

  private static boolean hasFirstParameterOfTypeCommandContext(Method m) {
    return m.getParameters()[0].getType().equals(CommandContext.class);
  }

  private static boolean hasCorrectNumberOfParameters(Method m, Map<String, ? extends Class<?>> slashArgumentDefinitions) {
    return m.getParameterCount() == 1 + slashArgumentDefinitions.size();
  }

  private static boolean areNameAndTypeInSlashArguments(String parameterName, Class<?> parameterType, Map<String, ? extends Class<?>> slashArgumentDefinitions) {
    final Class<?> slashArgumentType = slashArgumentDefinitions.get(parameterName);
    return slashArgumentType != null && slashArgumentType.equals(parameterType);
  }

  private static boolean isClassLocatedInPackages(Class<?> clazz, String... packagePrefixes) {
    return Stream.of(packagePrefixes).anyMatch(clazz.getName()::startsWith);
  }

  private ActivityRegistry getActivityRegistry() {
    if (this.activityRegistry == null) {
      this.activityRegistry = this.beanFactory.getBean(ActivityRegistry.class);
    }
    return this.activityRegistry;
  }
}
