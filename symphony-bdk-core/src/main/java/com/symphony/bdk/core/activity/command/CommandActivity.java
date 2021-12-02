package com.symphony.bdk.core.activity.command;

import static com.symphony.bdk.core.service.datafeed.util.RealTimeEventsBinder.bindRealTimeListener;

import com.symphony.bdk.core.activity.AbstractActivity;
import com.symphony.bdk.core.activity.exception.FatalActivityExecutionException;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.core.service.message.exception.PresentationMLParserException;
import com.symphony.bdk.core.service.message.util.MessageParser;
import com.symphony.bdk.core.service.message.util.PresentationMLParser;
import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4MessageSent;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
   * The dedicated {@link RealTimeEventListener} for the activity
   */
  private RealTimeEventListener listener;

  /**
   * {@inheritDoc}
   */
  @Override
  protected void bindToRealTimeEventsSource(Consumer<RealTimeEventListener> realTimeEventsSource) {
    if (listener == null) {
      listener = new RealTimeEventListener() {
        @Override
        public void onMessageSent(V4Initiator initiator, V4MessageSent event) {
          processEvent(initiator, event);
        }
      };
    }
    bindRealTimeListener(realTimeEventsSource, listener);
  }

  /**
   * {@inheritDoc}
   */
  @SneakyThrows
  protected void beforeMatcher(C context) {
    try {
      final String textContent =
          PresentationMLParser.getTextContent(context.getSourceEvent().getMessage().getMessage());
      context.setTextContent(textContent);

      // Fails if message contains a word beginning with @ without being an actual mention
      List<String> mentionsText = Arrays.stream(textContent.split("\\s+")).filter(s -> s.startsWith("@")).collect(Collectors.toList());
      final List<Long> userIds = MessageParser.getMentions(context.getSourceEvent().getMessage());

      List<Mention> mentions = new ArrayList<>();
      for (int i = 0; i < userIds.size(); i++) {
        mentions.add(new Mention(mentionsText.get(i), userIds.get(i)));
      }

      context.setMentions(mentions);
    } catch (PresentationMLParserException e) {
      throw new FatalActivityExecutionException(this.getInfo(), "Unable to parse presentationML", e);
    }
  }
}

