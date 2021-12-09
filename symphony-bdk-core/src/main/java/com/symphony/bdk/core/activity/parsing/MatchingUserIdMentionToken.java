package com.symphony.bdk.core.activity.parsing;

import com.symphony.bdk.core.activity.parsing.input.Mention;

import org.apiguardian.api.API;

import java.util.function.Supplier;
import java.util.regex.Pattern;

@API(status = API.Status.INTERNAL)
public class MatchingUserIdMentionToken implements CommandToken {

  private Supplier<Long> matchingUserId;

  public MatchingUserIdMentionToken(Supplier<Long> matchingUserId) {
    this.matchingUserId = matchingUserId;
  }

  @Override
  public Pattern getRegexPattern() {
    return null;
  }

  @Override
  public boolean matches(Object inputToken) {
    return inputToken instanceof Mention && ((Mention) inputToken).getUserId().equals(matchingUserId.get());
  }
}
