package com.symphony.bdk.spring.annotation;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.extension.AuthSessionAware;
import com.symphony.bdk.core.extension.BdkConfigAware;
import com.symphony.bdk.core.extension.Extension;
import com.symphony.bdk.core.extension.HttpClientAware;
import com.symphony.bdk.http.api.ApiClientBuilderProvider;
import com.symphony.bdk.http.api.HttpClient;
import com.symphony.bdk.spring.SymphonyBdkCoreProperties;

import lombok.Generated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.autoproxy.AutoProxyUtils;
import org.springframework.aop.scope.ScopedObject;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.util.Assert;

import java.util.stream.Stream;

@Slf4j
public class ExtensionProcessor implements SmartInitializingSingleton, BeanFactoryPostProcessor {

  /**
   * The list of packages to be ignored from application context scanning
   */
  private static final String[] IGNORED_PACKAGES = {
      "org.springframework.",
      "com.symphony.bdk.gen.",
      "com.symphony.bdk.core."
  };

  private BdkConfig bdkConfig;
  private AuthSession botSession;
  private ConfigurableListableBeanFactory beanFactory;
  private ApiClientBuilderProvider apiClientBuilderProvider;

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    this.beanFactory = beanFactory;
  }

  @Override
  @Generated // means excluded from test coverage: error cases hard to test
  public void afterSingletonsInstantiated() {
    Assert.state(this.beanFactory != null, "No ConfigurableListableBeanFactory set");

    final String[] beanNames = this.beanFactory.getBeanNamesForType(Extension.class);
    for (String beanName : beanNames) {
      if (!ScopedProxyUtils.isScopedTarget(beanName)) {

        final Class<?> type = this.determineTargetClass(beanName);

        if (type != null
            && !isClassLocatedInPackages(type, IGNORED_PACKAGES)) {
          try {
            log.info("Found extension bean: {}", beanName);
            final Extension e = this.beanFactory.getBean(beanName, Extension.class);
            if (e instanceof AuthSessionAware) {
              ((AuthSessionAware) e).setAuthSession(getBotSession());
            }
            if (e instanceof BdkConfigAware) {
              ((BdkConfigAware) e).setBdkConfig(getBdkConfig());
            }
            if (e instanceof HttpClientAware) {
              ((HttpClientAware) e).setHttpClientBuilder(getHttpClientBuilder());
            }
          } catch (Throwable ex) {
            // just alert the developer
            log.warn("Failed to process @Slash annotation on bean with name '{}'", beanName, ex);
          }
        }
      }
    }
  }

  @Generated // means excluded from test coverage: error cases hard to test
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

  private static boolean isClassLocatedInPackages(Class<?> clazz, String... packagePrefixes) {
    return Stream.of(packagePrefixes).anyMatch(clazz.getName()::startsWith);
  }

  private BdkConfig getBdkConfig() {
    if (this.bdkConfig == null) {
      this.bdkConfig = this.beanFactory.getBean(SymphonyBdkCoreProperties.class);
    }
    return this.bdkConfig;
  }

  private AuthSession getBotSession() {
    if (this.botSession == null) {
      this.botSession = this.beanFactory.getBean(AuthSession.class);
    }
    return this.botSession;
  }

  private HttpClient.Builder getHttpClientBuilder() {
    if (this.apiClientBuilderProvider == null) {
      this.apiClientBuilderProvider = this.beanFactory.getBean(ApiClientBuilderProvider.class);
    }
    return HttpClient.builder(this.apiClientBuilderProvider);
  }
}
