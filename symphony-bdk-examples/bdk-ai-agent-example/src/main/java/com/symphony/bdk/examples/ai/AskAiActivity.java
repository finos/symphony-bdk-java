package com.symphony.bdk.examples.ai;

import com.symphony.bdk.core.activity.command.PatternCommandActivity;
import com.symphony.bdk.core.activity.model.ActivityInfo;
import com.symphony.bdk.core.activity.model.ActivityType;
import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.examples.ai.context.AskAiContext;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Forwards any message addressed to the bot to the LangChain4j {@link Assistant} and replies
 * with its answer. Conversation memory is scoped per (stream, user), see {@link MemoryIds}.
 */
@Slf4j
@RequiredArgsConstructor
public class AskAiActivity extends PatternCommandActivity<AskAiContext> {

  private final Assistant assistant;
  private final MessageService messageService;

  @Override
  public Pattern pattern() {
    return Pattern.compile("^@" + this.getBotDisplayName() + "\\s+(.*)$", Pattern.DOTALL);
  }

  @Override
  protected void prepareContext(AskAiContext context, Matcher matcher) {
    context.setPrompt(matcher.group(1));
  }

  @Override
  public void onActivity(AskAiContext context) {
    final String memoryId = MemoryIds.of(context.getStreamId(), context.getInitiator().getUser().getUserId());
    log.info("Asking the AI agent (memoryId={}): {}", memoryId, context.getPrompt());

    final String answer = this.assistant.chat(memoryId, context.getPrompt());

    this.messageService.send(context.getStreamId(), "<messageML>" + answer + "</messageML>");
  }

  @Override
  protected ActivityInfo info() {
    return new ActivityInfo()
        .type(ActivityType.COMMAND)
        .name("AI agent command")
        .description("Usage: @BotMention {any question}");
  }
}
