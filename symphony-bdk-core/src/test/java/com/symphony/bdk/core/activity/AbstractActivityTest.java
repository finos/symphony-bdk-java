package com.symphony.bdk.core.activity;

import static org.junit.jupiter.api.Assertions.*;

import com.symphony.bdk.core.activity.form.TestFormReplyActivity;
import com.symphony.bdk.core.service.datafeed.EventException;
import com.symphony.bdk.core.service.datafeed.EventPayload;
import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4Stream;
import com.symphony.bdk.gen.api.model.V4SymphonyElementsAction;

import lombok.experimental.Delegate;
import org.junit.jupiter.api.Test;

import java.time.Instant;

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
  void shouldFailOnBeforeMatcherError() {

    final TestFormReplyActivity act = new TestFormReplyActivity();
    act.setBeforeMatcher(c -> {
      throw new EventException("Error while executing beforeMatcher callback.");
    });

    assertThrows(EventException.class,
        () -> act.processEvent(new V4Initiator(), new V4SymphonyElementsAction().stream(new V4Stream())));
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
  void shouldFailOnMatcherError() {

    final TestFormReplyActivity act = new TestFormReplyActivity();
    act.setMatcher(c -> {
      throw new EventException("Error while executing matcher.");
    });

    assertThrows(EventException.class,
        () -> act.processEvent(new V4Initiator(), new V4SymphonyElementsAction()));
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

  @Test
  void shouldFailOnActivityExecutionError() {

    final TestFormReplyActivity act = new TestFormReplyActivity();
    act.setOnActivity(c -> {
      throw new EventException("Error while executing onActivity.");
    });

    assertThrows(EventException.class,
        () -> act.processEvent(new V4Initiator(), new V4SymphonyElementsActionEvent(new V4SymphonyElementsAction())));
  }

  static class V4SymphonyElementsActionEvent extends V4SymphonyElementsAction implements EventPayload {
    @Delegate
    V4SymphonyElementsAction elementsAction;

    public V4SymphonyElementsActionEvent(V4SymphonyElementsAction elementsAction) {
      this.elementsAction = elementsAction;
    }

    @Override
    public Long getEventTimestamp() {
      return Instant.now().toEpochMilli();
    }

    @Override
    public void setEventTimestamp(Long eventTimestamp) {

    }
  }
}
