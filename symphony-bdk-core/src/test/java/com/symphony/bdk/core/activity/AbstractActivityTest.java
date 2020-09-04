package com.symphony.bdk.core.activity;

import com.symphony.bdk.core.activity.form.TestFormReplyActivity;
import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4SymphonyElementsAction;

import org.junit.jupiter.api.Test;

/**
 * Test class for the {@link AbstractActivity}.
 */
class AbstractActivityTest {

  @Test
  void shouldNotFailOnBeforeMatcherError() {

    final TestFormReplyActivity act = new TestFormReplyActivity();
    act.setBeforeMatcher(c -> {
      throw new RuntimeException("Error while executing beforeMatcher callback.");
    });

    act.processEvent(new V4Initiator(), new V4SymphonyElementsAction());
    // it should not fail
  }

  @Test
  void shouldNotFailOnMatcherError() {

    final TestFormReplyActivity act = new TestFormReplyActivity();
    act.setMatcher(c -> {
      throw new RuntimeException("Error while executing matcher.");
    });

    act.processEvent(new V4Initiator(), new V4SymphonyElementsAction());
    // it should not fail
  }

  @Test
  void shouldNotFailOnActivityExecutionError() {

    final TestFormReplyActivity act = new TestFormReplyActivity();
    act.setOnActivity(c -> {
      throw new RuntimeException("Error while executing onActivity.");
    });

    act.processEvent(new V4Initiator(), new V4SymphonyElementsAction());
    // it should not fail
  }
}
