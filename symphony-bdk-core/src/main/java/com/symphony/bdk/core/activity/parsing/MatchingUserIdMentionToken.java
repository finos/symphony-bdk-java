package com.symphony.bdk.core.activity.parsing;

import org.apiguardian.api.API;

import java.util.function.Supplier;

/**
 * CommandToken which matches message token if it is a mention with a given user ID.
 */
@API(status = API.Status.INTERNAL)
public class MatchingUserIdMentionToken implements CommandToken {

  private Supplier<Long> matchingUserId;

  public MatchingUserIdMentionToken(Supplier<Long> matchingUserId) {
    this.matchingUserId = matchingUserId;
  }

  @Override
  public boolean matches(Object inputToken) {
    return inputToken instanceof Mention && ((Mention) inputToken).getUserId().equals(matchingUserId.get());
  }

  @Override
  public Class<?> getTokenType() {
    return Mention.class;
  }
}
