package com.symphony.bdk.examples.activity;

import static com.symphony.bdk.core.config.BdkConfigLoader.loadFromSymphonyDir;

import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.activity.command.SlashArgumentActivity;
import com.symphony.bdk.core.activity.command.SlashMentionArgumentActivity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SlashCommandsWithArgs {

  public static void main(String[] args) throws Exception {
    final SymphonyBdk bdk = new SymphonyBdk(loadFromSymphonyDir("config.yaml"));

    bdk.activities().register(new SlashArgumentActivity("/echo {one} {two}", true, (context, slashArgs) ->
        bdk.messages().send(context.getStreamId(), "Args: " + slashArgs)
    ));

    bdk.activities().register(new SlashMentionArgumentActivity("/mentions {one} {two}", true, (context, slashArgs) ->
        log.info("Received args: {}", slashArgs)
    ));

    bdk.datafeed().start();
  }


}
