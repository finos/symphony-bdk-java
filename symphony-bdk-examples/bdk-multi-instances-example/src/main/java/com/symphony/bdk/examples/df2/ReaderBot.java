package com.symphony.bdk.examples.df2;

import static com.symphony.bdk.core.config.BdkConfigLoader.loadFromSymphonyDir;

import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.core.service.message.exception.PresentationMLParserException;
import com.symphony.bdk.core.service.message.util.PresentationMLParser;
import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4MessageSent;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.Random;
import java.util.UUID;

/**
 * A simple bot (named benchmark-reader), reading a datafeed and replying to a message with the same content.
 * The goal is to run multiple instances of it, hence it adds a unique id and uses a random color in the reply.
 */
@Slf4j
public class ReaderBot {

  public static void main(String[] args) throws Exception {
    String id = UUID.randomUUID().toString();
    MDC.put("BOT_ID", id);
    log.info("Running bot {}", id);

    String color = randomColor();

    BdkConfig config = loadFromSymphonyDir("config.yaml");
    config.getBot().setUsername("benchmark-reader");
    final SymphonyBdk bdk = new SymphonyBdk(config);

    bdk.datafeed().subscribe(new RealTimeEventListener() {
      @Override
      public void onMessageSent(V4Initiator initiator, V4MessageSent event) {
        try {
          String textContent = PresentationMLParser.getTextContent(event.getMessage().getMessage());

          if (textContent.contains("sleep")
              && event.getMessage().getTimestamp() > System.currentTimeMillis() - 30 * 1000) {
            log.info("Sleeping");
            Thread.sleep(30_000);
          }

          if (textContent.contains("break")
              && event.getMessage().getTimestamp() > System.currentTimeMillis() - 30 * 1000) {
            log.info("Breaking");
            throw new RuntimeException("break it");
          }

          bdk.messages().send(event.getMessage().getStream(),
              String.format("<messageML><span style=\"color: %s;\">%s <b>%s</b></span></messageML>",
                  color, textContent, id));

        } catch (PresentationMLParserException | InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    });

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      log.info("Stopping Datafeed...");
      bdk.datafeed().stop();
    }));

    bdk.datafeed().start();
  }

  private static String randomColor() {
    Random random = new Random();
    int nextInt = random.nextInt(0xffffff + 1);
    String color = String.format("#%06x", nextInt);
    return color;
  }
}
