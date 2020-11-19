package com.symphony.bdk.spring.slash;

import com.symphony.bdk.core.activity.command.CommandContext;
import com.symphony.bdk.spring.annotation.Slash;

import org.springframework.stereotype.Component;

/**
 * Simple bean that contains a slash method.
 */
@Component
public class TestSlashCommand {

  @Slash("/test")
  public void onTest(CommandContext context) {
    // nothing to be done here
  }

  @Slash("/error-test")
  public void onErrorTest(CommandContext context) {
    throw new RuntimeException("something wrong happened!");
  }

  @Slash("/hello")
  public void illegalPrototype() {
    //
  }
}
