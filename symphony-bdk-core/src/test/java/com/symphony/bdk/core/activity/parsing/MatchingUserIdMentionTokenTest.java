package com.symphony.bdk.core.activity.parsing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MatchingUserIdMentionTokenTest {

  @Test
  void test() {
    final long matchingUserId = 1234L;
    MatchingUserIdMentionToken mentionToken = new MatchingUserIdMentionToken(() -> matchingUserId);

    assertEquals(Mention.class, mentionToken.getTokenType());
    assertFalse(mentionToken.matches(""));
    assertFalse(mentionToken.matches(new Mention("@User", 9876L)));
    assertTrue(mentionToken.matches(new Mention("@User", matchingUserId)));
  }

}
