package com.symphony.bdk.core.activity.form;

import static org.junit.jupiter.api.Assertions.*;

import com.symphony.bdk.core.service.datafeed.DatafeedLoop;
import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4Stream;
import com.symphony.bdk.gen.api.model.V4SymphonyElementsAction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

/**
 * Test class for the {@link FormReplyActivity}.
 */
@ExtendWith(MockitoExtension.class)
class FormReplyActivityTest {

  private TestFormReplyActivity act;

  @Mock
  DatafeedLoop datafeedService;

  @BeforeEach
  void setUp() {
    act = new TestFormReplyActivity();
    act.bindToRealTimeEventsSource(datafeedService::subscribe);
  }

  @Test
  void testMatcher() {

    act.setMatcher(c -> c.getFormId().equals("test-form"));

    final FormReplyContext context = createContext();

    context.setFormId("test-form");
    assertTrue(act.matcher().matches(context));

    context.setFormId("form-test");
    assertFalse(act.matcher().matches(context));
  }

  @Test
  void testBeforeMatcher() {

    final FormReplyContext context = createContext();
    context.getSourceEvent().setFormId("formId");
    context.getSourceEvent().setFormValues(Collections.singletonMap("foo", "bar"));
    context.getSourceEvent().setStream(new V4Stream().streamId("streamId"));
    context.getSourceEvent().setFormMessageId("formMessageId");

    act.beforeMatcher(context);

    assertEquals("formId", context.getFormId());
    assertEquals("formMessageId", context.getFormMessageId());
    assertEquals("streamId", context.getStreamId());
    assertEquals("bar", context.getFormValue("foo"));
    assertNull(context.getFormValue("not-existing"));
  }

  private static FormReplyContext createContext() {
    return new FormReplyContext(new V4Initiator(), new V4SymphonyElementsAction());
  }
}
