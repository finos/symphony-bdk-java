package com.symphony.ms.bot.sdk.command;

import static com.symphony.ms.bot.sdk.internal.symphony.model.StreamType.ROOM;

import com.symphony.ms.bot.sdk.internal.command.MultiResponseCommandHandler;
import com.symphony.ms.bot.sdk.internal.command.MultiResponseComposer;
import com.symphony.ms.bot.sdk.internal.command.model.BotCommand;
import com.symphony.ms.bot.sdk.internal.event.model.MessageAttachmentFile;
import com.symphony.ms.bot.sdk.internal.symphony.ConfigClient;
import com.symphony.ms.bot.sdk.internal.symphony.StreamsClient;
import com.symphony.ms.bot.sdk.internal.symphony.exception.SymphonyClientException;
import com.symphony.ms.bot.sdk.internal.symphony.model.SymphonyStream;
import com.symphony.ms.bot.sdk.internal.symphony.model.SymphonyUser;
import com.symphony.ms.bot.sdk.internal.symphony.model.UserAvatar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Sample command to broadcast a message to all bot rooms
 */
public class BroadcastMessageCommandHandler extends MultiResponseCommandHandler {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(BroadcastMessageCommandHandler.class);

  private static final String BROADCAST_MESSAGE_TITLE = "Broadcast message from ";

  private final StreamsClient streamsClient;
  private final ConfigClient configClient;

  public BroadcastMessageCommandHandler(StreamsClient streamsClient, ConfigClient configClient) {
    this.streamsClient = streamsClient;
    this.configClient = configClient;
  }

  @Override
  protected Predicate<String> getCommandMatcher() {
    return Pattern
        .compile("^@" + getBotName() + " /broadcast")
        .asPredicate();
  }

  @Override
  public void handle(BotCommand command, MultiResponseComposer multiResponseComposer) {
    String commandStreamId = command.getMessageEvent().getStreamId();

    List<SymphonyStream> botStreams = getBotActiveRooms();

    List<SymphonyStream> broadcastStreams = new ArrayList<>(botStreams);
    broadcastStreams.removeIf(stream -> stream.getStreamId().equals(commandStreamId));
    Set<String> broadcastStreamIds =
        broadcastStreams.stream().map(SymphonyStream::getStreamId).collect(Collectors.toSet());

    List<MessageAttachmentFile> attachments = new ArrayList<>();
    try {
      attachments = messageClient.downloadMessageAttachments(command.getMessageEvent());
    } catch (SymphonyClientException sce) {
      LOGGER.error("SymphonyClientException thrown on AttachmentCommandHandler", sce);
    }

    multiResponseComposer.compose()
        .withTemplateFile("list", parameterForListTemplate(broadcastStreams))
        .toStreams(commandStreamId)
        .withTemplateFile("simple", parameterForSimpleTemplate(command))
        .withAttachments(attachments)
        .toStreams(broadcastStreamIds)
        .complete();
  }

  public String getCommandMessage(BotCommand command) {
    String rawMessage = command.getMessageEvent().getMessage();
    String commandMessage = "";
    Matcher matcher =
        Pattern.compile("@" + getBotName() + "\\s\\/broadcast[\\s]+([\\s\\S]*[^\\s])")
            .matcher(rawMessage);
    if (matcher.find()) {
      commandMessage = matcher.group(1);
    }
    return commandMessage;
  }

  private List<SymphonyStream> getBotActiveRooms() {
    try {
      return streamsClient.getUserStreams(Collections.singletonList(ROOM), false);
    } catch (SymphonyClientException e) {
      LOGGER.error("Exception thrown getting bot active rooms through streams client. {}", e);
      return new ArrayList<>();
    }
  }

  private Map<String, Map> parameterForListTemplate(List<SymphonyStream> broadcastStreams) {
    Map<String, Object> message = new HashMap<>();
    message.put("title", "Message sent to " + broadcastStreams.size() + " other room(s):");
    List<String> content = new ArrayList<>();
    for (SymphonyStream stream : broadcastStreams) {
      content.add(stream.getRoomName());
    }
    message.put("content", content);
    Map<String, Map> templateParameter = new HashMap<>();
    templateParameter.put("message", message);
    return templateParameter;
  }

  private Map<String, Map> parameterForSimpleTemplate(BotCommand command) {
    String userDisplayName = command.getUser().getDisplayName();
    Long userId = command.getUser().getUserId();
    String commandMessage = getCommandMessage(command);

    Map<String, String> messageParameter = new HashMap<>();
    messageParameter.put("title", BROADCAST_MESSAGE_TITLE + userDisplayName);
    messageParameter.put("content", commandMessage);
    messageParameter.put("icon", getIcon(userId));
    Map<String, Map> templateParameter = new HashMap<>();
    templateParameter.put("message", messageParameter);
    return templateParameter;
  }

  private String getIcon(Long userId) {
    String podHost = configClient.getPodBaseUrl();
    if (podHost == null || podHost.isEmpty()) {
      LOGGER.info("No icon used. PodHost configuration is disabled");
      return null;
    }
    SymphonyUser user = null;
    try {
      user = usersClient.getUserFromId(userId, true);
    } catch (SymphonyClientException e) {
      LOGGER.info("No icon used. {}", e.getMessage());
      return null;
    }
    List<UserAvatar> avatars = user.getAvatars();
    if (avatars == null || avatars.isEmpty()) {
      LOGGER.info("No icon used. User has no avatar");
      return null;
    }
    return avatars.stream()
        .map(avatar -> podHost + avatar.getUrl().replace("..", ""))
        .findFirst()
        .orElse(null);
  }

}
