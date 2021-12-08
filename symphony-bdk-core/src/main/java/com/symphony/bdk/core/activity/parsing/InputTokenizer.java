package com.symphony.bdk.core.activity.parsing;

import com.symphony.bdk.gen.api.model.V4Message;

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

  private static DocumentBuilder DOCUMENT_BUILDER = initBuilder();

  @SneakyThrows
  private static DocumentBuilder initBuilder() {
    final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    return factory.newDocumentBuilder();
  }

  @SneakyThrows
  public static List<InputToken> getTokens(V4Message message) {
    final Document doc = DOCUMENT_BUILDER.parse(
        new ByteArrayInputStream(message.getMessage().getBytes(StandardCharsets.UTF_8)));

    return getTokens(doc);
  }

  private static List<InputToken> getTokens(Document doc) {
    List<InputToken> tokens = new ArrayList<>();
    final StringBuffer buf = new StringBuffer();

    tokenize(doc, tokens, buf);
    tokenizeRegularContent(tokens, buf); // tokenize what is left in the buffer

    return tokens;
  }

  private static void tokenize(Node node, List<InputToken> tokens, StringBuffer buf) {
    if (isEntityNode(node)) {
      tokenizeEntityNode(node, tokens, buf);
    } else {
      tokenizeRegularNode(node, tokens, buf);
    }
  }

  private static boolean isEntityNode(Node node) {
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
    return true;
  }

  private static void tokenizeEntityNode(Node node, List<InputToken> tokens, StringBuffer buf) {
    tokenizeRegularContent(tokens, buf); // tokenize buffer as usual
    buf.delete(0, buf.length()); // clear the buffer
    tokens.add(new InputToken(node.getTextContent(), true)); // we assume a span node has only text inside
  }

  private static void tokenizeRegularContent(List<InputToken> tokens, StringBuffer buf) {
    Arrays.stream(buf.toString().trim().split("\\s+"))
        .filter(StringUtils::isNotBlank)
        .map(InputToken::new)
        .forEachOrdered(t -> tokens.add(t));
  }

  private static void tokenizeRegularNode(Node node, List<InputToken> tokens, StringBuffer buf) {
    final String nodeValue = node.getNodeValue();
    if (nodeValue != null) {
      buf.append(nodeValue);
    }

    for (int i = 0; i < node.getChildNodes().getLength(); i++) {
      tokenize(node.getChildNodes().item(i), tokens, buf);
    }
  }
}
