package com.symphony.bdk.core.activity.command;

import com.symphony.bdk.core.activity.ActivityMatcher;

import org.apiguardian.api.API;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Experimental implementation of the default {@link CommandActivity} that ease parsing of the text message.
 */
@API(status = API.Status.EXPERIMENTAL)
public abstract class PatternCommandActivity<C extends CommandContext> extends CommandActivity<C> {

  protected abstract Pattern pattern();

  protected void prepareContext(C context, Matcher matcher) {
    // to be implemented
  }

  @Override
  protected void beforeMatcher(C context) {
    super.beforeMatcher(context);
    final Matcher matcher = this.pattern().matcher(context.getTextContent());
    if (matcher.matches()) {
      this.prepareContext(context, matcher);
    }
  }

  @Override
  public ActivityMatcher<C> matcher() {
    return c -> this.pattern().matcher(c.getTextContent()).matches();
  }
}
