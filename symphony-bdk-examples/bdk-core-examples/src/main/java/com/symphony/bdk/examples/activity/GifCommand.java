package com.symphony.bdk.examples.activity;

import com.symphony.bdk.core.activity.command.PatternCommandActivity;
import com.symphony.bdk.core.activity.model.ActivityInfo;
import com.symphony.bdk.core.activity.model.ActivityType;
import com.symphony.bdk.examples.activity.context.GifCommandContext;

import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class GifCommand extends PatternCommandActivity<GifCommandContext> {

  @Override
  public Pattern pattern() {
    return Pattern.compile("^@" + this.getBotDisplayName() + " /gif ([a-zA-Z]+).*$");
  }

  @Override
  protected void prepareContext(GifCommandContext context, Matcher matcher) {
    context.setCategory(matcher.group(1));
  }

  @Override
  public void onActivity(final GifCommandContext context) {
    log.info("Gif category is \"{}\"", context.getCategory());
  }

  @Override
  protected ActivityInfo info() {
    return new ActivityInfo().type(ActivityType.COMMAND)
        .name("Gif Random by Category command")
        .description("Usage: @BotMention /gif {category}");
  }
}
