package com.symphony.bdk.core.activity.command;

import com.symphony.bdk.core.activity.ActivityMatcher;
import com.symphony.bdk.core.activity.model.ActivityInfo;
import com.symphony.bdk.core.activity.model.ActivityType;
import com.symphony.bdk.core.activity.parsing.MatchResult;
import com.symphony.bdk.core.activity.parsing.MatchingUserIdMentionToken;
import com.symphony.bdk.core.activity.parsing.SlashCommandPattern;
import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4MessageSent;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apiguardian.api.API;

import java.util.Objects;
import java.util.function.Consumer;

import javax.annotation.Nonnull;

/**
 * A "slash" command if the most basic action that can be performed by an end-user through the chat.
 */
@API(status = API.Status.EXPERIMENTAL)
public class SlashCommand extends CommandActivity<CommandContext> {

  @Getter
  private final String slashCommandName;
  private final SlashCommandPattern commandPattern;
  private final boolean requiresBotMention;
  private final Consumer<CommandContext> callback;
  private final String description;

  /**
   * Returns a new {@link SlashCommand} instance.
   *
   * @param slashCommandPattern Pattern of the command (ex: '/gif' or 'gif {option} {{@literal @}mention}').
   * @param callback            Callback to be processed when command is detected.
   */
  public static SlashCommand slash(@Nonnull String slashCommandPattern, @Nonnull Consumer<CommandContext> callback) {
    return slash(slashCommandPattern, true, callback);
  }

  /**
   * Returns a new {@link SlashCommand} instance.
   *
   * @param slashCommandPattern Pattern of the command (ex: '/gif' or 'gif {option} {{@literal @}mention}').
   * @param requiresBotMention  Indicates whether the bot has to be mentioned in order to trigger the command.
   * @param callback            Callback to be processed when command is detected.
   * @throws IllegalArgumentException if command name if empty.
   */
  public static SlashCommand slash(@Nonnull String slashCommandPattern, boolean requiresBotMention,
      @Nonnull Consumer<CommandContext> callback) {
    return new SlashCommand(slashCommandPattern, requiresBotMention, callback, "");
  }

  /**
   * Returns a new {@link SlashCommand} instance.
   *
   * @param slashCommandPattern Pattern of the command (ex: '/gif' or 'gif {option} {{@literal @}mention}').
   * @param callback            Callback to be processed when command is detected.
   * @param description         The summary of the command.
   * @return a {@link SlashCommand} instance.
   */
  public static SlashCommand slash(@Nonnull String slashCommandPattern, @Nonnull Consumer<CommandContext> callback,
      String description) {
    return slash(slashCommandPattern, true, callback, description);
  }

  /**
   * Returns a new {@link SlashCommand} instance.
   *
   * @param slashCommandPattern Pattern of the command (ex: '/gif' or 'gif {option} {{@literal @}mention}').
   * @param requiresBotMention  Indicates whether the bot has to be mentioned in order to trigger the command.
   * @param callback            Callback to be processed when command is detected.
   * @param description         The summary of the command.
   * @return a {@link SlashCommand} instance.
   */
  public static SlashCommand slash(@Nonnull String slashCommandPattern, boolean requiresBotMention,
      @Nonnull Consumer<CommandContext> callback, String description) {
    return new SlashCommand(slashCommandPattern, requiresBotMention, callback, description);
  }

  /**
   * Default protected constructor, new instances from static methods only.
   */
  protected SlashCommand(@Nonnull String slashCommandPattern, boolean requiresBotMention,
      @Nonnull Consumer<CommandContext> callback, String description) {

    if (StringUtils.isEmpty(slashCommandPattern)) {
      throw new IllegalArgumentException("The slash command name cannot be empty.");
    }

    this.slashCommandName = slashCommandPattern;
    this.commandPattern = new SlashCommandPattern(this.slashCommandName);
    this.requiresBotMention = requiresBotMention;
    if (this.requiresBotMention) {
      this.commandPattern.prependToken(new MatchingUserIdMentionToken(
          () -> getBotUserId())); // specific token with no argument name that matches user ID
    }

    this.callback = callback;
    this.description = description;
  }

  @Override
  public ActivityMatcher<CommandContext> matcher() {
    return context -> {
      final MatchResult matchResult = this.commandPattern.getMatchResult(context.getSourceEvent().getMessage());
      if (matchResult.isMatching()) {
        context.setArguments(matchResult.getArguments());
      }

      return matchResult.isMatching();
    };
  }

  @Override
  public void onActivity(CommandContext context) {
    this.callback.accept(context);
  }

  @Override
  protected ActivityInfo info() {
    return new ActivityInfo()
        .type(ActivityType.COMMAND)
        .name(this.slashCommandName)
        .description(this.buildCommandDescription());
  }

  @Override
  protected CommandContext createContextInstance(V4Initiator initiator, V4MessageSent event) {
    return new CommandContext(initiator, event);
  }

  private String buildCommandDescription() {
    return this.requiresBotMention ? this.description + " (mention required)"
        : this.description + " (mention not required)";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {return true;}
    if (o == null || getClass() != o.getClass()) {return false;}
    SlashCommand that = (SlashCommand) o;
    return requiresBotMention == that.requiresBotMention && slashCommandName.equals(that.slashCommandName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(slashCommandName, requiresBotMention);
  }
}
