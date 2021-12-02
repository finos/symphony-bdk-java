package com.symphony.bdk.core.activity.command;

import com.symphony.bdk.core.activity.ActivityMatcher;
import com.symphony.bdk.core.service.datafeed.EventException;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import javax.annotation.Nonnull;

public class SlashMentionArgumentActivity extends SlashArgumentActivity {

  private int numberOfMentions;

  public SlashMentionArgumentActivity(@Nonnull String slashCommandPattern, boolean requiresBotMention,
      @Nonnull BiConsumer<CommandContext, Map<String, Object>> callback) {
    super(slashCommandPattern, requiresBotMention, callback);

    numberOfMentions = StringUtils.countMatches(slashCommandPattern, '{');
    if (requiresBotMention) {
      numberOfMentions++;
    }
  }

  @Override
  protected ActivityMatcher<CommandContext> matcher() throws EventException {
    return c -> super.matcher().matches(c) && c.getMentions().size() == numberOfMentions; // TODO to be improved based on command pattern
  }

  @Override
  protected void onActivity(CommandContext context) throws EventException {
    final Map<String, String> arguments = this.antMatcher.extractUriTemplateVariables(this.patternSupplier().get(), context.getTextContent());
    // convert all to mentions
    final List<Mention> mentions = getMentions(context);

    final Map<String, Object> finalArguments = new HashMap<>();
    for (Map.Entry<String, String> stringStringEntry : arguments.entrySet()) {
      finalArguments.put(stringStringEntry.getKey(), mentions.stream().filter(m -> m.getText().equals(stringStringEntry.getValue())).findAny().orElse(null));
    }

    this.callback.accept(context, finalArguments);
  }

  private List<Mention> getMentions(CommandContext context) {
    if (this.requiresBotMention) {
      // first mention is bot mention
      return context.getMentions().subList(1, context.getMentions().size());
    }
    return context.getMentions();
  }

}
