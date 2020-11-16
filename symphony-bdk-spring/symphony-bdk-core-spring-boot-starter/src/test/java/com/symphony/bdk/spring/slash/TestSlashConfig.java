package com.symphony.bdk.spring.slash;

import com.symphony.bdk.core.activity.command.CommandContext;
import com.symphony.bdk.spring.annotation.Slash;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class TestSlashConfig {

  @Bean
  @Scope("prototype") // allows to ensure that only beans with "singleton" scope are scanned by com.symphony.bdk.spring.config.BdkActivityConfig.SlashAnnotationProcessor
  public FooBar fooBar() {
    return new FooBar();
  }

  public static class FooBar {
    @Slash("/foo-bar")
    public void onSlashFooBar(CommandContext commandContext) {}
  }
}
