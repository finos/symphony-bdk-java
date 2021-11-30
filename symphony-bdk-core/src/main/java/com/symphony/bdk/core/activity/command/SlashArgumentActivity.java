package com.symphony.bdk.core.activity.command;

import com.symphony.bdk.core.activity.ActivityMatcher;
import com.symphony.bdk.core.activity.model.ActivityInfo;
import com.symphony.bdk.core.service.datafeed.EventException;
import com.symphony.bdk.core.util.AntPathMatcher;

import org.apiguardian.api.API;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

@API(status = API.Status.EXPERIMENTAL)
public class SlashArgumentActivity extends CommandActivity<ArgumentCommandContext> {

  private final String slashCommandPattern;
  private final boolean requiresBotMention;
  private final Consumer<ArgumentCommandContext> callback;
  private final AntPathMatcher antMatcher; // spring core....

  public SlashArgumentActivity(@Nonnull String slashCommandPattern, boolean requiresBotMention,
      @Nonnull Consumer<ArgumentCommandContext> callback) {
    this.slashCommandPattern = slashCommandPattern;
    this.requiresBotMention = requiresBotMention;
    this.callback = callback;
    this.antMatcher = new AntPathMatcher(" ");
  }

  @Override
  protected ActivityMatcher<ArgumentCommandContext> matcher() throws EventException {
    return c -> this.antMatcher.match(patternSupplier().get(), c.getTextContent());
  }

  private Supplier<String> patternSupplier() {
    return () -> {
      final String botMention = this.requiresBotMention ? "@" + this.getBotDisplayName() + " " : "";
      return botMention + this.slashCommandPattern;
    };
  }

  @Override
  protected void onActivity(ArgumentCommandContext context) throws EventException {
    final Map<String, String> arguments =
        this.antMatcher.extractUriTemplateVariables(this.patternSupplier().get(), context.getTextContent());
    context.setArguments(arguments);
    this.callback.accept(context);
  }

  @Override
  protected ActivityInfo info() {
    return null;
  }
}
