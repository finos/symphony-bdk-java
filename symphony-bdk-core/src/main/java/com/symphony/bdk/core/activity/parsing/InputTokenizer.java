package com.symphony.bdk.core.activity.parsing;

import static org.apache.commons.lang3.StringUtils.isBlank;

import com.symphony.bdk.gen.api.model.V4Message;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apiguardian.api.API;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

@API(status = API.Status.INTERNAL)
@Slf4j
public class InputTokenizer {

  private static final DocumentBuilder DOCUMENT_BUILDER = initBuilder();
  private static final ObjectMapper MAPPER = new ObjectMapper();

  @SneakyThrows
  private static DocumentBuilder initBuilder() {
    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    return factory.newDocumentBuilder();
  }

  private Document document;
  private JsonNode dataNode;
  private List<InputToken<?>> tokens;
  private StringBuffer buffer;

  @SneakyThrows
  public InputTokenizer(V4Message message) {
    this.document = DOCUMENT_BUILDER.parse(
        new ByteArrayInputStream(message.getMessage().getBytes(StandardCharsets.UTF_8)));
    String jsonData = isBlank(message.getData()) ? "{}" : message.getData();
    this.dataNode = MAPPER.readTree(jsonData);
    this.tokens = new ArrayList<>();
    this.buffer = new StringBuffer();

    tokenize();
  }

  public List<InputToken<?>> getTokens() {
    return tokens;
  }

  private void tokenize() {
    tokenize(document); // begin from the root node
    tokenizeRegularContent(); // tokenize what is left in the buffer
  }

  private void tokenize(Node node) {
    if (isMentionNode(node)) {
      tokenizeMentionNode(node);
    } else {
      tokenizeRegularNode(node);
    }
  }

  private boolean isMentionNode(Node node) {
    if (!isEntityNode(node)) {
      return false;
    }
    String entityId = node.getAttributes().getNamedItem("data-entity-id").getNodeValue();
    return dataNode.get(entityId).get("type").asText().equals("com.symphony.user.mention");
  }

  private boolean isEntityNode(Node node) {
    if (!StringUtils.equals(node.getNodeName(), "span")) {
      return false;
    }

    final Node classAttribute = node.getAttributes().getNamedItem("class");
    if (classAttribute == null || !StringUtils.equals(classAttribute.getNodeValue(), "entity")) {
      return false;
    }

    final Node entityIdAttribute = node.getAttributes().getNamedItem("data-entity-id");
    if (entityIdAttribute == null) {
      return false;
    }

    String entityId = entityIdAttribute.getNodeValue();
    if (!dataNode.has(entityId)) {
      return false;
    }

    return true;
  }

  private void tokenizeMentionNode(Node node) {
    tokenizeRegularContent(); // tokenize buffer as usual
    buffer.delete(0, buffer.length()); // clear the buffer

    tokens.add(new MentionInputToken(node.getTextContent(), getUserIdInMention(node)));
  }

  private Long getUserIdInMention(Node node) {
    String entityId = node.getAttributes().getNamedItem("data-entity-id").getNodeValue();
    for (JsonNode id : dataNode.get(entityId).get("id")) {
      if (id.get("type").asText().equals("com.symphony.user.userId")) {
        return Long.parseLong(id.get("value").asText());
      }
    }
    return null;
  }

  private void tokenizeRegularContent() {
    Arrays.stream(buffer.toString().trim().split("\\s+"))
        .filter(StringUtils::isNotBlank)
        .map(StringInputToken::new)
        .forEachOrdered(t -> tokens.add(t));
  }

  private void tokenizeRegularNode(Node node) {
    final String nodeValue = node.getNodeValue();
    if (nodeValue != null) {
      buffer.append(nodeValue);
    }

    for (int i = 0; i < node.getChildNodes().getLength(); i++) {
      tokenize(node.getChildNodes().item(i));
    }
  }
}
