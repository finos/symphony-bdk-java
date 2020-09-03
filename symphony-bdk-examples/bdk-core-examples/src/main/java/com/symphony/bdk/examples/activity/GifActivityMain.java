package com.symphony.bdk.examples.activity;

import static com.symphony.bdk.core.config.BdkConfigLoader.fromSymphonyDir;

import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.activity.Activity;
import com.symphony.bdk.core.activity.ActivityRegistry;
import com.symphony.bdk.core.activity.command.SlashCommand;
import com.symphony.bdk.core.activity.model.ActivityInfo;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>Activity API demonstration around a simple Gif bot logic.</p>
 * <br/>
 * <h3>
 * Available commands
 * </h3>
 * <table>
 *   <tr>
 *     <td><pre>@BotMention /gif</pre></td>
 *     <td>Displays the Gif category Elements form</td>
 *   </tr>
 *   <tr>
 *     <td><pre>@BotMention /gif category</pre></td>
 *     <td>Direct command including Gif category as parameter</td>
 *   </tr>
 * </table>
 */
@Slf4j
public class GifActivityMain {

  public static void main(String[] args) throws Exception {

    // setup SymphonyBdk facade object
    final SymphonyBdk bdk = new SymphonyBdk(fromSymphonyDir("config.yaml"));

    // register new activity that sends a gif to the chat
    bdk.activities().register(new GifCommand());

    // displays the Gif form on /gif command with no params
    bdk.activities().register(new SlashCommand("/gif", true, context ->
        // send message contains Elements form to select a gif category
        bdk.messages().send(context.getStreamId(), loadGifElementsForm())
    ));

    // register a "formReply" activity that handles the Gif category form submission
    bdk.activities().register(new GifFormReplyActivity());

    // display activities documentation in the console
    reportActivities(bdk.activities());

    // finally, start the datafeed loop
    bdk.datafeed().start();
  }

  private static void reportActivities(ActivityRegistry registry) {
    final List<ActivityInfo> infos = registry.getActivityList()
        .stream()
        .map(Activity::getInfo)
        .collect(Collectors.toList());

    log.info("--");
    log.info("-- ACTIVITIES REPORT --");
    for (ActivityInfo info : infos) {
      log.info("--");
      log.info("TYPE: {}", info.getType());
      log.info("NAME: {}", info.getName());
      log.info("DESC: {}", info.getDescription());
    }
    log.info("--");
  }

  @SneakyThrows
  private static String loadGifElementsForm() {
    final InputStream gifFormInputStream = GifActivityMain.class.getResourceAsStream("/gif.mml.xml");
    return IOUtils.toString(gifFormInputStream, StandardCharsets.UTF_8);
  }
}
