package com.symphony.bdk.core.activity.command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.symphony.bdk.core.activity.model.ActivityInfo;
import com.symphony.bdk.core.service.datafeed.EventException;
import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4Message;
import com.symphony.bdk.gen.api.model.V4MessageSent;
import com.symphony.bdk.gen.api.model.V4Stream;

import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class PatternCommandActivityTest {

  static class ConcretePatternActivity extends PatternCommandActivity<CommandContext>{

    private Pattern pattern;

    public ConcretePatternActivity(String pattern) {
      this.pattern = Pattern.compile(pattern);
    }

    @Override
    public void prepareContext(CommandContext context, Matcher matcher) {
      super.prepareContext(context, matcher);
    }

    // In order to call protected processEvent method
    public void callProcessEvent(V4Initiator initiator, V4MessageSent messageSent){
      processEvent(initiator, messageSent);
    }

    @Override
    public void onActivity(CommandContext context) throws EventException {
    }

    @Override
    protected ActivityInfo info() {
      return null;
    }

    @Override
    protected Pattern pattern() {
      return pattern;
    }

    @Override
    protected CommandContext createContextInstance(V4Initiator initiator, V4MessageSent event) {
      return new CommandContext(initiator, event);
    }
  }

  @Test
  void testPatternActivityMatchingInput() {
    final ConcretePatternActivity activity = spy(new ConcretePatternActivity("^hello$"));

    activity.callProcessEvent(new V4Initiator(), createEvent("<div><p><span>hello</span></p></div>"));

    verify(activity).prepareContext(any(), any());
    verify(activity).onActivity(any());
  }

  private V4MessageSent createEvent(String message) {
    return new V4MessageSent()
        .message(new V4Message().message(message).messageId("").stream(new V4Stream().streamId("")));
  }

  @Test
  void testPatternActivityMismatchingInput() {
    final ConcretePatternActivity activity = spy(new ConcretePatternActivity("^hello$"));

    activity.callProcessEvent(new V4Initiator(), createEvent("<div><p><span>hellow</span></p></div>"));

    verify(activity, never()).prepareContext(any(), any());
    verify(activity, never()).onActivity(any());
  }
}
