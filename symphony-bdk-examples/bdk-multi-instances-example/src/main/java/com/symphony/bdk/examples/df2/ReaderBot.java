package com.symphony.bdk.examples.df2;

import static com.symphony.bdk.core.config.BdkConfigLoader.loadFromSymphonyDir;

import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.service.datafeed.RealTimeEventListener;
import com.symphony.bdk.core.service.message.exception.PresentationMLParserException;
import com.symphony.bdk.core.service.message.util.PresentationMLParser;
import com.symphony.bdk.gen.api.model.V4Initiator;
import com.symphony.bdk.gen.api.model.V4MessageSent;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * A simple bot (named benchmark-reader), reading a datafeed and replying to a message with the same content.
 * The goal is to run multiple instances of it, hence it adds a unique id and uses a random color in the reply.
 */
@Slf4j
public class ReaderBot {

  public static void main(String[] args) throws Exception {

    Config hzConfig = Config.load();
    hzConfig.getNetworkConfig().getInterfaces().setEnabled(true);
    // if you are connected to a VPN, you'll need this for multicast discovery to work, set it to your local network IP
    hzConfig.getNetworkConfig().getInterfaces().addInterface("192.168.1.250");
    HazelcastInstance hzInstance = Hazelcast.newHazelcastInstance(hzConfig);

    // a distributed cache for already processed events, entries are added with a TTL
    IMap<String, String> processedEvents = hzInstance.getMap("processed_events");

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

          log.info("message {}, keys {}", event.getMessage().getMessageId(), processedEvents.keySet());

          // we don't want multiple instances of a bot to reply to the same thread at the same time
          // ideally locks should be destroyed at some point to release memory
          Lock lock = hzInstance.getCPSubsystem().getLock(event.getMessage().getStream().getStreamId());
          lock.lock();
          try {
            // checking that message has not already been processed before replying
            if (processedEvents.containsKey(event.getMessage().getMessageId())) {
              log.info("Message already processed!");
              return;
            }
            bdk.messages().send(event.getMessage().getStream(),
                String.format("<messageML><span style=\"color: %s;\">%s <b>%s</b></span></messageML>",
                    color, textContent, id));
            processedEvents.put(event.getMessage().getMessageId(), "processed", 1, TimeUnit.MINUTES);

          } finally {
            lock.unlock();
          }
        } catch (PresentationMLParserException e) {
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
    return String.format("#%06x", nextInt);
  }
}
