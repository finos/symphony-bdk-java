package com.symphony.bdk.core.activity.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.symphony.bdk.core.activity.TestCommandActivity;
import com.symphony.bdk.core.activity.exception.FatalActivityExecutionException;
import com.symphony.bdk.core.service.datafeed.DatafeedService;
import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4Message;
import com.symphony.bdk.gen.api.model.V4MessageSent;
import com.symphony.bdk.gen.api.model.V4Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test class for the {@link CommandActivity}.
 */
@ExtendWith(MockitoExtension.class)
class CommandActivityTest {

  private TestCommandActivity act;

  @Mock DatafeedService datafeedService;

  @BeforeEach
  void setUp() {
    act = new TestCommandActivity();
    act.bindToRealTimeEventsSource(datafeedService::subscribe);
  }

  @Test
  void testMatcher() {

    act.setMatcher(c -> c.getTextContent().startsWith("foo"));

    final CommandContext context = createContext();

    context.setTextContent("foobar");
    assertTrue(act.matcher().matches(context));

    context.setTextContent("barfoo");
    assertFalse(act.matcher().matches(context));
  }

  @Test
  void testBeforeMatcher() {
    final CommandContext context = createContext();
    context.getSourceEvent().getMessage().setMessage("<div><p><span>hello world</span></p></div>");

    assertNull(context.getTextContent(), "Message text content must be null before beforeMatcher method.");
    act.beforeMatcher(context);
    assertEquals( "hello world", context.getTextContent());
  }

  @Test
  void testBeforeMatcherWithFailure() {
    final CommandContext context = createContext();
    context.getSourceEvent().getMessage().setMessage("<div<p><span>hello world<span></p></div>");

    assertThrows(FatalActivityExecutionException.class, () -> act.beforeMatcher(context));
  }

  private static CommandContext createContext() {
    return new CommandContext(new V4Initiator(), new V4MessageSent().message(new V4Message().stream(new V4Stream())));
  }
}
