package com.symphony.bdk.examples.df2;

import static com.symphony.bdk.core.config.BdkConfigLoader.loadFromSymphonyDir;

import com.symphony.bdk.core.SymphonyBdk;
import com.symphony.bdk.core.config.model.BdkConfig;
import com.symphony.bdk.core.service.message.MessageService;
import com.symphony.bdk.core.service.message.exception.PresentationMLParserException;
import com.symphony.bdk.core.service.message.util.PresentationMLParser;
import com.symphony.bdk.core.service.pagination.model.PaginationAttribute;
import com.symphony.bdk.gen.api.model.V3RoomAttributes;
import com.symphony.bdk.gen.api.model.V3RoomDetail;
import com.symphony.bdk.gen.api.model.V4Message;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A bot (named benchmark-injector) sending messages to a room and checking that all messages have been replied to.
 *
 */
@Slf4j
public class InjectorBot {
  // the bot replying to messages
  private static final long REPLIER_BOT_ID = 13056700582120L;
  // A real user id so you can visualize the test with the client (user will be added to the test room)
  private static final long ADMIN_ID = 13056700579841L;

  // Number of messages to test
  private static final int NB_MESSAGES = 10;

  public static void main(String[] args) throws Exception {
    BdkConfig config = loadFromSymphonyDir("config.yaml");
    config.getBot().setUsername("benchmark-injector");
    final SymphonyBdk bdk = new SymphonyBdk(config);

    Instant startTest = Instant.now();

    String streamId = createTestRoom(bdk, startTest);
    sendMessages(bdk, streamId);
    checkAllMessagesHaveBeenRepliedTo(bdk, startTest, streamId);
  }

  private static String createTestRoom(SymphonyBdk bdk, Instant startTest) {
    V3RoomAttributes roomAttributes = new V3RoomAttributes();
    roomAttributes.name("Test-" + startTest);
    roomAttributes.description("Test-" + startTest);
    roomAttributes.setPublic(true);
    roomAttributes.discoverable(true);
    V3RoomDetail stream = bdk.streams().create(roomAttributes);
    String streamId = stream.getRoomSystemInfo().getId();
    bdk.streams().addMemberToRoom(REPLIER_BOT_ID, streamId);
    bdk.streams().addMemberToRoom(ADMIN_ID, streamId);
    log.info("Create stream {}", streamId);
    return streamId;
  }

  private static void sendMessages(SymphonyBdk bdk, String streamId) {
    MessageService messages = bdk.messages();
    for (int i = 0; i < NB_MESSAGES; i++) {
      messages.send(streamId, String.format("<messageML><b>%s</b> %s</messageML>", i, Instant.now()));
    }
  }

  private static void checkAllMessagesHaveBeenRepliedTo(SymphonyBdk bdk, Instant startTest, String streamId)
      throws PresentationMLParserException, InterruptedException {
    MessageService messages = bdk.messages();
    PaginationAttribute pagination = new PaginationAttribute(0, NB_MESSAGES * 2);
    List<V4Message> allMessages = messages.listMessages(streamId, startTest, pagination);
    while (allMessages.size() != NB_MESSAGES * 2) {
      checkMessages(bdk, allMessages);
      log.info("Not seeing all messages yet({}/{}), retrying...", allMessages.size(), NB_MESSAGES * 2);
      Thread.sleep(100); // wait a bit before re-checking
      allMessages = messages.listMessages(streamId, startTest);
    }

    log.info("Looks like we got all messages, checking if all have been replied to...");
    checkMessages(bdk, allMessages);

    messages.send(streamId, "<messageML>Benchmark finished!</messageML>");
  }

  private static void checkMessages(SymphonyBdk bdk, List<V4Message> allMessages) throws PresentationMLParserException {
    List<V4Message> messagesSent = allMessages.stream()
        .filter(m -> m.getUser().getUserId().equals(bdk.botInfo().getId()))
        .collect(Collectors.toList());
    List<V4Message> messagesReplied = allMessages.stream()
        .filter(m -> m.getUser().getUserId().equals(REPLIER_BOT_ID))
        .collect(Collectors.toList());

    Map<String, List<V4Message>> sentAndReplied = new HashMap<>();
    for (V4Message v4Message : messagesSent) {
      String id = PresentationMLParser.getTextContent(v4Message.getMessage()).split("\\ ")[0];
      List<V4Message> matchingMessages = messagesReplied.stream()
          .filter(m -> {
            try {
              return PresentationMLParser.getTextContent(m.getMessage()).startsWith(id + " ");
            } catch (PresentationMLParserException e) {
              throw new RuntimeException(e);
            }
          }).collect(Collectors.toList());
      sentAndReplied.put(id, matchingMessages);
    }
    List<String> notYetReplied = sentAndReplied.entrySet().stream()
        .filter(e -> e.getValue().size() != 1)
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());

    log.info("{} not yet replied to", notYetReplied);
  }
}
