package com.symphony.bdk.bot.sdk.webapi.security.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.symphony.bdk.bot.sdk.webapi.security.JwtCookieFilter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.logging.ConditionEvaluationReportLoggingListener;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

public class JwtCookieDisabledTest {

  private final ApplicationContextRunner runner = new ApplicationContextRunner()
      .withInitializer(new ConditionEvaluationReportLoggingListener())
      .withUserConfiguration(JwtCookieFilter.class);

  @Test
  public void testShouldBeDisabled() {
    runner.withPropertyValues("jwt-cookie.enable=false")
        .run(context -> assertAll(
            () -> assertThat(context).doesNotHaveBean(JwtCookieFilter.class)
        ));
  }

  @Test
  public void testShouldBeEnabled() {
    runner.withPropertyValues("jwt-cookie.enable=true")
        .run(context -> context.assertThat().hasSingleBean(JwtCookieFilter.class));
  }

  @Test
  public void testShouldBeEnabledWithouProperty() {
    runner.run(context -> context.assertThat().hasSingleBean(JwtCookieFilter.class));
  }
}
