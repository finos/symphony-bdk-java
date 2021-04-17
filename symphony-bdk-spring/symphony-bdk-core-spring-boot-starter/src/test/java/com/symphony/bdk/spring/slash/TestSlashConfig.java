package com.symphony.bdk.spring.slash;

import com.symphony.bdk.core.activity.command.CommandContext;
import com.symphony.bdk.spring.annotation.Slash;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

@Configuration
public class TestSlashConfig {

  /**
   * Allows to ensure that only beans with "singleton" scope are scanned by com.symphony.bdk.spring.config.st.BdkActivityConfig.SlashAnnotationProcessor
   */
  @Bean("foobar-with-prototype-scope")
  @Scope("prototype")
  public FooBar prototypeFooBar() {
    return new FooBar();
  }

  @Lazy
  @Bean("foobar-lazy")
  public LazyFooBar lazyFooBar() {
    return new LazyFooBar();
  }

  @Bean("foobar")
  public FooBar fooBar() {
    return new FooBar();
  }

  public static class FooBar {

    @Slash("/foo-bar")
    public void onSlashFooBar(CommandContext commandContext) {}
  }

  public static class LazyFooBar {

    @Slash("/lazy-foo-bar")
    public void onSlashLazyFooBar(CommandContext commandContext) {}
  }
}
