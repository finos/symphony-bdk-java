package com.symphony.bdk.examples.activity;

import static com.symphony.bdk.core.config.BdkConfigLoader.loadFromSymphonyDir;

import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.activity.command.SlashArgumentActivity;

public class SlashCommandsWithArgs {

  public static void main(String[] args) throws Exception {
    final SymphonyBdk bdk = new SymphonyBdk(loadFromSymphonyDir("config.yaml"));

    bdk.activities().register(new SlashArgumentActivity("/echo {one} {two}", true, (context, slashArgs) ->
        bdk.messages().send(context.getStreamId(), "Args: " + slashArgs)
    ));

    bdk.datafeed().start();
  }


}
