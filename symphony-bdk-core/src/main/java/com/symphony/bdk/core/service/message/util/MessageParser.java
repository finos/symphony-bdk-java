package com.symphony.bdk.core.service.message.util;

import com.symphony.bdk.core.service.message.exception.MessageParserException;
import com.symphony.bdk.gen.api.model.V4Message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
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
@API(status = API.Status.EXPERIMENTAL)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MessageParser {

  private static final ObjectMapper MAPPER = new ObjectMapper();

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
    String data = message.getData();
    if (data == null || data.isEmpty()) {
      return Collections.emptyMap();
    }
    JsonNode dataJson = getJsonNode(data);
    Map<String, String> emojisMap = new HashMap<>();
    for (JsonNode node : dataJson) {
      if (isType(EntityTypeEnum.EMOJI, node) && !node.findPath("annotation").isMissingNode() &&
          !node.findPath("unicode").isMissingNode()) {
        emojisMap.put(node.findPath("annotation").asText(), node.findPath("unicode").asText());
      }
    }
    return emojisMap;
  }

  private static List<String> getTags(V4Message message, EntityTypeEnum type) throws MessageParserException {
    String data = message.getData();
    if (data == null || data.isEmpty()) {
      return Collections.emptyList();
    }
    JsonNode dataJson = getJsonNode(data);
    List<String> tags = new ArrayList<>();
    for (JsonNode node : dataJson) {
      if (isType(type, node) && !node.findPath("value").isMissingNode()) {
        tags.add(node.findPath("value").asText());
      }
    }
    return tags;
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

  @API(status = API.Status.INTERNAL)
  private enum EntityTypeEnum {
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
