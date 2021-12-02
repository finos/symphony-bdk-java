package com.symphony.bdk.core.activity.command;

import com.symphony.bdk.core.activity.ActivityMatcher;
import com.symphony.bdk.core.activity.model.ActivityInfo;
import com.symphony.bdk.core.service.datafeed.EventException;
import com.symphony.bdk.core.util.AntPathMatcher;

import com.symphony.bdk.gen.api.model.V4Initiator;

import com.symphony.bdk.gen.api.model.V4MessageSent;

import org.apiguardian.api.API;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

@API(status = API.Status.EXPERIMENTAL)
public class SlashArgumentActivity extends CommandActivity<CommandContext> {

  private final String slashCommandPattern;
  protected final boolean requiresBotMention;
  protected final BiConsumer<CommandContext, Map<String, Object>> callback;
  protected final AntPathMatcher antMatcher;

  public SlashArgumentActivity(@Nonnull String slashCommandPattern, boolean requiresBotMention,
      @Nonnull BiConsumer<CommandContext, Map<String, Object>> callback) {
    this.slashCommandPattern = slashCommandPattern;
    this.requiresBotMention = requiresBotMention;
    this.callback = callback;
    this.antMatcher = new AntPathMatcher(" ");
  }

  @Override
  protected ActivityMatcher<CommandContext> matcher() throws EventException {
    return c -> this.antMatcher.match(patternSupplier().get(), c.getTextContent());
  }

  protected Supplier<String> patternSupplier() {
    return () -> {
      final String botMention = this.requiresBotMention ? "@" + this.getBotDisplayName() + " " : "";
      return botMention + this.slashCommandPattern;
    };
  }

  @Override
  protected void onActivity(CommandContext context) throws EventException {
    final Map<String, Object> arguments = new HashMap<>(this.antMatcher.extractUriTemplateVariables(this.patternSupplier().get(), context.getTextContent()));
    this.callback.accept(context, arguments);
  }

  @Override
  protected ActivityInfo info() {
    return null;
  }

  protected CommandContext createContextInstance(V4Initiator initiator, V4MessageSent event) {
    return new CommandContext(initiator, event);
  }
}
