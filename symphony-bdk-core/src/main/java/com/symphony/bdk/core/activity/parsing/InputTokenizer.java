package com.symphony.bdk.core.activity.parsing;

import static org.apache.commons.lang3.StringUtils.isBlank;

import com.symphony.bdk.core.service.message.util.EntityTypeEnum;
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

  private static final String DATA_ENTITY_ID = "data-entity-id";
  private static final String TYPE = "type";
  private static final String SPAN = "span";
  private static final String CLASS = "class";
  private static final String ENTITY = "entity";
  private static final String ID = "id";
  private static final String VALUE = "value";

  private static final String SYMPHONY_USER_ID_TYPE = "com.symphony.user.userId";
  private static final String CASHTAG_VALUE_TYPE = "org.symphonyoss.fin.security.id.ticker";

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
    if (isEntityNode(node, EntityTypeEnum.MENTION)) {
      tokenizeMentionNode(node);
    } else if (isEntityNode(node, EntityTypeEnum.CASHTAG)) {
      tokenizeCashtagNode(node);
    } else {
      tokenizeRegularNode(node);
    }
  }

  private boolean isEntityNode(Node node, EntityTypeEnum type) {
    if (!isEntityNode(node)) {
      return false;
    }
    String entityId = node.getAttributes().getNamedItem(DATA_ENTITY_ID).getNodeValue();
    return dataNode.get(entityId).get(TYPE).asText().equals(type.getValue());
  }

  private boolean isEntityNode(Node node) {
    if (!StringUtils.equals(node.getNodeName(), SPAN)) {
      return false;
    }

    final Node classAttribute = node.getAttributes().getNamedItem(CLASS);
    if (classAttribute == null || !StringUtils.equals(classAttribute.getNodeValue(), ENTITY)) {
      return false;
    }

    final Node entityIdAttribute = node.getAttributes().getNamedItem(DATA_ENTITY_ID);
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

  private void tokenizeCashtagNode(Node node) {
    tokenizeRegularContent(); // tokenize buffer as usual
    buffer.delete(0, buffer.length()); // clear the buffer

    tokens.add(new CashtagInputToken(node.getTextContent(), getCashtagValue(node)));
  }

  private String getCashtagValue(Node node) {
    String entityId = node.getAttributes().getNamedItem(DATA_ENTITY_ID).getNodeValue();
    for (JsonNode id : dataNode.get(entityId).get(ID)) {
      if (id.get(TYPE).asText().equals(CASHTAG_VALUE_TYPE)) {
        return id.get(VALUE).asText();
      }
    }
    return null;
  }

  private Long getUserIdInMention(Node node) {
    String entityId = node.getAttributes().getNamedItem(DATA_ENTITY_ID).getNodeValue();
    for (JsonNode id : dataNode.get(entityId).get(ID)) {
      if (id.get(TYPE).asText().equals(SYMPHONY_USER_ID_TYPE)) {
        return Long.parseLong(id.get(VALUE).asText());
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
