package com.symphony.bdk.core.activity.command;

import static com.symphony.bdk.core.service.datafeed.util.RealTimeEventsBinder.bindOnMessageSent;

import com.symphony.bdk.core.activity.AbstractActivity;
import com.symphony.bdk.core.activity.exception.FatalActivityExecutionException;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.core.util.PresentationMLParser;
import com.symphony.bdk.core.util.exception.PresentationMLParserException;
import com.symphony.bdk.gen.api.model.V4MessageSent;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.util.function.Consumer;

/**
 * A form reply activity corresponds to any message send in a chat where the bot is part of.
 */
@Slf4j
@API(status = API.Status.STABLE)
public abstract class CommandActivity<C extends CommandContext> extends AbstractActivity<V4MessageSent, C> {

  /**
   * The bot display name
   */
  @Getter @Setter private String botDisplayName;

  /**
   * {@inheritDoc}
   */
  @Override
  protected void bindToRealTimeEventsSource(Consumer<RealTimeEventListener> realTimeEventsSource) {
    bindOnMessageSent(realTimeEventsSource, this::processEvent);
  }

  /**
   * {@inheritDoc}
   */
  protected void beforeMatcher(C context) {
    try {
      context.setTextContent(
          PresentationMLParser.getMessageTextContent(context.getSourceEvent().getMessage().getMessage()));
    } catch (PresentationMLParserException e) {
      throw new FatalActivityExecutionException(this.getInfo(), "Unable to parse presentationML", e);
    }
  }
}

