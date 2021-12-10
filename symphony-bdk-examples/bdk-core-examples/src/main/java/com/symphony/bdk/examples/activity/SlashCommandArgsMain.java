package com.symphony.bdk.examples.activity;

import static com.symphony.bdk.core.activity.command.SlashCommand.slash;
import static com.symphony.bdk.core.config.BdkConfigLoader.loadFromSymphonyDir;

import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.activity.parsing.Cashtag;
import com.symphony.bdk.core.activity.parsing.Hashtag;
import com.symphony.bdk.core.activity.parsing.Mention;

public class SlashCommandArgsMain {

  public static void main(String[] args) throws Exception {
    // setup SymphonyBdk facade object
    final SymphonyBdk bdk = new SymphonyBdk(loadFromSymphonyDir("config.yaml"));

    // Replies with the information of a mention
    bdk.activities().register(slash("/echo {@mention}", true, context ->
        {
          final Mention mention = context.getArguments().getAsMention("mention");
          bdk.messages().send(context.getStreamId(), "Mentioned user: " + mention.getUserDisplayName() + "" + ", id: " + mention.getUserId());
        }
    ));

    // Replies with the information of a hashtag
    bdk.activities().register(slash("/echo {#hashtag}", true, context ->
        {
          final Hashtag hashtag = context.getArguments().getAsHashtag("hashtag");
          bdk.messages().send(context.getStreamId(), "Hashtag value: " + hashtag.getValue());
        }
    ));

    // Replies with the information of a cashtag
    bdk.activities().register(slash("/echo {$cashtag}", true, context ->
        {
          final Cashtag cashtag = context.getArguments().getAsCashtag("cashtag");
          bdk.messages().send(context.getStreamId(), "Cashtag value: " + cashtag.getValue());
        }
    ));

    // Echoes a string
    bdk.activities().register(slash("/echo {argument}", true, context ->
        bdk.messages().send(context.getStreamId(), "Received argument: " + context.getArguments().getAsString("argument"))
    ));

    bdk.datafeed().start();
  }
}
