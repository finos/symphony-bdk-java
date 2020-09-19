package com.symphony.bdk.spring.slash;

import com.symphony.bdk.core.activity.command.CommandContext;
import com.symphony.bdk.spring.annotation.Slash;

import org.springframework.stereotype.Component;

/**
 * Simple bean that contains a slash method.
 */
@Component
public class SlashBean {

  @Slash("/test")
  public void onTest(CommandContext context) {
    // nothing to be done here
  }
}
