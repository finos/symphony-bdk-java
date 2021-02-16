package com.symphony.bdk.core.service.message.util;

import com.symphony.bdk.core.service.message.exception.MessageParserException;
import com.symphony.bdk.gen.api.model.V4Message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apiguardian.api.API;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Helper class for extracting entities inside an incoming {@link com.symphony.bdk.gen.api.model.V4Message} such as:
 * Mentions, Hashtags, Cashtags, Emojis.
 */
@Slf4j
@API(status = API.Status.EXPERIMENTAL)
public class MessageParser {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private MessageParser() {
  }

  /**
   * Parse data inside the message and returns a list containing the user ids corresponding to the users mentioned
   *
   * @param message incoming V4 message to be parsed
   * @return list of users ids that has been mentioned inside the message
   */
  public static List<Long> getMentions(V4Message message) throws MessageParserException {
    List<String> mentionsList = getTags(message, EntityTypeEnum.MENTION);
    return mentionsList.stream().map(Long::parseLong).collect(Collectors.toList());
  }

  /**
   * Parse data inside the message and returns a list containing the text of the hashtags found
   *
   * @param message incoming V4 message to be parsed
   * @return list of hashtags contained in the message
   */
  public static List<String> getHashtags(V4Message message) throws MessageParserException {
    return getTags(message, EntityTypeEnum.HASHTAG);
  }

  /**
   * Parse data inside the message and returns a list containing the text of the cashtags found
   *
   * @param message incoming V4 message to be parsed
   * @return list of cashtags contained in the message
   */
  public static List<String> getCashtags(V4Message message) throws MessageParserException {
    return getTags(message, EntityTypeEnum.CASHTAG);
  }

  /**
   * Parse data inside the message and returns a map containing the list of emojis found.
   * Key of the map are the annotation used to identify the emoji and the values are the their unicode.
   *
   * @param message incoming V4 message to be parsed
   * @return map of emojis contained in the message
   */
  public static Map<String, String> getEmojis(V4Message message) throws MessageParserException {
    return getEmojisList(message);
  }

  private static List<String> getTags(V4Message message, EntityTypeEnum type) throws MessageParserException {
    String data = message.getData();
    if (data == null || data.isEmpty()) {
      log.info("Data payload of message with id {} is empty, no entities found.", message.getMessageId());
      return Collections.emptyList();
    }
    JsonNode dataJson = getJsonNode(data);
    List<String> tags = new ArrayList<>();
    for (JsonNode node : dataJson) {
      if (isType(type, node)) {
        tags.add(node.get("id").get(0).get("value").asText());
      }
    }
    return tags;
  }

  private static Map<String, String> getEmojisList(V4Message message) throws MessageParserException {
    String data = message.getData();
    if (data == null || data.isEmpty()) {
      log.info("Data payload of message with id {} is empty, no entities found.", message.getMessageId());
      return Collections.emptyMap();
    }
    JsonNode dataJson = getJsonNode(data);
    Map<String, String> emojisMap = new HashMap<>();
    for (JsonNode node : dataJson) {
      if (isType(EntityTypeEnum.EMOJI, node)) {
        emojisMap.put(node.get("data").get("annotation").asText(), node.get("data").get("unicode").asText());
      }
    }
    return emojisMap;
  }

  private static JsonNode getJsonNode(String data) throws MessageParserException {
    JsonNode dataJson;
    try {
      dataJson = MAPPER.readTree(data);
    } catch (JsonProcessingException e) {
      throw new MessageParserException("Failed to extract payload from message data", e);
    }
    return dataJson;
  }

  private static boolean isType(EntityTypeEnum type, JsonNode node) {
    return type.getValue().equals(node.get("type").asText());
  }

  @API(status = API.Status.EXPERIMENTAL)
  public enum EntityTypeEnum {
    HASHTAG("org.symphonyoss.taxonomy"),
    CASHTAG("org.symphonyoss.fin.security"),
    MENTION("com.symphony.user.mention"),
    EMOJI("com.symphony.emoji");

    private final String value;

    EntityTypeEnum(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }

  }
}
