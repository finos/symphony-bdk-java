package com.symphony.bdk.core.activity.command;

import org.apache.commons.lang3.StringUtils;
import org.apiguardian.api.API;

import java.util.function.Consumer;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

/**
 * A "slash" command if the most basic action that can be performed by an end-user through the chat.
 */
@API(status = API.Status.EXPERIMENTAL)
public class SlashCommand extends PatternCommandActivity<CommandContext> {

  private final String slashCommandName;
  private final boolean requiresBotMention;
  private final Consumer<CommandContext> callback;

  /**
   * Returns a new {@link SlashCommand} instance.
   *
   * @param slashCommandName Identifier of the command (ex: '/gif' or 'gif').
   * @param callback Callback to be processed when command is detected.
   */
  public SlashCommand(@Nonnull String slashCommandName, @Nonnull Consumer<CommandContext> callback) {
    this(slashCommandName, true, callback);
  }

  /**
   * Returns a new {@link SlashCommand} instance.
   *
   * @param slashCommandName Identifier of the command (ex: '/gif' or 'gif').
   * @param requiresBotMention Indicates whether the bot has to be mentioned in order to trigger the command.
   * @param callback Callback to be processed when command is detected.
   *
   * @throws IllegalArgumentException if command name if empty.
   */
  public SlashCommand(@Nonnull String slashCommandName, boolean requiresBotMention, @Nonnull Consumer<CommandContext> callback) {

    if (StringUtils.isEmpty(slashCommandName)) {
      throw new IllegalArgumentException("The slash command name cannot be empty.");
    }

    this.slashCommandName = slashCommandName;
    this.requiresBotMention = requiresBotMention;
    this.callback = callback;
  }

  @Override
  public Pattern pattern() {
    final String botMention = this.requiresBotMention ? "@" + this.getBotDisplayName() + " " : "";
    return Pattern.compile("^" + botMention + this.slashCommandName + "$");
  }

  @Override
  public void onActivity(CommandContext context) {
    this.callback.accept(context);
  }
}
