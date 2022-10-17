package com.symphony.bdk.spring.slash;

import com.symphony.bdk.core.activity.command.CommandContext;
import com.symphony.bdk.core.activity.parsing.Cashtag;
import com.symphony.bdk.core.activity.parsing.Hashtag;
import com.symphony.bdk.core.activity.parsing.Mention;
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
    // nothing to be done here
  }

  @Slash("/hello {arg}")
  public void withStringArgument(CommandContext context, String arg) {
    // nothing to be done here
  }

  @Slash("/hello {arg1}{arg2}")
  public void withInvalidSlashCommandPattern(CommandContext context, String arg) {
    // nothing to be done here
  }

  @Slash("/hello {arg} {arg}")
  public void withDuplicatedArgs(CommandContext context, String arg) {
    // nothing to be done here
  }

  @Slash("/hello1 {arg} {@mention} {#hashtag} {$cashtag}")
  public void withArguments(CommandContext context, String arg, Mention mention, Hashtag hashtag, Cashtag cashtag) {
    // nothing to be done here
  }

  @Slash("/hello2 {arg} {@mention} {#hashtag} {$cashtag}")
  public void withArgumentDifferentOrder(CommandContext context, Mention mention, Cashtag cashtag, String arg, Hashtag hashtag) {
    // nothing to be done here
  }

  @Slash("/hello {argument}")
  public void withStringArgumentMismatchingName(CommandContext context, String arg) {
    // nothing to be done here
  }

  @Slash("/hello {@arg}")
  public void withStringArgumentMismatchingType(CommandContext context, String arg) {
    // nothing to be done here
  }
}
